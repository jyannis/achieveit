package com.ecnu2020.achieveit.entity.request_response.auth;

import com.ecnu2020.achieveit.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
  * @Author Zc
  * @Description 封装增加和修改Request请求
  * @Date 18:00 2020/3/4
**/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class AddMemberReq {

    @NotNull
    @ApiModelProperty("员工id,不为空")
    private String staffId;
    @ApiModelProperty("角色")
    private String role = RoleEnum.NON.getRoleName();
    @ApiModelProperty("Git权限，默认为0，1读2读写")
    private Short gitAuth = 0;
    @ApiModelProperty("File权限，默认为0，1读2读写")
    private Short fileAuth = 0;
    @ApiModelProperty("提交工时权限，默认为0，1读2读写")
    private Short taskTimeAuth = 0;
}
