package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.ProjectCondition;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.ProjectService;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("项目生命周期控制")
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/list")
    @ApiOperation(value = "项目列表",response = PageInfo.class)
    @ApiImplicitParams({
    })
    public Object list(ProjectCondition projectCondition,
                       PageParam pageParam){
        return projectService.list(projectCondition,pageParam);
    }

    @GetMapping("/info")
    @ApiOperation(value = "项目详情",response = Project.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectId", value = "项目id", required = true)
    })
    public Object get(String projectId){
        return projectService.get(projectId);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PostMapping("/build")
    @ApiOperation(value = "新建项目",response = Project.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project", value = "项目信息", required = true),
            @ApiImplicitParam(name = "superiorId", value = "项目上级id", required = true),
    })
    public Object build(@RequestBody @Validated Project project, String superiorId){
        //TODO 新建项目时记得把项目经理自己加进auth表，并开启他的全部权限。这是为了在之后交付、完结时确定是这个项目的项目经理而非其他项目的项目经理发起的操作
        return projectService.build(project,superiorId);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/update")
    @ApiOperation(value = "更新项目",response = Boolean.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "project", value = "项目信息", required = true),
    })
    public Object update(@RequestBody @Validated Project project){
        return projectService.update(project);
    }

    @Auth(role = RoleEnum.SUPERIOR)
    @PutMapping("/review")
    @ApiOperation(value = "8.4审批立项",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
            @ApiImplicitParam(name = "status", value = "是否批准（1=批准，-1=拒绝", required = true),
    })
    public Object review(String projectId,Integer status){
        return projectService.review(projectId,status);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/deliver")
    @ApiOperation(value = "交付项目",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object deliver(String projectId){
        return projectService.deliver(projectId);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/close")
    @ApiOperation(value = "完结项目，相当于8.12申请项目归档",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object close(String projectId){
        return projectService.close(projectId);
    }

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER)
    @PutMapping("/file")
    @ApiOperation(value = "8.13审核归档申请",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
            @ApiImplicitParam(name = "status", value = "是否批准（1=批准，-1=拒绝", required = true),
    })
    public Object file(String projectId,Integer status){
        return projectService.file(projectId,status);
    }


}
