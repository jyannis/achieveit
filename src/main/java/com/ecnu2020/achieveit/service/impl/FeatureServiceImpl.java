package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.Page;
import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Feature;
import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.request_response.FeatureResponse;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.FeatureCondition;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.mapper.FeatureMapper;
import com.ecnu2020.achieveit.mapper.ProjectMapper;
import com.ecnu2020.achieveit.service.FeatureService;
import com.ecnu2020.achieveit.util.MakeExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import tk.mybatis.mapper.entity.Example;

/**
 * @description:
 * @author: ganlirong
 * @create: 2020/03/14
 */
@Service
public class FeatureServiceImpl implements FeatureService {

    @Value("${file.excelUrl}")
    private String excelUrl;

    @Autowired
    private FeatureMapper featureMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private MakeExcel<Feature> makeExcel;

    @Override
    public Page<Feature> list(String projectId, FeatureCondition featureCondition,
                                      PageParam pageParam) {
//        Example example=new Example(Feature.class).selectProperties("feature");
//        example.setDistinct(true);
//        example.createCriteria().andEqualTo("projectId",projectId);
//        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
//        List<String> featureList=
//            featureMapper.selectByExample(example).stream().map(feature -> feature.getFeature()).collect(Collectors.toList());
//        //获得父功能的page info
//        PageInfo<String> pageInfo=new PageInfo<>(featureList);
//        List<FeatureResponse> list=new ArrayList<>();
//        for(String feature:featureList){
//            FeatureResponse res=new FeatureResponse();
//            res.setFeature(feature);
//            Example featureExample=new Example(Feature.class);
//            featureExample.createCriteria().andEqualTo("projectId",projectId).andEqualTo("feature",feature);
//            List<Feature> features=featureMapper.selectByExample(featureExample);
//            res.setSubFeatureList(features);
//            list.add(res);
//        }
//        Page page=new Page(pageParam.getPageNum(),pageParam.getPageSize(),pageInfo.getTotal(),
//            pageInfo.getPages(),list);
        Example example=new Example(Feature.class);
        example.createCriteria().andEqualTo("projectId",projectId).andEqualTo("deleted",0);
        example.setOrderByClause("feature asc,id asc");
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize());
        List<Feature> featureList= featureMapper.selectByExample(example);
        return new Page(new PageInfo(featureList));
    }

    @Override
    public Boolean build(List<Feature> featureList) {
        featureList.stream().forEach(feature -> featureMapper.insert(feature));
        return true;
    }

    @Override
    public Boolean update(List<Feature> featureList) {
        featureList.stream().forEach(feature -> featureMapper.updateByPrimaryKey(feature));
        return true;
    }

    @Override
    public Boolean delete(List<Integer> featureIdList) {
        featureIdList.stream().forEach(featureId->{
            Feature feature=featureMapper.selectByPrimaryKey(featureId);
            Optional.ofNullable(feature).orElseThrow(()->new RRException(ExceptionTypeEnum.INVALID_FEATURE));
            feature.setDeleted((short)1);
            featureMapper.updateByPrimaryKey(feature);
        });
        return true;
    }


    @Override
    public String getExcel(String projectId) throws Exception {
        Project old=projectMapper.selectByPrimaryKey(projectId);
        Optional.ofNullable(old).orElseThrow(()->new RRException(ExceptionTypeEnum.PROJECTID_INVALID));
        Example example=new Example(Feature.class);
        example.setOrderByClause("feature asc,id asc");
        example.createCriteria().andEqualTo("projectId",projectId).andEqualTo("deleted",0);
        List<Feature> featureList= featureMapper.selectByExample(example);
        return  makeExcel.makeExcel(featureList,old.getName()+"功能列表",excelUrl);
    }
}
