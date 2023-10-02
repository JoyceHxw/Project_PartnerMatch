package com.hxw.partnermatch.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * 封装返回结果
 * @param <T>
 */
@Getter
@Setter
public class Result<T> {
    //状态码
    private Integer code;
    //返回消息
    private String message;
    //返回数据
    private T data;
    //返回描述
    private String description;

    public Result(){}

    protected static <T> Result<T> build(T data){
        Result<T> result=new Result<>();
        if(data!=null){
            result.setData(data);
        }
        return result;
    }

    public static <T> Result<T> build(T data, Integer code, String message, String description){
        Result<T> result=build(data);
        result.setCode(code);
        result.setMessage(message);
        result.setDescription(description);
        return result;
    }

    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum){
        Result<T> result=build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        result.setDescription(resultCodeEnum.getDescription());
        return result;
    }

    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum, String description){
        Result<T> result=build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        result.setDescription(description);
        return result;
    }

    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum, String message,String description){
        Result<T> result=build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(message);
        result.setDescription(description);
        return result;
    }

    public static <T> Result<T> ok(T data){
        Result<T> result=build(data);
        return build(data,ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> ok(T data,String description){
        Result<T> result=build(data);
        return build(data,ResultCodeEnum.SUCCESS,description);
    }
}
