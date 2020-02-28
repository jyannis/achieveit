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
public class Project{
	private String id;
	private String name;
	private String description;
	private String customerInfo;
	private Timestamp beginTime;
	private Timestamp endTime;
	private String technology;
	private String business;
	private String feature;
	private String status;

}
