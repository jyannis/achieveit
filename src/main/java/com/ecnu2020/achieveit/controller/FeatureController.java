package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Feature;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.FeatureCondition;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.FeatureService;
import com.ecnu2020.achieveit.util.MakeExcel;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/feature")
public class FeatureController {

    @Autowired
    private FeatureService featureService;

    @GetMapping("/list")
    @ApiOperation(value = "功能列表",response = PageInfo.class)
    @ApiImplicitParams({
    })
    public Object list(String projectId,
                       FeatureCondition featureCondition,
                       PageParam pageParam){
        return featureService.list(projectId,featureCondition,pageParam);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PostMapping("/build")
    @ApiOperation(value = "新建功能",response = Project.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectId", value = "项目Id", required = true),
        @ApiImplicitParam(name = "featureList", value = "(子)功能数组", required = true),
    })
    public Object build(@RequestParam String projectId,
                        @RequestBody @Validated List<Feature> featureList){
        return featureService.build(featureList);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/update")
    @ApiOperation(value = "更新功能",response = Boolean.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectId", value = "项目Id", required = true),
        @ApiImplicitParam(name = "featureList", value = "(子)功能数组", required = true),
    })
    public Object update(@RequestParam String projectId,
                         @RequestBody List<Feature> featureList){
        return featureService.update(featureList);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除功能",response = Boolean.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectId", value = "项目Id", required = true),
        @ApiImplicitParam(name = "featureList", value = "(子)功能数组", required = true),
    })
    public Object delete(@RequestParam String projectId,
                         @RequestBody List<Integer> featureIdList){
        return featureService.delete(featureIdList);
    }

    @GetMapping("/getExcel")
    @ApiOperation(value = "下载功能列表excel",response = String.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectId", value = "项目Id", required = true)
    })
    public Object getExcel(String projectId) throws Exception {
//        String filePath=
//        FileSystemResource file = new FileSystemResource(filePath);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getFilename(),"utf-8")));
//        headers.add("Access-Control-Expose-Headers", "FileName");
//        headers.add("FileName", URLEncoder.encode(file.getFilename(),"utf-8"));
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");

        return featureService.getExcel(projectId);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @GetMapping("/template")
    @ApiOperation(value = "下载功能模板",response = String.class)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "projectId", value = "项目Id", required = true)
    })
    public Object template(@RequestParam String projectId){
        return featureService.getTemplate();
    }


}
