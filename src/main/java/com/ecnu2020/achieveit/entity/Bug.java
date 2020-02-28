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
public class Bug{
	private Integer id;
	private String projectId;
	private String name;
	private String description;
	private Integer level;
	private String tracker;
	private Timestamp updateTime;
	private String status;

}
