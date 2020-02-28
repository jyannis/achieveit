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
public class Device{
	private Integer id;
	private String name;
	private String admin;
	private String borrower;
	private Timestamp endTime;
	private String status;

}
