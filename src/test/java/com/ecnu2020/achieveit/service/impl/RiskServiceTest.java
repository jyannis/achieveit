package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.BugEnum;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.RiskMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.impl.RiskServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.entity.Config;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @Description test BugService Api
 * @Author ZC
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RiskServiceTest {

    @Mock
    private RiskMapper riskMapper;

    @InjectMocks
    private RiskServiceImpl riskServiceimp;

    @Mock
    private StaffMapper staffMapper;

    private Risk risk;
    private PageParam pageParam;
    private Staff staff;

    @Before
    public void setUp(){
        risk = new Risk();
        pageParam = new PageParam();
        staff = new Staff();
        EntityHelper.initEntityNameMap(Staff.class, new Config());
        risk = Risk.builder().projectId("test").id(1).status(BugEnum.GOING.getStatus()).related("|test|test1|").build();
        when(riskMapper.deleteByPrimaryKey(any())).thenReturn(1);
        when(riskMapper.insertSelective(any())).thenReturn(1);
        when(riskMapper.updateByPrimaryKey(any())).thenReturn(1);
    }

    @Test
    public void testAddRisk(){
        when(riskMapper.selectOne(any())).thenReturn(null);
        Risk testRisk = riskServiceimp.addRisk(any());
        Assert.assertNull(testRisk);
    }

    @Test
    public void testDeleteRisk(){
        Assert.assertTrue(riskServiceimp.delRisk(anyInt()));
    }

    @Test
    public void testUpdateRisk(){
        Assert.assertTrue(riskServiceimp.modRisk(risk));
    }

    @Test
    public void testGetRiskList(){
        risk.setId(1);
        List<Risk> listRisk = new ArrayList<>();
        listRisk.add(risk);
        when(riskMapper.select(any())).thenReturn(listRisk);
        Assert.assertEquals(riskServiceimp.getRiskList(anyString(),pageParam).getPageNum(),1);
    }

    @Test
    public void testAddRiskFail(){
        try{
            riskServiceimp.addRisk(any());
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_RISK_FAIL,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void testSendRiskMail(){
        staff.setId("1");
        staff.setEmail("testMail");
        List<Staff> listStaff = new ArrayList<>();
        List<Risk> list = new ArrayList<>();
        list.add(risk);
        listStaff.add(staff);
        when(riskMapper.selectAll()).thenReturn(list);
        when(staffMapper.selectByExample(any())).thenReturn(listStaff);
        riskServiceimp.setRiskMail();
        Assert.assertTrue(true);
    }

}
