package com.ecnu2020.achieveit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff{
	@Id
	private String id;
	@NotNull(message = "员工名不能为空")
	private String name;
	private String password;
	private String email;
	private String department;
	private String tel;
	private Short manager;

}
