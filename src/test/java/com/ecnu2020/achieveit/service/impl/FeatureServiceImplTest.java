package com.ecnu2020.achieveit.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecnu2020.achieveit.AchieveitApplication;
import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Feature;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.FeatureCondition;
import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.ecnu2020.achieveit.mapper.FeatureMapper;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.util.MakeExcel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * @description:
 * @author: ganlirong
 * @create: 2020/03/29
 */
@RunWith(SpringRunner.class)
//@PowerMockRunnerDelegate(SpringRunner.class)
//@PrepareForTest({SecurityUtils.class})//静态类mock
@SpringBootTest(classes = AchieveitApplication.class)
public class FeatureServiceImplTest {
    @MockBean
    private FeatureMapper featureMapper;

    @MockBean
    private ProjectMapper projectMapper;

//    @MockBean
    private MakeExcel<Feature> makeExcel;

    @Autowired
    private FeatureServiceImpl featureService;

    private PageParam pageParam;
    private Project project;
    private Feature feature;
    private List<Feature> featureList;

    @Before
    public void setUp() {
        makeExcel=mock(MakeExcel.class);
        pageParam = PageParam.builder().pageSize(10).pageNum(1).build();
        project =
            Project.builder().id("0318testPro").name("项目1").business("业务1").feature("特色功能").description("描述")
            .status(ProjectStatusEnum.BUILD.getStatus()).technology("spring").deleted((short) 0).build();
        feature=Feature.builder().id(1).feature("主功能").subFeature("子功能").description("功能描述")
            .projectId("0318testPro").deleted((short)0).build();
        Feature feature1=Feature.builder().id(2).feature("主功能2").subFeature("子功能2").description(
            "功能描述2")
            .projectId("0318testPro").deleted((short)0).build();
        featureList=Arrays.asList(feature,feature1);
        EntityHelper.initEntityNameMap(Feature.class, new Config());
    }

    @Test
    public void list() {
        FeatureCondition featureCondition = new FeatureCondition();
        when(featureMapper.selectByExample(any())).thenReturn(Arrays.asList(feature));
        //Execute
        featureService.list(project.getId(), featureCondition,pageParam);
        verify(featureMapper, times(1)).selectByExample(any());
    }

    @Test
    public void build() {
        when(featureMapper.insertSelective(any())).thenReturn(1);
        //Execute
        Boolean res=featureService.build(featureList);
        verify(featureMapper, times(2)).insertSelective(any());
        assertTrue(res);
    }

    @Test
    public void update() {
        when(featureMapper.updateByPrimaryKey(any())).thenReturn(1);
        //Execute
        Boolean res=featureService.update(featureList);
        verify(featureMapper, times(2)).updateByPrimaryKey(any());
        assertTrue(res);
    }

    @Test
    public void delete_when_feature_exist() {
        when(featureMapper.selectByPrimaryKey(any())).thenReturn(feature);
        when(featureMapper.updateByPrimaryKey(any())).thenReturn(1);
        //Execute
        List<Integer> featureIdList=Arrays.asList(1,2);
        Boolean res=featureService.delete(featureIdList);
        ArgumentCaptor<Feature> featureArgumentCaptor=ArgumentCaptor.forClass(Feature.class);
        verify(featureMapper, times(2)).updateByPrimaryKey(featureArgumentCaptor.capture());
        assertEquals((short)1,featureArgumentCaptor.getValue().getDeleted().shortValue());
        assertTrue(res);
    }

    @Test(expected = RRException.class)
    public void delete_when_feature_not_exist() {
        when(featureMapper.selectByPrimaryKey(any())).thenReturn(null);
        when(featureMapper.updateByPrimaryKey(any())).thenReturn(1);
        //Execute
        List<Integer> featureIdList=Arrays.asList(1,2);
        featureService.delete(featureIdList);
        verify(featureMapper, times(0)).updateByPrimaryKey(any());
    }

    @Test
    public void get_excel_when_project_exist() throws Exception {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(project);
        when(featureMapper.selectByExample(any())).thenReturn(featureList);
//        when(makeExcel.makeExcel(any(),anyString(),anyString())).thenReturn("功能列表");
        //Execute
        String res=featureService.getExcel(project.getId());
        verify(featureMapper, times(1)).selectByExample(any());
//        verify(makeExcel.makeExcel(any(),stringArgumentCaptor.capture(),anyString()));
        assertEquals("/file/achieveit/excel/项目1功能列表.xlsx",res);
    }

    @Test(expected = RRException.class)
    public void get_excel_when_project_not_exist() throws Exception {
        when(projectMapper.selectByPrimaryKey(anyString())).thenReturn(null);
        when(featureMapper.selectByExample(any())).thenReturn(featureList);
        when(makeExcel.makeExcel(any(),anyString(),anyString())).thenReturn("功能列表");
        //Execute
        featureService.getExcel("2");
        verify(featureMapper, times(0)).selectByExample(any());
    }

    @Test
    public void get_template() {
        //Execute
        String res=featureService.getTemplate();
        assertEquals("/file/achieveit/excel/功能模板.xlsx",res);
    }

}
