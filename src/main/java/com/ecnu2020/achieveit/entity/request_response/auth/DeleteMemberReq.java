package com.ecnu2020.achieveit.entity.request_response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
  * @Author Zc
  * @Description 封装删除人员权限请求
  * @Date 18:00 2020/3/4
**/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteMemberReq {
    @NotNull
    private String staffId;
    @NotNull
    private String role;
}
