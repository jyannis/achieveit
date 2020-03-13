package com.ecnu2020.achieveit.entity;

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
public class Device{

	@Id
	private Integer id;
	@NotNull(message = "设备名不为空")
	private String name;
	private String admin;
	private String borrower;
	@NotNull(message = "不能为空")
	private Timestamp endTime;
	private String status = "已归还";

}
