package com.hxw.partnermatch.utils;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"success",""),
    IS_NULL(501,"null error",""),
    PARAMS_ERROR(502,"parameters error",""),
    NO_AUTH(503,"no authority",""),
    SYSTEM_ERROR(550,"system error","");

    private final Integer code;
    private final String message;
    private final String description;

    ResultCodeEnum(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description=description;
    }

}
