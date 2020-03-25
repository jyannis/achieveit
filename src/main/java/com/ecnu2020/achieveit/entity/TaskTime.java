package com.ecnu2020.achieveit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import java.sql.Timestamp;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTime {
	@Id
	private Integer id;
	@NotNull
	private Integer featureId;
	private String description;
	@NotNull
	private Integer activityId;
	@NotNull
	private Timestamp beginTime;
	@NotNull
	private Timestamp endTime;
	@NotNull
	private Timestamp updateTime;
	private Short status = 0;
	@NotNull
	private String staffId;

}
