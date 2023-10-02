package com.hxw.partnermatch.model.requests;

import lombok.Getter;

@Getter
public class JoinTeamRequest {
    private Long teamId;
    private String password;
}
