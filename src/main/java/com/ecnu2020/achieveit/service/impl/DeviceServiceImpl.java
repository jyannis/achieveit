package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Device;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.DeviceMapper;
import com.ecnu2020.achieveit.service.DeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description 实现DeviceService
 * @Author ZC
 **/
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    @Transactional
    public Boolean modDeviceInfo(Device device){
        Device staffExample = deviceMapper.selectByPrimaryKey(device.getId());
        device.setId(staffExample.getId());
        return deviceMapper.updateByPrimaryKey(device) > 0;
    }

    @Override
    @Transactional
    public Device addDevice(Device device){
        Device deviceExample = deviceMapper.selectOne(device);
        if(deviceExample != null) throw new RRException((ExceptionTypeEnum.ADD_DEVICE_FAIL));
        deviceMapper.insertSelective(device);
        return deviceMapper.selectOne(device);
    }

    @Override
    @Transactional
    public Boolean delDevice(Integer id){
        Device deviceExample = deviceMapper.selectByPrimaryKey(id);
        Optional.ofNullable(deviceExample).orElseThrow(()-> new RRException(ExceptionTypeEnum.DELETE_DEVICE_FAIL));
        return deviceMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public PageInfo<Device> getDeviceList(String projectId,PageParam pageParam){
        Example example = new Example(Device.class);
        example.createCriteria().andEqualTo("projectId",projectId);
        List<Integer> deviceIdLiist = deviceMapper.selectByExample(example)
                .stream()
                .map(device -> device.getId())
                .collect(Collectors.toList());
        Example example1 = new Example(Device.class);
        example1.createCriteria().andIn("id",deviceIdLiist);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Device> list = deviceMapper.selectByExample(example1);
        return new PageInfo<>(list);
    }


}
