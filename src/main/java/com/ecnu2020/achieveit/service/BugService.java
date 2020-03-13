package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Bug;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

/**
 * @Description
 * @Author ZC
 * @Date 2020/3/11 14:40
 **/
public interface BugService {

    Boolean modBug(Bug bug);

    Bug addBug(Bug bug);

    PageInfo<Bug> getBugList(String projectId,PageParam pageParam);
}
