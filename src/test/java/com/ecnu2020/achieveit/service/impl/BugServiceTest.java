package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Bug;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.BugMapper;
import com.ecnu2020.achieveit.service.impl.BugServiceImpl;
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
 * @Description test BugService Api
 * @Author ZC
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BugServiceTest {
    @Mock
    private BugMapper bugMapper;

    @InjectMocks
    private BugServiceImpl bugServiceimp;

    private Bug bug;
    private PageParam pageParam;

    @Before
    public void setUp(){
        bug = new Bug();
        pageParam = new PageParam();
        bug.setProjectId("testId");
        bug.setId(1);

        when(bugMapper.selectOne(any())).thenReturn(null);
        when(bugMapper.deleteByPrimaryKey(any())).thenReturn(1);
        when(bugMapper.updateByPrimaryKey(any())).thenReturn(1);
    }

    @Test
    public void testAddBug(){
        Bug testBug = bugServiceimp.addBug(any());
        Assert.assertNull(testBug);
    }

    @Test
    public void testDeleteBug(){
        Assert.assertTrue(bugServiceimp.delBug(anyInt()));
    }

    @Test
    public void testUpdateBug(){
        Assert.assertTrue(bugServiceimp.modBug(bug));
    }

    @Test
    public void testGetBugList(){
        List<Bug> listBug = new ArrayList<>();
        listBug.add(bug);
        when(bugMapper.select(any())).thenReturn(listBug);
        Assert.assertEquals(bugServiceimp.getBugList(anyString(),pageParam).getPageNum(),1);
    }

    @Test
    public void  updateBugFail(){
        try{
            bugServiceimp.modBug(bug);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.UPDATE_BUG_FAIL,e.getExceptionTypeEnum());
        }
    }

    @Test
    public void addBugFail(){
        try{
            bugServiceimp.addBug(bug);
        }catch (RRException e){
            Assert.assertEquals(ExceptionTypeEnum.ADD_BUD_FAIL,e.getExceptionTypeEnum());
        }
    }
}
