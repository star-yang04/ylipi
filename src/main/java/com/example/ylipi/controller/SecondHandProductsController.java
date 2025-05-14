package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.SecondHandProducts;
import com.example.ylipi.service.impl.SecondHandProductsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/secondHandProducts")
public class SecondHandProductsController {

    @Autowired
    private SecondHandProductsServiceImpl secondHandProductsService;

    /**
     * 新增二手商品
     */
    @PostMapping
    public Result addProduct(@RequestBody SecondHandProducts product,
                             @RequestHeader("Authorization") String token) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        product.setStatus("available"); // 默认上架
        boolean saved = secondHandProductsService.save(product);
        return saved ? Result.success("商品发布成功") : Result.error("发布失败");
    }

    /**
     * 修改商品信息
     */
    @PutMapping
    public Result updateProduct(@RequestBody SecondHandProducts product,
                                @RequestHeader("Authorization") String token) {
        product.setUpdateTime(LocalDateTime.now());
        boolean updated = secondHandProductsService.updateById(product);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除商品（根据id）
     */
    @DeleteMapping("/{id}")
    public Result deleteProduct(@PathVariable Integer id,
                                @RequestHeader("Authorization") String token) {
        boolean removed = secondHandProductsService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据id查询单个商品
     */
    @GetMapping("/{id}")
    public Result getProduct(@PathVariable Integer id,
                             @RequestHeader("Authorization") String token) {
        SecondHandProducts product = secondHandProductsService.getById(id);
        return product != null ? Result.success(product) : Result.error("未找到该商品");
    }

    /**
     * 查询所有商品（分页）
     */
    @GetMapping("/all")
    public Result listProducts(@RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestHeader("Authorization") String token) {
        Page<SecondHandProducts> page = new Page<>(pageNum, pageSize);
        secondHandProductsService.page(page, new QueryWrapper<SecondHandProducts>().orderByDesc("create_time"));
        return Result.success(page);
    }

    /**
     * 查询某个卖家的所有商品（分页）
     */
    @GetMapping("/seller/{sellerId}")
    public Result listBySeller(@PathVariable Integer sellerId,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestHeader("Authorization") String token) {
        Page<SecondHandProducts> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SecondHandProducts> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId).orderByDesc("create_time");
        secondHandProductsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 修改商品上架状态
     */
    @PatchMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id,
                               @RequestParam String status,
                               @RequestHeader("Authorization") String token) {
        SecondHandProducts product = secondHandProductsService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        boolean updated = secondHandProductsService.updateById(product);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}

