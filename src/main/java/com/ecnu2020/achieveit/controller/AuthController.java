package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.enums.RoleEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("项目角色权限控制")
@RequestMapping("/auth")
public class AuthController {

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PostMapping("/role")
    @ApiOperation("添加项目成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
            @ApiImplicitParam(name = "staffId", value = "员工id", required = true),
            @ApiImplicitParam(name = "role", value = "角色名称（在枚举类型内）", required = true),
            @ApiImplicitParam(name = "roleName", value = "角色名称（在枚举类型内）", required = true),
            @ApiImplicitParam(name = "gitAuth", value = "git权限（0=无，1=读，2=读写）", defaultValue = "0"),
            @ApiImplicitParam(name = "fileAuth", value = "文件服务器权限（0=无，1=读，2=读写）", defaultValue = "0"),
            @ApiImplicitParam(name = "taskTimeAuth", value = "登记工时权限（0=无，1=读，2=读写）", defaultValue = "0"),
    })
    public Object build(String projectId, String staffId,String roleName,
                        Short gitAuth,Short fileAuth,Short taskTimeAuth){
        return "success";
    }

}
