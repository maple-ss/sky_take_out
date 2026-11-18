package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import com.sky.vo.ShoppingCartVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 查询当前用户的购物车
     * @param userId
     * @return
     */
    List<ShoppingCartVO> listByUserId(@Param("userId") Long userId);


    /**
     * 根据id修改主键值
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id} ")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 清空当前用户的购物车
     * @param userId
     */
    void deleteByUserId(@Param("userId") Long userId);
}
