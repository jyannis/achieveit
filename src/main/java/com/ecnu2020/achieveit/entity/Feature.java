package com.ecnu2020.achieveit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feature{
	@Id
	private Integer id;
	@NotEmpty
	private String projectId;
	private String feature;
	private String subFeature;
	private String description;
	@JsonIgnore
	private Short deleted=0;

}
