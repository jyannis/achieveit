package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.TaskTime;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/11 20:33
 **/
public interface TaskTimeService {
    TaskTime createTaskTime(String projectId,TaskTime taskTime);
    Boolean  ReviewTaskTime(Integer id,Short status);
    Boolean  modTaskTime(String projectId,TaskTime taskTime);
    PageInfo<TaskTime> getTaskTimeList(PageParam pageParam);
}
