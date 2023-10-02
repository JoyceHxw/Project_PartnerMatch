package com.hxw.partnermatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.mapper.TeamMapper;
import com.hxw.partnermatch.model.Tag;
import com.hxw.partnermatch.model.Team;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.UserTeam;
import com.hxw.partnermatch.model.requests.JoinTeamRequest;
import com.hxw.partnermatch.model.requests.TeamSearchRequest;
import com.hxw.partnermatch.service.TeamService;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.service.UserTeamService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
* @author 81086
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2023-09-16 17:53:05
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private TeamMapper teamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) //数据库事务回滚
    public Result buildTeam(Team team, HttpServletRequest request) {
        //1.请求参数是否为空
        if(team==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"队伍信息为空");
        }
        //2.是否登录
        User user=userService.getCurrentUser(request);
        //3.队伍人数>1且<=20，注意参数可能为空
        int maxNum= Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1 || maxNum>20){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍人数不符合要求");
        }
        //4.队伍标题<=20
        String name=team.getName();
        if(StringUtils.isBlank(name) || name.length()>20){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍名称不符合要求");
        }
        //5.描述<=512
        String description =team.getDescription();
        if(StringUtils.isBlank(description) || description.length()>512){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍描述不符合要求");
        }
        //6.status是否公开或加密，不传默认为0（公开）
        int status=Optional.ofNullable(team.getStatus()).orElse(0);
        if(status==2){
            if(StringUtils.isBlank(team.getPassword()) || team.getPassword().length()>32 ){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码设置不正确");
            }
        }
        else if(status>2 || status<0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //7.检验失效时间>当前时间，允许失效时间为空
        Date expireTime=team.getExpireTime();
        if(expireTime!=null && new Date().after(expireTime)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"失效时间超过当前时间");
        }
        //8.单个用户最多创建5个队伍，未过期的队伍数<5
        Long userId=user.getId();
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getUserId,userId);
        queryWrapper.gt(Team::getExpireTime,new Date()).or().isNull(Team::getExpireTime);
        long count = this.count(queryWrapper);
        if(count>=5){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"用户最多创建5个队伍");
        }
        //9.插入队伍信息到队伍表
        team.setUserId(userId);
        boolean result=this.save(team);
        if(!result){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"创建失败");
        }
        //10.插入信息到关系表
        Long teamId=team.getId();
        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        userTeam.setIsFounder(1);
        result=userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"创建失败");
        }
        return Result.ok(teamId,"创建成功");
    }

    @Override
    public Result searchTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request) {
        if(teamSearchRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        Long userId=null;
        Integer isRelative =null;
        User currentUser=userService.getCurrentUser(request);
        Long id = teamSearchRequest.getId();
        if(id!=null && id>0){
            queryWrapper.eq(Team::getId,id);
        }
        //筛选是否是用户加入的或创建的队伍
        isRelative=teamSearchRequest.getIsRelative();
        //筛选加入的队伍
        List<Long> idList = teamSearchRequest.getIdList();
        if(idList!=null){
            queryWrapper.in(Team::getId,idList);
        }
        String searchText = teamSearchRequest.getSearchText();
        if(StringUtils.isNotBlank(searchText)){
            queryWrapper.and(qw->qw.like(Team::getName,searchText).or().like(Team::getDescription,searchText));
        }
        Integer maxNum = teamSearchRequest.getMaxNum();
        if(maxNum!=null && maxNum>0){
            queryWrapper.eq(Team::getMaxNum,maxNum);
        }
        Integer num = teamSearchRequest.getNum();
        if(num!=null && num>0){
            queryWrapper.eq(Team::getNum,num);
        }
        userId = teamSearchRequest.getUserId();
        if(userId!=null){
            queryWrapper.eq(Team::getUserId,userId);
        }
        //根据状态查询，区分管理员，是否展示私有队伍
        Integer status = teamSearchRequest.getStatus();
        boolean admin = userService.isAdmin(request);
        if(status!=null && status>=0 ){
            if(!admin && status==1){
                throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有查询权限");
            }
            queryWrapper.eq(Team::getStatus,status);
        }
        //区分是否是管理员，是否是自己创建的队伍或加入的队伍，是否展示私有队伍
        if(!userService.isAdmin(request) && !Objects.equals(userId, currentUser.getId()) && isRelative==null){
            queryWrapper.ne(Team::getStatus,1);
        }
        //不展示已过期队伍，注意queryWrapper写法
        queryWrapper.and(qw->qw.gt(Team::getExpireTime,new Date()).or().isNull(Team::getExpireTime));
//        queryWrapper.gt(Team::getExpireTime,new Date()).or().isNull(Team::getExpireTime);
//        List<Team> teamList = this.list(queryWrapper);
        //改成分页查询
        List<Team> teamList;
        if(teamSearchRequest.getPageNum()==null || teamSearchRequest.getPageSize()==null){
            teamList=teamMapper.selectList(queryWrapper);
        }
        else{
            Page<Team> page=new Page<>(teamSearchRequest.getPageNum(),teamSearchRequest.getPageSize());
            Page<Team> teamPage = teamMapper.selectPage(page, queryWrapper);
            teamList=teamPage.getRecords();
        }
        return Result.ok(teamList,"查询成功");
    }

    @Override
    public Result searchMyJoinTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request) {
        Long userId=teamSearchRequest.getUserId();
        if(userId==null || userId<=0){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"没有用户参数");
        }
        LambdaQueryWrapper<UserTeam> queryWrapper=new LambdaQueryWrapper<>();
        //包括创建的队伍
//        Integer isFounder = teamSearchRequest.getIsFounder();
//        queryWrapper.eq(UserTeam::getIsFounder,isFounder);
        //查询加入的队伍id
        queryWrapper.eq(UserTeam::getUserId,userId);
        List<UserTeam> teamList = userTeamService.list(queryWrapper);
        List<Long> teamIdList=new ArrayList<>();
        for (UserTeam userTeam : teamList) {
            teamIdList.add(userTeam.getTeamId());
        }
        teamSearchRequest.setIdList(teamIdList);
        teamSearchRequest.setUserId(null);
        return searchTeam(teamSearchRequest,request);
    }

    public Result updateTeam(Team team, HttpServletRequest request) {
        if(team==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"修改参数为空");
        }
        //校验权限，只有管理员或队伍的创建者可以修改
        User currentUser=userService.getCurrentUser(request);
        boolean admin = userService.isAdmin(request);
        if(!Objects.equals(team.getUserId(), currentUser.getId()) && !admin ){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有修改权限");
        }
        //改变队伍状态时传入密码
        Integer status = team.getStatus();
        if(status!=null && status==2){
            if(StringUtils.isBlank(team.getPassword())){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"修改队伍状态必须传入密码");
            }
        }
        int result = teamMapper.updateById(team);
        if(result==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"修改失败");
        }
        return Result.ok(result,"修改成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if(joinTeamRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"传入参数为空");
        }
        Long teamId = joinTeamRequest.getTeamId();
        Team team=getTeamById(teamId);
        //队伍未过期
        Date expireTime = team.getExpireTime();
        if(expireTime.before(new Date())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍已过期");
        }
        //队伍当前人数未满
        Integer num = team.getNum();
        Integer maxNum = team.getMaxNum();
        if(num>=maxNum){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍已满");
        }
        //不能加入私有的队伍，加入私密队伍验证密码
        Integer status = team.getStatus();
        if(status!=null && status==1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"无法加入私有队伍");
        }
        if(status!=null && status==2){
            String password = team.getPassword();
            if(StringUtils.isBlank(password) || !password.equals(joinTeamRequest.getPassword())){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码错误");
            }
        }
        //todo:需要加锁，防止多个线程不安全
        //不能重复加入已加入的队伍
        User currentUser=userService.getCurrentUser(request);
        Long userId = currentUser.getId();
        LambdaQueryWrapper<UserTeam> queryWrapperUT = new LambdaQueryWrapper<>();
        queryWrapperUT.eq(UserTeam::getTeamId,teamId).eq(UserTeam::getUserId,userId);
        long count = userTeamService.count(queryWrapperUT);
        if(count>0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"已加入");
        }
        //修改队伍信息
        team.setNum(num+1);
        int result = teamMapper.updateById(team);
        if(result==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"加入失败");
        }
        //添加关系到关系表
        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        userTeam.setIsFounder(0);
        boolean save = userTeamService.save(userTeam);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"加入失败");
        }
        return Result.ok(userId,"加入成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result quitTeam(Long teamId, HttpServletRequest request) {
        if(teamId==null || teamId<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍参数错误");
        }
        Team team=getTeamById(teamId);
        //检验是否在队伍中
        User currentUser=userService.getCurrentUser(request);
        Long userId = currentUser.getId();
        LambdaQueryWrapper<UserTeam> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId,teamId).eq(UserTeam::getUserId,userId);
        long count = userTeamService.count(queryWrapper);
        if(count==0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"不在队伍中");
        }
        //队伍是否解散
        Integer num = team.getNum();
        if(num==1){
            //删除记录
            boolean removeTeam = this.removeById(teamId);
            if(!removeTeam){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"退出失败");
            }
            LambdaQueryWrapper<UserTeam> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(UserTeam::getTeamId,teamId);
            boolean remove = userTeamService.remove(queryWrapper1);
            if(!remove){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"退出失败");
            }
        }
        else{
            //区分是否为队长
            if(Objects.equals(team.getUserId(), userId)){
                //查询队伍成员的加入时间，转移队长
                LambdaQueryWrapper<UserTeam> queryWrapper2=new LambdaQueryWrapper<>();
                queryWrapper2.eq(UserTeam::getTeamId,teamId);
                //根据关系id确定加入队伍的先后顺序
                queryWrapper2.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper2);
                if(CollectionUtils.isEmpty(userTeamList) || userTeamList.size()<2){
                    throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"队伍人数太少");
                }
                UserTeam nextUserTeam=userTeamList.get(1); //当前用户的下一个人
                nextUserTeam.setIsFounder(1); //更新为队长
                //更新队长信息
                boolean updateNext = userTeamService.updateById(nextUserTeam);
                if(!updateNext){
                    throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"退出失败");
                }
                Long nextUserId = nextUserTeam.getUserId();
                //更新队长
                team.setUserId(nextUserId);

            }
            //更新队伍人数
            team.setNum(num-1);
            int result = teamMapper.updateById(team);
            if(result==0){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"退出失败");
            }
            //移除关系
            boolean remove=userTeamService.remove(queryWrapper);
            if(!remove){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"退出失败");
            }
        }
        return Result.ok(null,"退出成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteTeam(Long teamId, HttpServletRequest request) {
        if(teamId==null || teamId<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍参数错误");
        }
        Team team=getTeamById(teamId);
        User currentUser=userService.getCurrentUser(request);
        //校验用户权限
        if(!Objects.equals(team.getUserId(), currentUser.getId()) && !userService.isAdmin(request)){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有权限");
        }
        //移除所有相关信息
        boolean removeTeam = this.removeById(teamId);
        if(!removeTeam){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"解散失败");
        }
        LambdaQueryWrapper<UserTeam> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId,teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if(!remove){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"解散失败");
        }
        return Result.ok(true,"解散成功");
    }

    /**
     * 通过队伍id获取队伍
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId){
        if(teamId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Team team = teamMapper.selectById(teamId);
        if(team==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍不存在");
        }
        return team;
    }
}




