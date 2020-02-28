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
        return "success";
    }

    @Auth(role = RoleEnum.SUPERIOR)
    @PutMapping("/review")
    @ApiOperation("审核项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "project", value = "项目信息", required = true),
            @ApiImplicitParam(name = "status", value = "是否批准（1=批准，-1=拒绝", required = true),
    })
    public Object review(String projectId,Integer status){
        return "success";
    }



}
