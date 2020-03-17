package com.ecnu2020.achieveit.service;

import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.github.pagehelper.PageInfo;

import java.util.List;
/**
  * @Author ZC
  * @Description 人员信息部分接口
**/
public interface StaffService {

    Staff login(String id, String password);

    PageInfo<Staff>  importStaff(PageParam pageParam);

    Boolean modStaffInfo(Staff staff);

    PageInfo<Staff> getProjectStaff(String projectId, String keyword, PageParam pageParam);

}
