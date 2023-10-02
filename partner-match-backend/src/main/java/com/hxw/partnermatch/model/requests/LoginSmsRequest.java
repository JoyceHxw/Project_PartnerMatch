package com.hxw.partnermatch.model.requests;

import lombok.Getter;

@Getter
public class LoginSmsRequest {
    private String phone;
    private Integer code;
}
