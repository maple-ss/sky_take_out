package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.Cacheable;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {


    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品")
    @CacheEvict(cacheNames = "dishCache", key = "#dishDTO.categoryId")
    public Result save(@Valid @RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")//要分页的信息都放在路径之后，并且符合DTO,直接接收即可
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    @GetMapping("/list")//信息直接跟在list?后面，直接拿categoryId和name接收即可
    @ApiOperation("根据分类id或菜品名称查询菜品")
    public Result<List<DishVO>> list(Long categoryId, String name){
        log.info("查询菜品列表, categoryId={}, name={}", categoryId, name);

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setName(name);

        List<DishVO> dishVOS = dishService.listWithFlavor(dish);
        return Result.success(dishVOS);
    }

    @DeleteMapping//一次性在路径?后面传入了多个id，所以用ids接收，但是要把逗号隔开，用RequestParam
    @ApiOperation("批量删除菜品")
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品{}",ids);
        dishService.deletBatch(ids);
        return Result.success();
    }

    /**
     * 在修改菜品时会查询这个菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")//{}格式的固定用PathVariable
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    @CacheEvict(cacheNames = "dishCache",allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售停售菜品")
    public Result startOrStop(@PathVariable("status") Integer status, Long id){
        log.info("起售或停售菜品, id={}, status={}", id, status);
        dishService.startOrStop(status, id);
        return Result.success();
    }

}
