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
public class Auth{
	private Integer id;
	private String staffId;
	private String projectId;
	private String role;
	private Short gitAuth;
	private Short fileAuth;
	private Short taskTimeAuth;

}
