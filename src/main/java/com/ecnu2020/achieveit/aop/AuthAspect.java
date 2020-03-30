package com.ecnu2020.achieveit.aop;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.common.Result;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.service.AuthService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.Subject;
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

    @Autowired
    AuthService authService;

    @Around("print()")
    @Transactional
    public Result doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Auth authAnnotation = method.getAnnotation(Auth.class);
        if(authAnnotation == null){
            //执行切点
            Object object = proceedingJoinPoint.proceed();

            //封装返回体
            return Result.success(object);
        }
        RoleEnum acquireRole = authAnnotation.role();
        short acquireGitAuth = authAnnotation.gitAuth();
        short acquireFileAuth = authAnnotation.fileAuth();
        short acquireTaskTimeAuth = authAnnotation.taskTimeAuth();

        // 请求体 获取项目id
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String projectId = request.getParameter("projectId");


        UserDTO principal = (UserDTO) SecurityUtils.getSubject().getPrincipal();
//        if(principal == null){
//            throw new UnauthenticatedException();
//        }

        //检查权限
        //如果要求的role是项目经理，且没传projectId，说明正在新建项目，就从员工表里匹配
        if(acquireRole == RoleEnum.PROJECT_MANAGER && projectId == null && "build".equals(method.getName())){
            if(!authService.checkManager(principal)){
                throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
            }
            //执行切点
            Object object = proceedingJoinPoint.proceed();

            //封装返回体
            return Result.success(object);
        }

        //如果不满足“要求的role是项目经理，且没传projectId”，就一定传了projectId，从auth表里匹配
        if(acquireRole != RoleEnum.NON){
            if(projectId == null){
                throw new RRException(ExceptionTypeEnum.PROJECTID_MISSING);
            }
            if(!authService.checkRole(acquireRole,acquireGitAuth,acquireFileAuth,acquireTaskTimeAuth,projectId,principal)){
                throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
            }
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
