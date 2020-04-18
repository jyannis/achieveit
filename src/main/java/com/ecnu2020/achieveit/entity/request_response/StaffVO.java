package com.ecnu2020.achieveit.entity.request_response;

import com.ecnu2020.achieveit.enums.RoleEnum;

import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * @description: 员工列表需要添加角色
 * @author: ganlirong
 * @create: 2020/04/18
 */
@Data
public class StaffVO {
    private String id;
    private String name;
    private String password;
    private String email;
    private String department;
    private String tel;
    private Short manager;
    private String role;
}
