package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.service.ActivityService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/zhangjingying")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    //private ActivityService activityService = new ActivityServiceImpl();

    @PostMapping("/sub_activity")
    @ApiOperation(value = "获取子活动",response = String.class, responseContainer = "List")
    @ApiImplicitParams({@ApiImplicitParam(name = "activity", value = "活动", required = true),
            @ApiImplicitParam(name = "projectId", value = "项目Id", required = true)})//需要吗？
    public Object getSub_activityList(String activity){
        return activityService.sub_activityList(activity);
    }
}
