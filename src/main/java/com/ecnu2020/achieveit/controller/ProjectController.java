package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Project;
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
        @ApiImplicitParam(name = "staffId", value = "员工id", required = true),
        @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
        @ApiImplicitParam(name = "keyWord", value = "关键字", required = false),
        @ApiImplicitParam(name = "pageNum", value = "页数 (不小于0)", paramType = "query",defaultValue = "1",dataType = "Integer"),
        @ApiImplicitParam(name = "count", value = "每页显示数量 (不小于0)", paramType = "query",defaultValue = "10",dataType = "Integer"),

    })
    public PageInfo<Object> list(String staffId,
                                 @RequestParam(defaultValue = "1970-01-01 00:00:00") String beginTime,
                                 @RequestParam(defaultValue = "2100-12-31 00:00:00") String endTime,
                                 @RequestParam(required = false)String keyWord,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer count ){
        return projectService.list(staffId,beginTime,endTime,keyWord,pageNum,count);
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

    @Auth(role = RoleEnum.SUPERIOR)
    @PutMapping("/review")
    @ApiOperation(value = "审核项目",response = Boolean.class)
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
    @ApiOperation(value = "完结项目",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object close(String projectId){
        return projectService.close(projectId);
    }

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER)
    @PutMapping("/file")
    @ApiOperation(value = "归档项目",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object file(String projectId){
        return projectService.file(projectId);
    }



}
