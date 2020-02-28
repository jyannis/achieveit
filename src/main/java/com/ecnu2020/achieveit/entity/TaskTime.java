package com.ecnu2020.achieveit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTime {
	private Integer id;
	private Integer featureId;
	private String description;
	private Integer activityId;
	private Timestamp beginTime;
	private Timestamp endTime;
	private Timestamp updateTime;
	private Short status;

}
