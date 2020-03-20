package com.ecnu2020.achieveit.enums;

import lombok.Getter;

/**
 * @Description 风险/缺陷状态枚举
 * @Author ZC
 **/
@Getter
public enum BugEnum {
    NON("未解决"),
    SOLVE("已解决"),
    GOING("进行中");

    private String status;

    BugEnum(String status){
        this.status = status;
    }
}
