package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.StaffService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/11 22:25
 **/
@RestController
@Validated
@Api("项目人员信息管理")
@RequestMapping("/staff")
public class StaffController {


    @Autowired
    private StaffService staffService;

    @GetMapping("/projectStaff")
    @ApiOperation(value = "员工信息",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键字")
    })
    public Object ProjectStaff(String projectId, String keyword, PageParam pageParam){
        if(keyword == null) keyword = "";
        return staffService.getProjectStaff(projectId,keyword,pageParam);
    }

    @GetMapping("/importStaff")
    @ApiOperation(value = "导入员工信息",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true)
    })
    public Object Staff(String projectId,PageParam pageParam){
        return staffService.importStaff(projectId,pageParam);
    }

    @Auth(role = RoleEnum.PROJECT_MANAGER)
    @PutMapping("/modStaffInfo")
    @ApiOperation(value = "修改员工信息",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目id", required = true)
    })
    public Object modStaffInfo(String projectId,@RequestBody @Valid Staff staff){
        return staffService.modStaffInfo(staff);
    }
}
