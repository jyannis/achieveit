package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.StaffService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
  * @Author ZC
  * @Description 实现StaffService
**/
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private AuthMapper authMapper;

    @Override
    public Staff login(String id, String password) {
        Staff staff = Staff.builder()
                .id(id)
                .password(password)
                .build();
        return staffMapper.selectOne(staff);
    }

    @Override
    public PageInfo<Staff> importStaff(PageParam pageParam){
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Staff> staffList = staffMapper.selectAll();
        return new PageInfo<>(staffList);
    }

    @Override
    @Transactional
    public Boolean modStaffInfo(Staff staff){
        Staff staff1 = staffMapper.selectByPrimaryKey(staff.getId());
        staff.setPassword(staff1.getPassword());
       return  staffMapper.updateByPrimaryKey(staff) > 0;
    }

    @Override
    public PageInfo<Staff> getProjectStaff(String projectId, String keyword, PageParam pageParam) {
        Auth authExample = Auth.builder().projectId(projectId).build();
        List<String> staffId = authMapper.select(authExample)
                .stream()
                .map(auth ->  auth.getStaffId())
                .collect(Collectors.toList());
        Example example = new Example(Staff.class);
        example.createCriteria().andIn("id",staffId);
        List<String> id = staffMapper.selectByExample(example)
                .stream()
                .filter(staff -> staff.toString().contains(keyword))
                .map(staff -> staff.getId())
                .collect(Collectors.toList());
        if(id.isEmpty()) return new PageInfo<>(null);
        Example example1 = new Example(Staff.class);
        example1.createCriteria().andIn("id",id);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Staff> stafflist = staffMapper.selectByExample(example1);
        return new PageInfo<>(stafflist);
    }
}
