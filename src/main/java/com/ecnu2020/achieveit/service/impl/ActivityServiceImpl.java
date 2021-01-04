package com.ecnu2020.achieveit.service.impl;

import com.ecnu2020.achieveit.entity.Activity;
import com.ecnu2020.achieveit.mapper.ActivityMapper;
import com.ecnu2020.achieveit.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<String> sub_activityList(String activity) {
        Example activityExample = new Example(Activity.class);
        if(activity.isEmpty())
            return new ArrayList<String>();
        activityExample.createCriteria().andEqualTo("activity", activity);
        List<String> sub_activityList = activityMapper.selectByExample(activityExample)
                .stream()
                .map(Activity-> Activity.getSubActivity())
                .collect(Collectors.toList());

        return sub_activityList;
    }
}
