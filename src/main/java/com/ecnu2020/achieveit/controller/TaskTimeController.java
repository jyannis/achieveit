package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.TaskTime;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.TaskTimeService;
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
@Validated
@Api("项目工时信息管理")
@RequestMapping("/taskTime")
public class TaskTimeController {

    @Autowired
    private TaskTimeService taskTimeService;

//    @Auth(role = RoleEnum.NON)
    @PostMapping("/add")
    @ApiOperation(value = "新增工时信息",response = TaskTime.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object addTaskTime(String projectId,@RequestBody @Valid TaskTime taskTime){
            return taskTimeService.createTaskTime(projectId,taskTime);
    }

    @Auth(role = RoleEnum.SUPERIOR)
    @PutMapping("/review")
    @ApiOperation(value = "审核工时",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "id", value = "工时id", paramType = "query",required = true,dataType = "int"),
            @ApiImplicitParam(name = "status", value = "审核状态", paramType = "query",required = true,dataType = "int"),

    })
    public Object  ReviewTaskTime(String projectId, Integer id, Short status){
        System.out.println(status);
        return taskTimeService.ReviewTaskTime(id,status);
    }

    @PutMapping("/mod")
    @ApiOperation(value = "修改工时信息",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public  Object  modTaskTime(String projectId,@RequestBody @Valid TaskTime taskTime){
            return taskTimeService.modTaskTime(projectId,taskTime);
    }

    @GetMapping("/getList")
    @ApiOperation(value = "得到工时列表",response = PageInfo.class)
    public  Object  getTaskTimeList(PageParam pageParam){
        return taskTimeService.getTaskTimeList(pageParam);
    }
}
