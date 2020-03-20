package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Bug;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

/**
 * @Description 缺陷部分接口
 * @Author ZC
 **/
public interface BugService {

    Boolean modBug(Bug bug);

    Bug addBug(Bug bug);

    Boolean delBug(Integer id);

    PageInfo<Bug> getBugList(String projectId,PageParam pageParam);
}
