package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知里进行公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        if (autoFill == null) {
            return;
        }

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            return;
        }

        OperationType operationType = autoFill.value();
        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        log.info("开始进行公共字段填充，操作类型：{}", operationType);
        if (OperationType.INSERT == operationType) {
            invokeSetMethod(entity, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
            invokeSetMethod(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
            invokeSetMethod(entity, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
            invokeSetMethod(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        } else if (OperationType.UPDATE == operationType) {
            invokeSetMethod(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
            invokeSetMethod(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
        }
    }

    private void invokeSetMethod(Object entity, String methodName, Class<?> parameterType, Object value) {
        try {
            Method method = entity.getClass().getMethod(methodName, parameterType);
            method.invoke(entity, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("公共字段自动填充失败：" + methodName, ex);
        }
    }
}
