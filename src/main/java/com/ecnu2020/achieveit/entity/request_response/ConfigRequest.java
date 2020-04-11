package com.ecnu2020.achieveit.entity.request_response;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description: 配置项目
 * @author: ganlirong
 * @create: 2020/04/11
 */
@Data
public class ConfigRequest {
    @NotEmpty
    private String id;
    private String gitPath;
    private String filePath;
    private String vmSpace;
}
