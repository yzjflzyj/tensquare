package com.tensquare.base.controller;

import entity.Result;
import exception.MyException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// 切的是controller
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 捕获异常后调用这个方法
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.error("出错了");
    }

    @ExceptionHandler(MyException.class)
    public Result handleMyException(MyException e){
        e.printStackTrace();
        return Result.error(e.getMessage());
    }

    /**
     * 数据有效性校验
     * MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // 有哪些校验没有通过的属性值
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuffer sb = new StringBuffer();
        for (FieldError fieldError : fieldErrors) {
            // 每个不通过的属性
            sb.append(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            sb.append("\r\n");
        }
        return Result.error(sb.toString());
    }
}
