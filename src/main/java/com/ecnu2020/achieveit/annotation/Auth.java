package com.ecnu2020.achieveit.annotation;

import com.ecnu2020.achieveit.enums.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限控制注解
 * @author yan on 2020-02-27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    //角色
    RoleEnum role() default RoleEnum.NON;

    //git读写权限 0=无权限 1=读 2=写
    short gitPerm() default 0;

    //文件服务器读写权限
    short filePerm() default 0;

    //工时登记权限
    short taskTimePerm() default 0;



}
