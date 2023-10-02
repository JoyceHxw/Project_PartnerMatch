package com.hxw.partnermatch.exception;

import com.hxw.partnermatch.utils.Result;
import com.hxw.partnermatch.utils.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 内部消化异常，让前端得到更详细的信息，不暴露服务器内部状态
 */

@RestControllerAdvice //aop
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException e){
        log.error("BusinessException"+e.getMessage(),e);
        return Result.build(null,e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e);
        return Result.build(null, ResultCodeEnum.SYSTEM_ERROR,e.getMessage(),"");
    }
}
