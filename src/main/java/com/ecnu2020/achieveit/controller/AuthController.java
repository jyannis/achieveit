package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.request_response.auth.AddMemberReq;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.AuthService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Api("项目角色权限控制")
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/add")
    @ApiOperation(value = "添加项目成员权限",response = Auth.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object addMemberAuth(String projectId, @Valid AddMemberReq addMemberReq){
        return authService.addMemberAuth(projectId,addMemberReq);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除项目成员权限",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "staffId", value = "员工id", paramType = "query",required = true,dataType = "String"),
    })
    public Object deleteMemberAuth(String projectId, String staffId){
            return authService.deleteMemberAuth(projectId,staffId);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/update")
    @ApiOperation(value = "修改项目成员权限",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object modMemberAuth(String projectId,@Valid AddMemberReq addMemberReq){
        return authService.modMemberAuth(projectId,addMemberReq);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @GetMapping("/getMembers")
    @ApiOperation(value = "查看项目成员权限",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object getProjectMember(String projectId,String keyword, PageParam pageParam){
        if(keyword == null) keyword = "";
        return authService.getProjectMember(projectId,keyword,pageParam);
    }

    @GetMapping("/getRoles")
    @ApiOperation(value = "查看项目成员角色",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object getRoleMember(String projectId,PageParam pageParam){
        return authService.getRoles(projectId,pageParam);
    }

}
