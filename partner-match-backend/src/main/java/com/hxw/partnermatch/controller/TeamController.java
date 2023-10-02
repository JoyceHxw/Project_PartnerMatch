package com.hxw.partnermatch.controller;

import com.hxw.partnermatch.exception.BusinessException;
import com.hxw.partnermatch.model.Team;
import com.hxw.partnermatch.model.requests.JoinTeamRequest;
import com.hxw.partnermatch.model.requests.TeamIdRequest;
import com.hxw.partnermatch.model.requests.TeamSearchRequest;
import com.hxw.partnermatch.service.TeamService;
import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("team")
@CrossOrigin(origins = "http://127.0.0.1:5173",allowCredentials = "true") //解决跨域携带cookie的问题
public class TeamController {
    @Autowired
    private TeamService teamService;

    @PostMapping("build")
    public Result buildTeam(@RequestBody Team team, HttpServletRequest request){
        Result result=teamService.buildTeam(team,request);
        return result;
    }

    @GetMapping("getById")
    public Result getTeamById(@RequestParam Long id){
        if(id==null || id<1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍参数错误");
        }
        Team team = teamService.getById(id);
        return Result.ok(team,"获取成功");
    }

    @PostMapping("search")
    public Result searchTeam(@RequestBody TeamSearchRequest teamSearchRequest, HttpServletRequest request){
        if(teamSearchRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=teamService.searchTeam(teamSearchRequest,request);
        return result;
    }

    @PostMapping("search/myJoin")
    public Result searchMyJoinTeam(@RequestBody TeamSearchRequest teamSearchRequest, HttpServletRequest request){
        if(teamSearchRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        Result result=teamService.searchMyJoinTeam(teamSearchRequest,request);
        return result;
    }

    @PostMapping("update")
    public Result updateTeam(@RequestBody Team team, HttpServletRequest request){
        Result result=teamService.updateTeam(team,request);
        return result;
    }

    @PostMapping("join")
    public Result joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        Result result=teamService.joinTeam(joinTeamRequest,request);
        return result;
    }

    @PostMapping("quit")
    public Result quitTeam(@RequestBody TeamIdRequest teamIdRequest, HttpServletRequest request){
        Long teamId=teamIdRequest.getTeamId();
        Result result=teamService.quitTeam(teamId, request);
        return result;
    }

    @PostMapping("delete")
    public Result deleteTeam(@RequestBody TeamIdRequest teamIdRequest, HttpServletRequest request){
        Long teamId=teamIdRequest.getTeamId();
        Result result=teamService.deleteTeam(teamId, request);
        return result;
    }

}
