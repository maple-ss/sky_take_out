package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获请求参数校验异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Objects.toString(error.getDefaultMessage(), "参数校验失败"))
                .findFirst()
                .orElse("参数校验失败");
        log.error("参数校验失败：{}", message);
        return Result.error(message);
    }

}
