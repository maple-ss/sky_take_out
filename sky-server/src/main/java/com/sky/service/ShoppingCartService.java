package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.vo.ShoppingCartVO;

import java.util.List;

public interface ShoppingCartService {

    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCartVO> showShoppingCart();

    void cleanShoppingCart();

    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
