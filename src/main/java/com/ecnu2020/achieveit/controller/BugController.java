package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Bug;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.BugService;
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
@Api("项目缺陷控制")
@RequestMapping("/bug")
public class BugController {

    @Autowired
    private BugService bugService;

    @Auth(role = RoleEnum.TESTER)
    @PutMapping("/mod")
    @ApiOperation(value = "修改缺陷信息",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object modBugInfo(String projectId,@RequestBody @Valid Bug bug){
        bug.setProjectId(projectId);
        return bugService.modBug(bug);
    }

    @Auth(role = RoleEnum.TESTER)
    @PostMapping("/add")
    @ApiOperation(value = "新增缺陷",response = Bug.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object addBug(String projectId,@RequestBody @Valid  Bug bug){
        bug.setProjectId(projectId);
        return bugService.addBug(bug);
    }

    @Auth(role = RoleEnum.TESTER)
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除缺陷",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object delBug(String projectId,@RequestParam Integer id){
        return bugService.delBug(id);
    }

    @GetMapping("/getBug")
    @ApiOperation(value = "获取缺陷列表",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", paramType = "query",required = true,dataType = "String"),
    })
    public Object getBug(String projectId,PageParam pageParam){
        return bugService.getBugList(projectId,pageParam);
    }
}
