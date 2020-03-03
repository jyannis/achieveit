package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Project;
import com.github.pagehelper.PageInfo;

public interface ProjectService {

    Project build(Project project, String superiorId);

    Boolean review(String projectId, Integer status);

    Boolean deliver(String projectId);

    Boolean close(String projectId);

    Boolean file(String projectId);

    PageInfo<Object> list(String staffId, String beginTime, String endTime, String keyWord, Integer pageNum, Integer count);

    Project get(String projectId);
}
