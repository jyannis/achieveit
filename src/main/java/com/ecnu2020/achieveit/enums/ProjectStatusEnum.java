package com.ecnu2020.achieveit.enums;

import lombok.Getter;

/**
 * 项目状态枚举
 * @author yan on 2020-02-28
 */
@Getter
public enum ProjectStatusEnum {

    WAITING("待立项"),
    BUILD("申请立项"),
    REVIEW("已立项"),
    REJECTED("已驳回"),
    ONGOING("进行中"),
    DELIVER("已交付"),
    CLOSE("已完结"),
    APPLY("归档申请中"),
    FILE("已归档");

    private String status;


    ProjectStatusEnum(String status) {
        this.status = status;
    }

}
