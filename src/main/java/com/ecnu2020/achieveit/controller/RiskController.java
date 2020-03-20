package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.RiskService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@Api("项目风险控制")
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/mod")
    @ApiOperation(value = "修改风险信息",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object modRiskInfo(String projectId,@RequestBody @Valid Risk risk){
        risk.setProjectId(projectId);
        return riskService.modRisk(risk);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PostMapping("/add")
    @ApiOperation(value = "新增风险",response = Risk.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object addRisk(String projectId,@RequestBody @Valid Risk risk){
        risk.setProjectId(projectId);
        return riskService.addRisk(risk);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @GetMapping("/getRisk")
    @ApiOperation(value = "获取风险列表",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object getRisk(String projectId,PageParam pageParam){
        return riskService.getRiskList(projectId,pageParam);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除风险",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object delRisk(String projectId,@RequestParam Integer id){
        return riskService.delRisk(id);
    }


    @Scheduled(cron ="0 0 8 ? * 2")
    public void setRiskMail(){
        riskService.setRiskMail();
    }

}
