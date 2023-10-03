package com.hxw.partnermatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.mapper.ChatMapper;
import com.hxw.partnermatch.mapper.UserMapper;
import com.hxw.partnermatch.model.Chat;
import com.hxw.partnermatch.model.Team;
import com.hxw.partnermatch.model.User;
import com.hxw.partnermatch.model.requests.ChatRequest;
import com.hxw.partnermatch.model.requests.TeamSearchRequest;
import com.hxw.partnermatch.model.responses.ChatResult;
import com.hxw.partnermatch.model.responses.ChatUserResult;
import com.hxw.partnermatch.service.ChatService;
import com.hxw.partnermatch.service.TeamService;
import com.hxw.partnermatch.service.UserService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 81086
* @description 针对表【chat(消息表)】的数据库操作Service实现
* @createDate 2023-09-23 20:21:01
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Result getChat(ChatRequest chatRequest, HttpServletRequest request) {
        Long id=chatRequest.getId();
        int type = chatRequest.getType();
        if(id==null || (type<0 || type>1)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"参数错误");
        }
        User currentUser=userService.getCurrentUser(request);
        List<ChatUserResult> chatUserResultList=new ArrayList<>();
        if(type==0){
            for (Chat chat : getUserChat(id, currentUser.getId(), type)) {
                User toUser=userService.getById(chat.getToId());
                User fromUser=userService.getById(chat.getFromId());
                ChatUserResult chatUserResult=new ChatUserResult(
                        chat.getToId(),toUser.getUsername(),toUser.getAvatar(),
                        chat.getFromId(),fromUser.getUsername(),fromUser.getAvatar(),
                        chat.getContent(),chat.getCreateTime()
                );
                chatUserResultList.add(chatUserResult);
            }
        }
        else{
            for (Chat chat : getTeamChat(id, type)) {
                User fromUser=userService.getById(chat.getFromId());
                ChatUserResult chatUserResult=new ChatUserResult(
                        chat.getToId(),null,null,
                        chat.getFromId(),fromUser.getUsername(),fromUser.getAvatar(),
                        chat.getContent(),chat.getCreateTime()
                );
                chatUserResultList.add(chatUserResult);
            }

        }
        return Result.ok(chatUserResultList);
    }

    @Override
    public Result getChatUserList(int type, HttpServletRequest request) {
        User currentUser=userService.getCurrentUser(request);
        //查找有聊天记录的聊天对象
        //发消息和接收消息都算
        //1.找到收到或发过消息的对象id
        LambdaQueryWrapper<Chat> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.and(qw->qw.eq(Chat::getToId,currentUser.getId()).or().eq(Chat::getFromId,currentUser.getId()));
        Set<Long> userIds=new HashSet<>();
        for (Chat chat : chatMapper.selectList(queryWrapper)) {
            userIds.add(chat.getToId());
            userIds.add(chat.getFromId());
        }
        //2.删除自身
        userIds.remove(currentUser.getId());
        if(userIds.isEmpty()){
            return Result.ok(null);
        }
        //3.封装返回对象
        List<ChatResult> chatResultList=new ArrayList<>();
        for (User user : userMapper.selectBatchIds(userIds)) {
            List<Chat> chatList = this.getUserChat(user.getId(), currentUser.getId(), 0);
            //最新的聊天记录
            Chat latestChat = chatList.stream()
                    .max(Comparator.comparing(Chat::getCreateTime))
                    .orElse(null);
            if(latestChat!=null){
                Date latestTime=latestChat.getCreateTime();
                String latestText=latestChat.getContent();
                user.setPassword("");
                ChatResult chatResult=new ChatResult(user,latestText,latestTime,null);
                chatResultList.add(chatResult);
            }
        }
        //4.按时间顺序排列
        chatResultList.sort(Comparator.comparing(ChatResult::getLatestTime).reversed());
        return Result.ok(chatResultList);
    }

    @Override
    public Result getChatTeamList(int type, HttpServletRequest request) {
        User currentUser=userService.getCurrentUser(request);
        //查找有聊天记录的队伍
        //发消息和接收消息都算
        //1.找到所在队伍id
        TeamSearchRequest teamSearchRequest=new TeamSearchRequest();
        teamSearchRequest.setUserId(currentUser.getId());
        teamSearchRequest.setIsRelative(1);
        List<Team> teamList = (List<Team>) teamService.searchMyJoinTeam(teamSearchRequest, request).getData();
        //2.找到收到或发过消息的对象id
        List<ChatResult> chatResultList=new ArrayList<>();
        for(Team team: teamList){
            Long teamId = team.getId();
            LambdaQueryWrapper<Chat> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Chat::getType,type).eq(Chat::getToId,teamId);
            List<Chat> chatList = chatMapper.selectList(queryWrapper);
            if(chatList==null){
                continue;
            }
            //最新的聊天记录
            Chat latestChat = chatList.stream()
                    .max(Comparator.comparing(Chat::getCreateTime))
                    .orElse(null);
            if(latestChat!=null){
                User user=userService.getById(latestChat.getFromId());
                Date latestTime=latestChat.getCreateTime();
                String latestText=latestChat.getContent();
                user.setPassword("");
                ChatResult chatResult=new ChatResult(user,latestText,latestTime,team);
                chatResultList.add(chatResult);
            }
        }
        chatResultList.sort(Comparator.comparing(ChatResult::getLatestTime).reversed());
        return Result.ok(chatResultList);
    }

    /**
     * 私信聊天记录
     * @param userId 聊天对象id
     * @param currentUserId 当前用户id
     * @param type 聊天类型
     * @return
     */
    public List<Chat> getUserChat(Long userId, Long currentUserId, int type){
        //如果有缓存，直接读取缓存
        String redisKey=String.format("hxw:partnermatch:userchat:%s%s%s",currentUserId,userId,type);
        ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
        List<Chat> chatList=(List<Chat>) valueOperations.get(redisKey);
        if(chatList!=null){
            return chatList;
        }
        LambdaQueryWrapper<Chat> queryWrapper=new LambdaQueryWrapper<>();
        //互相发送的消息
        queryWrapper.eq(Chat::getType,type)
                .and(qw->qw.eq(Chat::getToId,userId).eq(Chat::getFromId,currentUserId)
                        .or()
                        .eq(Chat::getToId,currentUserId).eq(Chat::getFromId,userId));
        List<Chat> chatList1 = this.list(queryWrapper);
        try{
            //注意设置缓存过期时间，redis内存不能无限期增加
            valueOperations.set(redisKey,chatList1,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            log.error("redis set key error",e);
        }
        return chatList1;
    }

    /**
     * 群聊记录
     * @param teamId 队伍id
     * @param type 聊天类型
     * @return
     */
    public List<Chat> getTeamChat(Long teamId, int type){
        String redisKey=String.format("hxw:partnermatch:teamchat:%s%s",teamId,type);
        ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
        //如果有缓存，直接读取缓存
        List<Chat> chatList=(List<Chat>) valueOperations.get(redisKey);
        if(chatList!=null){
            return chatList;
        }
        LambdaQueryWrapper<Chat> queryWrapper=new LambdaQueryWrapper<>();
        //互相发送的消息
        queryWrapper.eq(Chat::getType,type).eq(Chat::getToId,teamId);
        List<Chat> chatList1 = this.list(queryWrapper);
        try{
            //注意设置缓存过期时间，redis内存不能无限期增加
            valueOperations.set(redisKey,chatList1,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            log.error("redis set key error",e);
        }
        return chatList1;
    }

    @Override
    public Result saveChat(Chat chat) {
        Long toId = chat.getToId();
        Long fromId = chat.getFromId();
        Integer type = chat.getType();
        ValueOperations<String,Object> valueOperations=redisTemplate.opsForValue();
        //更新redis
        if(type==0){
            //消息的发送和接收方的聊天记录都要更新
            String redisKey1=String.format("hxw:partnermatch:userchat:%s%s%s",toId,fromId,type);
            List<Chat> chatList1=(List<Chat>) valueOperations.get(redisKey1);
            if(chatList1!=null){
                chatList1.add(chat);
                valueOperations.set(redisKey1,chatList1,30000, TimeUnit.MILLISECONDS);
            }
            String redisKey2=String.format("hxw:partnermatch:userchat:%s%s%s",fromId,toId,type);
            List<Chat> chatList2=(List<Chat>) valueOperations.get(redisKey2);
            if(chatList2!=null){
                chatList2.add(chat);
                valueOperations.set(redisKey2,chatList2,30000, TimeUnit.MILLISECONDS);
            }
        }
        else{
            String redisKeyTeam=String.format("hxw:partnermatch:teamchat:%s%s",toId,type);
            List<Chat> chatListTeam=(List<Chat>) valueOperations.get(redisKeyTeam);
            if(chatListTeam!=null){
                chatListTeam.add(chat);
                valueOperations.set(redisKeyTeam,chatListTeam,30000, TimeUnit.MILLISECONDS);
            }
        }
        //更新mysql
        boolean save = this.save(chat);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"保存错误");
        }
        return Result.ok(save);
    }
}




