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
public class Risk{
	private Integer id;
	private String projectId;
	private String type;
	private String description;
	private Integer level;
	private String influence;
	private String strategy;
	private String status;
	private String responsible;
	private String frequency;
	private String related;
	private Timestamp createTime;

}
