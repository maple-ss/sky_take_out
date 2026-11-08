package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@ApiModel(description = "员工新增和修改时传递的数据模型")
public class EmployeeDTO implements Serializable {

    @ApiModelProperty("员工id，新增时可不传，修改时必须传")
    private Long id;

    @ApiModelProperty("登录用户名")
    private String username;

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("手机号，11位数字")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String phone;

    @ApiModelProperty("性别：0女，1男")
    private String sex;

    @ApiModelProperty("身份证号码，15位或18位")
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^(\\d{15}|\\d{17}[\\dXx])$", message = "身份证号必须为15位或18位，最后一位可为X")
    private String idNumber;

}
