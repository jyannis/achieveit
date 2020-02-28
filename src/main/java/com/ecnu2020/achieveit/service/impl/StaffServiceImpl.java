package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    StaffMapper staffMapper;

    @Override
    public Staff login(String id, String password) {
        Staff staff = Staff.builder()
                .id(id)
                .password(password)
                .build();
        return staffMapper.selectOne(staff);
    }
}
