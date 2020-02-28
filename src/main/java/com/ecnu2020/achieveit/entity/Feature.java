package com.ecnu2020.achieveit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feature{
	private Integer id;
	private String projectId;
	private String feature;
	private String subFeature;
	private String description;

}
