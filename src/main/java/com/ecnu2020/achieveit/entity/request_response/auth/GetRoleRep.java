package com.ecnu2020.achieveit.entity.request_response.auth;

import com.ecnu2020.achieveit.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author ZC
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class GetRoleRep {
    @ApiModelProperty("员工id")
    private String staffId;
    @ApiModelProperty("角色")
    private String role = RoleEnum.NON.getRoleName();
}
