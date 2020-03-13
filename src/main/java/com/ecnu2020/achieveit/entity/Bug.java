package com.ecnu2020.achieveit.entity;

import com.ecnu2020.achieveit.enums.BugEnum;
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
public class Bug{
	@Id
	private Integer id;
	private String projectId;
	@NotNull(message = "缺陷名不为空")
	private String name;
	private String description;
	private Integer level = 1;
	private String tracker;
//	@NotNull(message = "更新时间不为空")
	private Timestamp updateTime;
	private String status = BugEnum.NON.getStatus();

}
