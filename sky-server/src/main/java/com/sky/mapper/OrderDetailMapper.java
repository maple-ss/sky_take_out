package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {

    /**
     * 插入订单明细
     * @param orderDetail
     */
    void insert(OrderDetail orderDetail);
}
