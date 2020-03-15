package com.ecnu2020.achieveit.entity;

import com.ecnu2020.achieveit.enums.ProjectStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project{
	@NotEmpty
	@Id
	private String id;
	@NotEmpty
	private String name;
	private String description;
	private String customerInfo;
	private Timestamp beginTime;
	private Timestamp endTime;
	private String technology;
	private String business;
	private String feature;
	private String status= ProjectStatusEnum.BUILD.getStatus();
	@JsonIgnore
	private Short deleted=0;

}
