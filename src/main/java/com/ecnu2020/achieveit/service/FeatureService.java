package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.common.Page;
import com.ecnu2020.achieveit.entity.Feature;
import com.ecnu2020.achieveit.entity.request_response.FeatureResponse;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.FeatureCondition;

import java.util.List;

public interface FeatureService {
    Page<Feature> list(String projectId, FeatureCondition featureCondition,
                               PageParam pageParam);

    Boolean build(List<Feature> featureList);

    Boolean update(List<Feature> featureList);

    Boolean delete(List<Integer> featureIdList);

    String getExcel(String projectId) throws Exception;
}
