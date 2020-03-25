package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.common.RRException;
import com.ecnu2020.achieveit.entity.Risk;
import com.ecnu2020.achieveit.entity.Staff;
import com.ecnu2020.achieveit.entity.request_response.common.PageParam;
import com.ecnu2020.achieveit.enums.BugEnum;
import com.ecnu2020.achieveit.enums.ExceptionTypeEnum;
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

import java.util.*;
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
    private StaffMapper staffMapper;

    @Autowired
    private SendMail sendMail;

    final private String message =  "今天是每周的风险识别追踪日，请召集相关人员识别和跟踪风险";
    final private String relatedMessage = "您有待处理的风险，请及时处理";
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
    @Transactional
    public Boolean delRisk(Integer id){
        return  riskMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public PageInfo<Risk> getRiskList(String projectId,PageParam pageParam){
        Risk risk = Risk.builder().projectId(projectId).build();
        PageHelper.startPage(pageParam.getPageNum(),pageParam.getPageSize(),pageParam.getOrderBy());
        List<Risk> riskList = riskMapper.select(risk);
        return new PageInfo<>(riskList);
    }

    @Override
    public void setRiskMail(){
        String str = BugEnum.NON.getStatus() + BugEnum.GOING.getStatus();
        List<String> riskResponlist = riskMapper.selectAll()
                .stream().filter(risk -> str.contains(risk.getStatus()))
                .map(risk -> risk.getResponsible())
                .distinct()
                .collect(Collectors.toList());

        Set<String> related = getRelatedStaff(str);
        if(related.isEmpty()) return;
        Iterator<String> iterator = getEmailList(new ArrayList<>(related));
        /**
          * @Description 发送邮件给风险相关者
        **/
        while(iterator.hasNext()){
            try {
                sendMail.sendMail(iterator.next(), subject, relatedMessage);
            }catch(Exception e){
                log.warn(e.getMessage());
            }
        }

        if(riskResponlist.isEmpty()) return;
        iterator = getEmailList(riskResponlist);
        /**
          * 发送邮件给项目负责人
        **/
        while(iterator.hasNext()){
            try {
                sendMail.sendMail(iterator.next(), subject, message);
            }catch(Exception e){
                log.warn(e.getMessage());
            }
        }
    }

    /**
      * @Description 发送邮件给风险相关者
    **/
    public Set<String> getRelatedStaff(String str){
        List<String> relateList = riskMapper.selectAll()
                .stream().filter(risk -> str.contains(risk.getStatus()))
                .map(risk -> risk.getRelated())
                .distinct()
                .collect(Collectors.toList());
        Set<String> relatedStaff = new HashSet<>();
        Iterator<String>  e = relateList.iterator();
        while(e.hasNext()){
            List<String> relateId = Arrays.asList((e.next()).split("\\|"));
            for(int i = 1; i < relateId.size(); i++){
                if(!relatedStaff.contains(relateId.get(i))){
                    relatedStaff.add(relateId.get(i));
                }
            }
        }
        return relatedStaff;
    }

    /**
      * @Description 得到list<staff> 的email迭代
    **/
    public Iterator<String> getEmailList(List<String> staffId){
        Example example = new Example(Staff.class);
        example.createCriteria().andIn("id",staffId);
        List<String> emailList = staffMapper.selectByExample(example)
                .stream().map(staff -> staff.getEmail()).collect(Collectors.toList());
        Iterator<String> iterator = emailList.iterator();
        return iterator;
    }
}
