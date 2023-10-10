package com.hxw.partnermatch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.mapper.UserMapper;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.TagsSearchRequest;
import com.hxw.partnermatch.model.responses.TagDistance;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.utils.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hxw.partnermatch.utils.RedisConstant.*;
import static com.hxw.partnermatch.utils.UserConstant.USER_LOGIN_STATE;
import static com.hxw.partnermatch.utils.UserConstant.ADMIN_ROLE;


/**
* @author 81086
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-09-08 16:49:05
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //密码加密加盐
    private static final String SALT="hxw";


    @Override
    public Result register(User user,String checkPassword) {
        //1.校验非空
        if(StringUtils.isEmpty(user.getAccount()) || StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(checkPassword)){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"账号或密码为空");
//            return Result.build(null, ResultCodeEnum.IS_NULL);
        }
        //2.校验账户长度>=6
        if(user.getAccount().length()<6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号长度小于6");
//            return Result.build(null,ResultCodeEnum.IS_NOT_LONG);
        }
        //3.校验密码长度>=6
        if(user.getPassword().length()<6 || checkPassword.length()<6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码长度小于6");
//            return Result.build(null,ResultCodeEnum.IS_NOT_LONG);
        }
        //4.账户不能包含特殊字符
        String regex = "^[a-zA-Z0-9_.@]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user.getAccount());
        if (!matcher.matches()) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号包含特殊字符");
//            return Result.build(null,ResultCodeEnum.USERNAME_ERROR);
        }
        //5.校验密码相同
        if(!user.getPassword().equals(checkPassword)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"两次输入密码不一致");
//            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        }
        //6.校验账户名不能重复，放在后面如果前面出错就不用查询了
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,user.getAccount());
        Long count = userMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号已存在");
        }
        //7.密码加密
        user.setPassword(MD5Util.encrypt(SALT+user.getPassword()));
        //随机生成默认头像
        Random random = new Random();
        int min = 1;
        int max = 100;
        int num = random.nextInt(max - min + 1) + min;
        user.setAvatar(String.format("https://api.multiavatar.com/%s.png",num));
        //保存数据
        int result = userMapper.insert(user);
        if(result==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败，注册失败");
        }
        return Result.ok(null,"注册成功");
    }


    @Override
    public Result login(User user, HttpServletRequest request) {
        //1.验证非空
        if(StringUtils.isEmpty(user.getAccount()) || StringUtils.isEmpty(user.getPassword())){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"账号或密码为空");
//            return Result.build(null, ResultCodeEnum.IS_NULL);
        }
        //2.校验账户长度>=6
        if(user.getAccount().length()<6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号长度小于6");
//            return Result.build(null,ResultCodeEnum.IS_NOT_LONG);
        }
        //3.校验密码长度>=6
        if(user.getPassword().length()<6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码长度小于6");
//            return Result.build(null,ResultCodeEnum.IS_NOT_LONG);
        }
        //4.账户不能包含特殊字符
        String regex = "^[a-zA-Z0-9_.@]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user.getAccount());
        if (!matcher.matches()) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号包含特殊字符");
//            return Result.build(null,ResultCodeEnum.USERNAME_ERROR);
        }
        //5.校验密码相同
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,user.getAccount());
        User loginUser=userMapper.selectOne(queryWrapper);
        if(loginUser==null){ //用户不存在
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号不存在");
//            return Result.build(null,ResultCodeEnum.USERNAME_ERROR);
        }
        if(!loginUser.getPassword().equals(MD5Util.encrypt(SALT+user.getPassword()))){ //密码错误
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码错误");
//            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        }
        //6.返回用户数据，用户脱敏
        loginUser.setPassword(""); //不返回密码
        //7.在session中记录用户的登录态
        //request.getSession().setAttribute(USER_LOGIN_STATE,loginUser);
        //把用户数据保存在redis中
        //随机生成token
        String token= UUID.randomUUID().toString(true);
//        Map<String,Object> userMap=BeanUtil.beanToMap(user);
//        redisTemplate.opsForHash().putAll(LOGIN_USER_TOKEN_KEY+token,userMap);
        redisTemplate.opsForValue().set(LOGIN_USER_TOKEN_KEY+token,loginUser);
        //设置有效期
        redisTemplate.expire(LOGIN_USER_TOKEN_KEY+token,LOGIN_USER_TOKEN_TTL,TimeUnit.MINUTES);
        return Result.ok(token,"登录成功");
    }

    @Override
    public Result sendSms(String phone) {
        //1.检验电话是否为空
        if(phone==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"电话不能为空");
        }
        //2.检验电话是否存在
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user=userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"电话不存在");
        }
        //3.生成验证码
        Random random = new Random();
        int min = 1000;  // 最小值为1000
        int max = 9999;  // 最大值为9999
        int code = random.nextInt(max - min + 1) + min;
        //4.保存验证码到redis中，设置过期时间
        redisTemplate.opsForValue().set(LOGIN_CODE_KEY+phone,code,LOGIN_CODE_TTL,TimeUnit.MINUTES);
        System.out.println("登录验证码为："+code);
        return Result.ok(code,"验证码发送成功");
    }

    @Override
    public Result loginSms(String phone, Integer code,HttpServletRequest request) {
        //0.检验参数是否为空
        if(phone==null || code==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"电话或验证码为空");
        }
        //1.检验电话是否存在
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user=userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"电话不存在");
        }
        //2.验证验证码是否正确，从redis中获取
        Object o = redisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if(o==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"验证码已过期");
        }
        int code_redis=(Integer) o;
        if(code!=code_redis){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"验证码错误");
        }
        //3.返回用户数据
        user.setPassword(""); //不返回密码
        //3.记录用户的登录态
        //request.getSession().setAttribute(USER_LOGIN_STATE,user);
        //把用户数据保存在redis中
        //随机生成token
        String token= UUID.randomUUID().toString(true);
//        Map<String,Object> userMap=BeanUtil.beanToMap(user);
//        redisTemplate.opsForHash().putAll(LOGIN_USER_TOKEN_KEY+token,userMap);
        redisTemplate.opsForValue().set(LOGIN_USER_TOKEN_KEY+token,user);
        //设置有效期
        redisTemplate.expire(LOGIN_USER_TOKEN_KEY+token,LOGIN_USER_TOKEN_TTL,TimeUnit.MINUTES);
        return Result.ok(token,"登录成功");

    }

    @Override
    public Result logout(HttpServletRequest request) {
//        request.getSession().removeAttribute(USER_LOGIN_STATE);
        //清除redis和线程
        UserHolder.removeUser();
        String token=request.getHeader("Authorization");
        redisTemplate.delete(LOGIN_USER_TOKEN_KEY + token);
        return Result.ok(null,"退出成功");
    }

    @Override
    public Result searchByTag(TagsSearchRequest tagsSearchRequest) {
        if(tagsSearchRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        String tags = tagsSearchRequest.getTags();
        Gson gson = new Gson();
        // 使用Gson将JSON字符串转换为List<String>
        List<String> tagsList = gson.fromJson(tags, new TypeToken<List<String>>(){}.getType());
        //空集合返回所有用户
        //第一种方法：数据库查询
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        for(String tag: tagsList){
            queryWrapper=queryWrapper.like(User::getTags, tag);
        }
        Page<User> page=new Page<>(tagsSearchRequest.getPageNum(),tagsSearchRequest.getPageSize());
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        List<User> userList=userPage.getRecords();
        //数据脱敏
        List<User> safeUserList=getSafeUserList(userList);
        //第二种方法，内存中查询
        //先查询所有用户
//        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
//        List<User> userList=userMapper.selectList(queryWrapper);
//        Gson gson=new Gson();
//        //在内存中判断是否包含要求的标签
//        List<User> filteredUserList=userList.stream().filter(user -> {
//            String tagsStr=user.getTags();
//            if(org.apache.commons.lang3.StringUtils.isBlank(tagsStr)){
//                return false;
//            }
//            Set<String> tempTagNameSet=gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
//            for(String tagName: tags){
//                if(!tempTagNameSet.contains(tagName)){
//                    return false;
//                }
//            }
//            return true;
//        }).collect(Collectors.toList()); // 使用collect终止流操作，将结果收集到新的列表中;
        return Result.ok(safeUserList);
    }

    @Override
    public Result updateUser(User user,HttpServletRequest request) {
        //1.检验是否为空
        if(user==null ){
            throw new BusinessException(ResultCodeEnum.IS_NULL);
        }
        //2.校验权限
        //管理员可以修改其他人的信息
        //自己可以修改自己的信息
        User currentUser=getCurrentUser(request);
        if(currentUser==null){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"未登录");
        }
        if(!isAdmin(request) && !Objects.equals(user.getId(), currentUser.getId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有修改权限");
        }
        //3.检验是否修改的是标签，如果是，需要更新推荐用户列表，Cache Aside Pattern策略删除缓存
        if(user.getTags()!=null){
            String redisKey=String.format(RECOMMEND_USER_KEY+"%d",currentUser.getId());
            redisTemplate.delete(redisKey);
        }
        int result = userMapper.updateById(user);
        if(result==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"修改失败");
        }
        return Result.ok(result,"修改成功");
    }


    @Override
    public User getCurrentUser(HttpServletRequest request) {
//        if(request==null){
//            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR);
//        }
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user=(User) userObj;
//        if(user==null){
//            throw new BusinessException(ResultCodeEnum.NO_AUTH,"未登录");
//        }
//        Long id = user.getId();
//        User userInfo = userMapper.selectById(id);
//        userInfo.setPassword("");
//        return userInfo;
//        String token=request.getHeader("Authorization");
//        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
//            throw new BusinessException(ResultCodeEnum.NO_AUTH,"未登录");
//        }
//        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(LOGIN_USER_TOKEN_KEY + token);
//        if(userMap.isEmpty()){
//            throw new BusinessException(ResultCodeEnum.NO_AUTH,"未登录");
//        }
//        User user = BeanUtil.fillBeanWithMap(userMap, new User(), false);
//        user.setPassword("");
//        return user;
        //修改为从线程中获取
        User user=UserHolder.getUser();
        if(user==null){
            throw new BusinessException(ResultCodeEnum.NO_AUTH, "未登录");
        }
        return user;
    }

    @Override
    public Result recommendUsers(TagsSearchRequest tagsSearchRequest,HttpServletRequest request) {
        if(tagsSearchRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //对于数据量庞大，查询需要花费较长时间时，使用缓存
        //不同用户展现的数据不同，用redis的Key-Value存储在缓存中
        User currentUser=getCurrentUser(request);
        String redisKey=String.format(RECOMMEND_USER_KEY+"%d",currentUser.getId());
        ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
        List<Long> userIdList;
        //如果有缓存，直接读取缓存
        List<Long> similarUserIdList=(List<Long>) valueOperations.get(redisKey);
        if(similarUserIdList!=null){
            userIdList=similarUserIdList;
        }
        else{
            //如果没有缓存，从数据库中查询，并存入缓存
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.select(User::getId,User::getTags); //只选择两列，提高查询性能
            List<User> userList=this.list(queryWrapper);
            String tags=currentUser.getTags();
            Gson gson=new Gson();
            List<String> tagList=gson.fromJson(tags, new TypeToken<List<String>>(){}.getType());
            //维护一个长度为10的优先队列
    //        int queueLength=100;
            PriorityQueue<TagDistance> tagDistanceList=new PriorityQueue<>(new TagDistanceComparator());
            //计算相似度
            for (User userIdTags : userList) {
                String userTags = userIdTags.getTags();
                Long userId=userIdTags.getId();
                //剔除当前用户
                if (Objects.equals(userIdTags.getId(), currentUser.getId())) {
                    continue;
                }
                List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>(){}.getType());
                //得到编辑距离，大根堆删队顶顶最大元素，最后得到编辑距离最小的10个用户
                //todo:编辑距离存在缺陷，当两个标签的位置顺序不对时，需要花费更多的距离调整，是否考虑顺序的影响
                int editDistance = Algorithm.editDistance(userTagList, tagList);
                //查询
                tagDistanceList.offer(new TagDistance(userId, editDistance));
    //            if (tagDistanceList.size() > queueLength) {
    //                tagDistanceList.poll(); //删除队顶元素
    //            }
            }
            //最终正序排列的userIdList
            int size=tagDistanceList.size();
            Long[] finalUserIdList=new Long[size];
            int i=size-1;
            while (!tagDistanceList.isEmpty()) {
                TagDistance tagDistance = tagDistanceList.poll();
                Long userId=tagDistance.getUserId();
                finalUserIdList[i]=userId;
                i--;
            }
            userIdList = Arrays.asList(finalUserIdList);
            //保存在缓存中
            try{
                //注意设置缓存过期时间，redis内存不能无限期增加
                valueOperations.set(redisKey,userIdList,30000, TimeUnit.MILLISECONDS);
            } catch (Exception e){
                log.error("redis set key error",e);
            }
        }
        //根据userIdList分页查询user
        Integer pageNum = tagsSearchRequest.getPageNum();
        Integer pageSize = tagsSearchRequest.getPageSize();
        List<Long> subIdList;
        if(pageSize * (pageNum - 1)<userIdList.size()){
            subIdList = userIdList.subList(pageSize * (pageNum - 1), Math.min(userIdList.size(),pageSize * (pageNum - 1) + pageSize));
        } else {
            subIdList = null;
        }
        if(subIdList==null){
            return Result.ok(null);
        }
        else{
            List<User> result = userMapper.selectBatchIds(subIdList);
            //按照距离顺序排列
            result.sort(Comparator.comparingLong(user -> subIdList.indexOf(user.getId())));
            return Result.ok(getSafeUserList(result));
        }
    }

    @Override
    public List<User> getSafeUserList(List<User> userList) {
        return userList.stream().map(user -> {
            user.setPassword("");
            return user;
        }).toList();
    }

    @Override
    public boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) userObj;
        if(user==null || user.getUserRole()!=ADMIN_ROLE){
            return false;
        }
        return true;
    }



}




