package com.ecnu2020.achieveit.enums;

import lombok.Getter;

/**
 * 项目角色枚举
 * @author yan on 2020-02-27
 */
@Getter
public enum RoleEnum {
    NON("无角色"),
    PROJECT_MANAGER("项目经理"),
    SUPERIOR("项目上级"),
    CONFIGURATION_MANAGER("组织配置管理员"),
    EPG_LEADER("EPG leader"),
    QA_MANAGER("QA manager"),
    DEVELOPER_LEADER("开发Leader"),
    DEVELOPER("开发人员"),
    TEST_LEADER("测试Leader"),
    TESTER("测试人员"),
    EPG("EPG"),
    QA("QA");


    private String roleName;


    RoleEnum(String roleName) {
        this.roleName = roleName;
    }


}
