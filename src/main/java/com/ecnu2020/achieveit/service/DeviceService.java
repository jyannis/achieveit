package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Device;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description 设备部分接口
 * @Author ZC
 * @Date 2020/3/10 19:58
 **/
public interface DeviceService {

    Boolean modDeviceInfo(Device device);

    Device addDevice(Device device);

    Boolean delDevice(Integer id);

    PageInfo<Device> getDeviceList(String projectId,PageParam pageParam);
}
