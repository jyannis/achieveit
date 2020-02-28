package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.enums.RoleEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("项目生命周期控制")
@RequestMapping("/project")
public class ProjectController {

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PostMapping("/build")
    @ApiOperation("新建项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project", value = "项目信息", required = true),
            @ApiImplicitParam(name = "superiorId", value = "项目上级id", required = true),
    })
    public Object build(Project project,String superiorId){
        //TODO 新建项目时记得把项目经理自己加进auth表，并开启他的全部权限。这是为了在之后交付、完结时确定是这个项目的项目经理而非其他项目的项目经理发起的操作
        return "success";
    }

    @Auth(role = RoleEnum.SUPERIOR)
    @PutMapping("/review")
    @ApiOperation("审核项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
            @ApiImplicitParam(name = "status", value = "是否批准（1=批准，-1=拒绝", required = true),
    })
    public Object review(String projectId,Integer status){
        return "success";
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/deliver")
    @ApiOperation("交付项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object deliver(String projectId){
        return "success";
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/close")
    @ApiOperation("完结项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object close(String projectId){
        return "success";
    }

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER)
    @PutMapping("/file")
    @ApiOperation("归档项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
    })
    public Object file(String projectId){
        return "success";
    }



}
