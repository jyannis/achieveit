package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Api("项目角色权限控制")
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PostMapping("/add")
    @ApiOperation("添加项目成员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "staffId", value = "员工id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "roleName", value = "角色名称（在枚举类型内）", paramType = "query", required = true,dataType = "String"),
            @ApiImplicitParam(name = "gitAuth", value = "git权限（0=无，1=读，2=读写）", paramType = "query", defaultValue = "0",dataType = "Short"),
            @ApiImplicitParam(name = "fileAuth", value = "文件服务器权限（0=无，1=读，2=读写）", paramType = "query", defaultValue = "0",dataType = "Short"),
            @ApiImplicitParam(name = "taskTimeAuth", value = "登记工时权限（0=无，1=读，2=读写）", paramType = "query", defaultValue = "0",dataType = "Short"),
    })
    public Object addMemberAuth(String projectId,String staffId,String roleName,
                          Short gitAuth, Short fileAuth, Short taskTimeAuth){
        return authService.addMemberAuth(projectId,staffId,roleName,gitAuth,fileAuth,taskTimeAuth);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @DeleteMapping("/delete")
    @ApiOperation("删除项目成员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "staffId", value = "员工id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "roleName", value = "角色名称（在枚举类型内）", paramType = "query", required = true,dataType = "String"),
    })
    public Object deleteMemberAuth(String projectId,String staffId,String roleName){
            authService.deleteMemberAuth(projectId,staffId,roleName);
            return "success";
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/update")
    @ApiOperation("修改项目成员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "staffId", value = "员工id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "roleName", value = "角色名称（在枚举类型内）", paramType = "query", required = true,dataType = "String"),
            @ApiImplicitParam(name = "gitAuth", value = "git权限（0=无，1=读，2=读写）", paramType = "query", defaultValue = "0",dataType = "Short"),
            @ApiImplicitParam(name = "fileAuth", value = "文件服务器权限（0=无，1=读，2=读写）", paramType = "query", defaultValue = "0",dataType = "Short"),
            @ApiImplicitParam(name = "taskTimeAuth", value = "登记工时权限（0=无，1=读，2=读写）", paramType = "query", defaultValue = "0",dataType = "Short"),
    })
    public Object modMemberAuth(String projectId,String staffId,String roleName,
                             Short gitAuth,Short fileAuth,Short taskTimeAuth){
        return authService.modMemberAuth(projectId,staffId,roleName,gitAuth,fileAuth,taskTimeAuth);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @GetMapping("/getMembers")
    @ApiOperation("查看项目成员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", paramType = "query",defaultValue = "1",dataType = "Integer"),
            @ApiImplicitParam(name = "count", value = "每页显示数量 (不小于0)", paramType = "query",defaultValue = "10",dataType = "Integer"),
    })
    public Object getProjectMember(String projectId,Integer pageNum,Integer count){
        return authService.getProjectMember(projectId,pageNum,count);
    }

}
