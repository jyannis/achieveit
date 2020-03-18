package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.dto.UserDTO;
import com.ecnu2020.achieveit.entity.Auth;
import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.BugEnum;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
import com.ecnu2020.achieveit.enums.RoleEnum;
import com.ecnu2020.achieveit.mapper.AuthMapper;
import com.ecnu2020.achieveit.mapper.RiskMapper;
import com.ecnu2020.achieveit.mapper.StaffMapper;
import com.ecnu2020.achieveit.service.RiskService;
import com.ecnu2020.achieveit.util.SendMail;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 实现RiskService
 * @Author ZC
 **/
@Slf4j
@Service
public class RiskServiceImpl implements RiskService {

    @Autowired
    private RiskMapper riskMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private SendMail sendMail;

    final private String message =  "今天是每周的风险识别追踪日，请召集相关人员识别和跟踪风险";
    final private String subject = "风险警告";

    @Override
    @Transactional
    public Boolean modRisk(Risk risk){
        return riskMapper.updateByPrimaryKey(risk) > 0;
    }

    @Override
    @Transactional
    public Risk addRisk(Risk risk){
        if(riskMapper.selectOne(risk)!=null) throw  new RRException(ExceptionTypeEnum.ADD_RISK_FAIL);
        riskMapper.insertSelective(risk);
        return riskMapper.selectOne(risk);
    }

    @Override
    public PageInfo<Risk> getRiskList(String projectId,PageParam pageParam){
        Example example = new Example(Risk.class);
        example.createCriteria().andEqualTo("projectId",projectId);
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Risk> bugList = riskMapper.selectByExample(example);
        return new PageInfo<>(bugList);
    }

    @Override
    public void setRiskMail(){
        String str = BugEnum.NON.getStatus() + BugEnum.GOING.getStatus();
        List<String> listProject = riskMapper.selectAll()
                .stream().filter(risk -> str.contains(risk.getStatus()))
                .map(risk -> risk.getProjectId())
                .distinct()
                .collect(Collectors.toList());
        if(listProject.isEmpty()) return;
        Example example = new Example(Auth.class);
        example.createCriteria().andIn("projectId",listProject);
        List<String> staffId = authMapper.selectByExample(example)
                .stream()
                .filter(auth -> auth.getRole().equals(RoleEnum.PROJECT_MANAGER.getRoleName()))
                .map(auth -> auth.getStaffId())
                .collect(Collectors.toList());
        if(staffId.isEmpty()) return;
        Example example1 = new Example(Staff.class);
        example1.createCriteria().andIn("id",staffId);
        List<String> emailList = staffMapper.selectByExample(example1)
                .stream().map(staff -> staff.getEmail()).collect(Collectors.toList());
        while(emailList.iterator().hasNext()){
            try {
                sendMail.sendMail(emailList.iterator().next(), subject, message);
            }catch(Exception e){
                log.warn(e.getMessage());
            }
        }
    }
}
