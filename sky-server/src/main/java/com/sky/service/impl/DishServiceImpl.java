package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品和口味数据
     * @param dishDTO
     */
    @Transactional//保证整个方法是原子性的
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入一条数据

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        if (dish.getStatus() == null) {
            dish.setStatus(StatusConstant.DISABLE);
        }
        dishMapper.insert(dish);

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            /**
             *  在插入菜品表的时候是要靠数据库生成id，但是这里的口味需要用到id
             *  所以需要在菜品mapper.xml文件对应的插入里加入useGeneratedKeys="true" keyProperty="id"
             *  表示可以让菜品id返回
             *  然后循环一遍，把每个口味都赋上菜品id
             */
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));

            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deletBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE)
            {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size()>0)
        {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

//        for (Long id : ids) {
//            dishFlavorMapper.deleteByDishId(id);
//            dishMapper.deleteById(id);
//        }

        dishFlavorMapper.deleteByDishIds(ids);
        dishMapper.deleteByIds(ids);
    }

    /**
     * 根据id查询菜品和对应的口味
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);

        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }


    /**
     * 根据id修改菜品基本信息和口味信息
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.update(dish);

        Long dishId = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(dishId);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            /**
             *  在插入菜品表的时候是要靠数据库生成id，但是这里的口味需要用到id
             *  所以需要在菜品mapper.xml文件对应的插入里加入useGeneratedKeys="true" keyProperty="id"
             *  表示可以让菜品id返回
             *  然后循环一遍，把每个口味都赋上菜品id
             */
            flavors.forEach(flavor -> flavor.setDishId(dishId));

            dishFlavorMapper.insertBatch(flavors);
        }
    }

}
