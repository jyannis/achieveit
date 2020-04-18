package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.StaffVO;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.StaffService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public PageInfo<Staff> importStaff(String projectId,PageParam pageParam){
        if(projectId != null) {
            String Role = RoleEnum.PROJECT_MANAGER.getRoleName() + RoleEnum.QA_MANAGER.getRoleName() + RoleEnum.EPG_LEADER.getRoleName();
            UserDTO currentUser = (UserDTO) SecurityUtils.getSubject().getPrincipal();
            Auth auth = Auth.builder().projectId(projectId).staffId(currentUser.getId()).build();
            Auth testAuth = authMapper.selectOne(auth);
            if (!Role.contains(testAuth.getRole())) throw new RRException(ExceptionTypeEnum.PERMISSION_DENIED);
        }
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
    public PageInfo<StaffVO> getProjectStaff(String projectId, String keyword, PageParam pageParam) {
        Auth authExample = Auth.builder().projectId(projectId).build();
        List<String> staffId = authMapper.select(authExample)
                .stream()
                .map(auth ->  auth.getStaffId())
                .collect(Collectors.toList());
        Map<String,String> staffRoleMap = authMapper.select(authExample)
            .stream()
            .collect(Collectors.toMap(Auth::getStaffId, Auth::getRole));
        if(staffId.isEmpty()) return new PageInfo<>();
        Example example = new Example(Staff.class);
        example.createCriteria().andIn("id",staffId);
        List<String> id = staffMapper.selectByExample(example)
                .stream()
                .filter(staff -> staff.toString().contains(keyword))
                .map(staff -> staff.getId())
                .collect(Collectors.toList());
        if(id.isEmpty()) return new PageInfo<>();
        Example example1 = new Example(Staff.class);
        example1.createCriteria().andIn("id",id);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Staff> stafflist = staffMapper.selectByExample(example1);
        List<StaffVO> staffVOList=new ArrayList<>();
        stafflist.stream()
            .forEach(staff -> {
                StaffVO staffVO=new StaffVO();
                BeanUtils.copyProperties(staff,staffVO);
                staffVO.setRole(staffRoleMap.get(staff.getId()));
                staffVOList.add(staffVO);
            });
        PageInfo<Staff> origin=new PageInfo<>(stafflist);
        PageInfo<StaffVO> res=new PageInfo<>();
        BeanUtils.copyProperties(origin,res);
        res.setList(staffVOList);

        return res;
    }
}
