package com.ecnu2020.achieveit.entity;


import com.ecnu2020.achieveit.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

/**
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auth{
	@Id
	private Integer id;
	private String staffId;
	private String projectId;
	private String role = RoleEnum.NON.getRoleName();
	private Short gitAuth = 0;
	private Short fileAuth = 0;
	private Short taskTimeAuth = 0;

}
