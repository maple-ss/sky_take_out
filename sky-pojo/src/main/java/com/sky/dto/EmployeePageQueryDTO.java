package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@ApiModel(description = "员工分页查询参数")
public class EmployeePageQueryDTO implements Serializable {

    @ApiModelProperty(value = "员工姓名，支持模糊查询", example = "张三")
    //员工姓名
    private String name;

    @ApiModelProperty(value = "页码", required = true, example = "1")
    @Min(value = 1, message = "页码必须大于等于1")
    //页码
    private int page;

    @ApiModelProperty(value = "每页记录数", required = true, example = "10")
    @Min(value = 1, message = "每页记录数必须大于等于1")
    //每页显示记录数
    private int pageSize;

}
