package com.ecnu2020.achieveit.entity.request_response.condition;

import com.ecnu2020.achieveit.enums.ProjectStatusEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import java.lang.ref.PhantomReference;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ganlirong
 * @create: 2020/03/08
 */
@ApiModel
@Data
public class ProjectCondition {
    @ApiModelProperty("关键字，非必传")
    private String keyWord="";
    @ApiModelProperty("项目状态，默认全部状态")
    private List<String> status=
         Arrays.stream(ProjectStatusEnum.values()).map(e -> e.getStatus()).collect(Collectors.toList());
}
