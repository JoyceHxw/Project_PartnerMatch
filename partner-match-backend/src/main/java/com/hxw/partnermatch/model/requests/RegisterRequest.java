package com.hxw.partnermatch.model.requests;

import com.hxw.partnermatch.model.User;
import lombok.Getter;

@Getter
public class RegisterRequest {
    private User user;
    private String checkPassword;
}
