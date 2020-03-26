package com.ecnu2020.achieveit.controller;

import com.ecnu2020.achieveit.annotation.Auth;
import com.ecnu2020.achieveit.entity.Device;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.service.DeviceService;
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
@Api("项目设备管理")
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER)
    @PutMapping("/mod")
    @ApiOperation(value = "修改设备信息",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "设备id", paramType = "query",required = true,dataType = "String"),
    })
    public Object modDeviceInfo(String projectId, @RequestBody @Valid Device device){
        device.setProjectId(projectId);
        return deviceService.modDeviceInfo(device);
    }

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER)
    @PostMapping("/add")
    @ApiOperation(value = "增加设备",response = Device.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "设备id", paramType = "query",required = true,dataType = "String"),
    })
    public Object addDevice(String projectId, @RequestBody @Valid Device device){
        device.setProjectId(projectId);
        return deviceService.addDevice(device);
    }

    @Auth(role = RoleEnum.CONFIGURATION_MANAGER)
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除设备",response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "设备id", paramType = "query",required = true,dataType = "String"),
    })
    public Object delDevice(String projectId,@RequestParam Integer id){
        return deviceService.delDevice(id);
    }

    @GetMapping("/getDevice")
    @ApiOperation(value = "获取设备列表",response = PageInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "设备id", paramType = "query",required = true,dataType = "String"),
    })
    public Object getDevice(String projectId,PageParam pageParam){
        return deviceService.getDeviceList(projectId,pageParam);
    }

}
