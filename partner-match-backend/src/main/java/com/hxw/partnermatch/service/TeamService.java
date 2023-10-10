package com.hxw.partnermatch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxw.partnermatch.model.Team;
import com.hxw.partnermatch.model.requests.JoinTeamRequest;
import com.hxw.partnermatch.model.requests.TeamSearchRequest;
import com.hxw.partnermatch.utils.Result;
import jakarta.servlet.http.HttpServletRequest;


/**
* @author 81086
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2023-09-16 17:53:05
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @param request
     * @return
     */
    Result buildTeam(Team team, HttpServletRequest request);

    /**
     * 查询队伍列表
     * @param teamSearchRequest
     * @param request
     * @return
     */
    Result searchTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request);

    /**
     * 查询用户加入的队伍
     * @param teamSearchRequest
     * @param request
     * @return
     */
    Result searchMyJoinTeam(TeamSearchRequest teamSearchRequest, HttpServletRequest request);

    /**
     * 修改队伍信息
     * @param team
     * @param request
     * @return
     */
    Result updateTeam(Team team, HttpServletRequest request);

    /**
     * 加入队伍
     * @param joinTeamRequest
     * @param request
     * @return
     */
    Result joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request);

    /**
     * 退出退伍
     * @param teamId
     * @param request
     * @return
     */
    Result quitTeam(Long teamId, HttpServletRequest request);

    /**
     * 解散队伍
     * @param teamId
     * @param request
     * @return
     */
    Result deleteTeam(Long teamId, HttpServletRequest request);


    /**
     * 同一用户不能重复加入同一队伍
     * 加悲观锁synchronized
     * @param teamId
     * @param userId
     * @param team
     * @param num
     * @return
     */
    Result joinTeamOnce(Long teamId, Long userId, Team team, Integer num);
}
