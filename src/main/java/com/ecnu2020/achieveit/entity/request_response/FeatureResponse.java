package com.ecnu2020.achieveit.entity.request_response;

import com.ecnu2020.achieveit.entity.Feature;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: ganlirong
 * @create: 2020/03/14
 */
@Data
public class FeatureResponse {
    String feature;
    List<Feature> subFeatureList;

}
