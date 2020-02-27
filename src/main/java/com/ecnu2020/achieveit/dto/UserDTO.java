package com.ecnu2020.achieveit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * session缓存实体
 * @author yan on 2020-02-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private int id;
    private String name;
    private String department;

}
