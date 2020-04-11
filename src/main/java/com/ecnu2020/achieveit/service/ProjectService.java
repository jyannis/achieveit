package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Project;
import com.ecnu2020.achieveit.entity.request_response.ConfigRequest;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.entity.request_response.condition.ProjectCondition;
import com.github.pagehelper.PageInfo;

public interface ProjectService {

    Project build(Project project, String superiorId, String configManagerId,
                  String qaManagerId, String epgLeaderId);

    Boolean review(String projectId, Integer status);

    Boolean deliver(String projectId);

    Boolean close(String projectId);

    Boolean file(String projectId, Integer status);

    PageInfo<Project> list(ProjectCondition projectCondition,
                          PageParam pageParam);

    Project get(String projectId);

    Boolean update(Project project);

    Boolean delete(String projectId);

    Boolean apply(String projectId);

    Boolean onGoing(String projectId);

    Boolean applyBuild(String projectId);

    Boolean config(ConfigRequest configRequest);
}
