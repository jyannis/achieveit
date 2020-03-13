package com.ecnu2020.achieveit.enums;

import lombok.Getter;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/12 21:28
 **/
@Getter
public enum BugEnum {
    NON("未处理"),
    SOLVE("已解决"),
    GOING("进行中");

    private String status;

    BugEnum(String status){
        this.status = status;
    }
}
