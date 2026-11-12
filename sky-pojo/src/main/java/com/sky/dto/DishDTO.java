package com.sky.dto;

import com.sky.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "新增和修改菜品时传递的数据模型")
public class DishDTO implements Serializable {

    @ApiModelProperty("菜品id，新增时可不传")
    private Long id;

    //菜品名称
    @ApiModelProperty(value = "菜品名称", required = true)
    @NotBlank(message = "菜品名称不能为空")
    private String name;

    //菜品分类id
    @ApiModelProperty(value = "菜品分类id", required = true)
    @NotNull(message = "菜品分类不能为空")
    private Long categoryId;

    //菜品价格
    @ApiModelProperty(value = "菜品价格", required = true)
    @NotNull(message = "菜品价格不能为空")
    @DecimalMin(value = "0.01", message = "菜品价格必须大于0")
    @Digits(integer = 8, fraction = 2, message = "菜品价格最多保留两位小数")
    private BigDecimal price;

    //图片
    @ApiModelProperty(value = "菜品图片路径", required = true)
    @NotBlank(message = "菜品图片不能为空")
    private String image;

    //描述信息
    @ApiModelProperty("菜品描述")
    @Size(max = 255, message = "菜品描述不能超过255个字符")
    private String description;

    //0 停售 1 起售
    @ApiModelProperty("菜品状态：0停售，1起售")
    @Min(value = 0, message = "菜品状态只能为0或1")
    @Max(value = 1, message = "菜品状态只能为0或1")
    private Integer status;

    //口味
    @ApiModelProperty("菜品口味")
    @Valid
    private List<DishFlavor> flavors = new ArrayList<>();

}
