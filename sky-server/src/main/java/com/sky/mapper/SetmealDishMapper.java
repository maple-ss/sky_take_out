package com.sky.mapper;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 查看当下ids的菜品对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(@Param("dishIds") List<Long> dishIds);

    /**
     * 批量插入套餐菜品
     * @param setmealDishes
     */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 批量删除关联ids的菜品
     * @param ids
     */
    void delete(@Param("ids") List<Long> ids);

    /**
     * 查询关联id的菜品
     * @param id
     * @return
     */
    List<SetmealDish> getDishById(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId} ")
    void deleteById(Long setmealId);

    @Select("select count(*) from setmeal_dish sd " +
            "left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId} and d.status = 0")
    Integer countDisabledDishBySetmealId(Long setmealId);

    @Select("select count(distinct s.id) from setmeal_dish sd " +
            "inner join setmeal s on sd.setmeal_id = s.id " +
            "where sd.dish_id = #{dishId} and s.status = 1")
    Integer countEnabledSetmealByDishId(Long dishId);
}
