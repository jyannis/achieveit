package com.ecnu2020.achieveit.aop;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.common.Result;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.common.RRException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 权限控制
 * 返回体统一封装
 * @author yan on 2020-02-27
 */
@Aspect
@Component
@Slf4j
public class AuthAspect {


    @Around("print()")
    @Transactional
    public Result doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Auth authAnnotation = method.getAnnotation(Auth.class);
        RoleEnum acquireRole = authAnnotation.role();
        short acquireGitPerm = authAnnotation.gitPerm();
        short acquireFilePerm = authAnnotation.filePerm();
        short acquireTaskTimePerm = authAnnotation.taskTimePerm();

        // 请求体 获取项目id
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String projectIdString = request.getParameter("projectId");
        Integer projectId =  projectIdString == null ? null : Integer.parseInt(projectIdString);

        //检查权限
        //如果要求的role是项目经理就从员工表里匹配
        if(acquireRole == RoleEnum.PROJECT_MANAGER){
            //TODO 鉴权
        }


        if(acquireRole != RoleEnum.PROJECT_MANAGER
            && acquireRole != RoleEnum.NON){
            //如果要求的role不是项目经理也不是NON，projectId就不应为null
            if(projectId == null){
                throw new RRException(ExceptionTypeEnum.PROJECTID_MISSING);
            }

            //TODO 鉴权
        }

        //此时权限校验已通过

        //执行切点
        Object object = proceedingJoinPoint.proceed();

        //封装返回体
        return Result.success(object);
    }


    @Pointcut("execution(public * com.ecnu2020.achieveit.controller.*.*(..))")
    public void print() {
    }

}
