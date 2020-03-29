package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Device;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.DeviceMapper;
import com.ecnu2020.achieveit.service.impl.DeviceServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @Description test DeviceService Api
 * @Author ZC
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DeviceServiceTest {

    @Mock
    private DeviceMapper deviceMapper;

    @InjectMocks
    private DeviceServiceImpl deviceServiceImp;

    private Device device;
    private PageParam pageParam;

    @Before
    public void setUp(){
        device = new Device();
        pageParam = new PageParam();
        device.setId(1);

        when(deviceMapper.updateByPrimaryKey(any())).thenReturn(1);
        when(deviceMapper.selectOne(any())).thenReturn(null);
        when(deviceMapper.insertSelective(any())).thenReturn(1);
        when(deviceMapper.selectByPrimaryKey(anyInt())).thenReturn(device);
        when(deviceMapper.deleteByPrimaryKey(anyInt())).thenReturn(1);
    }

    @Test
    public void testModDeviceInfo(){
        Boolean bool = deviceServiceImp.modDeviceInfo(device);
        Assert.assertTrue(bool);
    }

    @Test
    public void testAddDevice(){
        Device testDevice = deviceServiceImp.addDevice(device);
        Assert.assertNull(testDevice);
    }

    @Test
    public void testDelDevice(){
        Boolean bool = deviceServiceImp.delDevice(anyInt());
        Assert.assertTrue(bool);
    }

    @Test
    public void testGetDeviceList(){
        List<Device> list = new ArrayList<>();
        list.add(device);
        when(deviceMapper.select(any())).thenReturn(list);
        when(deviceMapper.selectByExample(any())).thenReturn(list);
        Assert.assertEquals(deviceServiceImp.getDeviceList(anyString(),pageParam).getPageNum(),1);
    }

    @Test
    public void testAddFail(){
        when(deviceMapper.selectOne(any())).thenReturn(null);
        try{
            deviceServiceImp.addDevice(device);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_DEVICE_FAIL,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testDelFail(){
        when(deviceMapper.selectByPrimaryKey(anyInt())).thenReturn(null);
        try{
            deviceServiceImp.delDevice(anyInt());
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.DELETE_DEVICE_FAIL,e.getExceptionTypeEnum());
        }
    }
}
