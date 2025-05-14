package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.MerchantProducts;
import com.example.ylipi.service.impl.MerchantProductsServiceImpl;
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
@RequestMapping("/merchantProducts")
public class MerchantProductsController {

    @Autowired
    private MerchantProductsServiceImpl merchantProductsService;

    /**
     * 分页获取所有商品（支持按商品类型筛选）
     */
    @GetMapping
    public Result getAllProducts(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 @RequestParam(required = false) String productType,
                                 @RequestHeader("Authorization") String token) {
        Page<MerchantProducts> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MerchantProducts> wrapper = new QueryWrapper<>();
        if (productType != null && !productType.isEmpty()) {
            wrapper.eq("product_type", productType);
        }
        wrapper.orderByDesc("create_time");
        merchantProductsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 根据商品ID获取商品详情
     */
    @GetMapping("/{id}")
    public Result getProductById(@PathVariable Integer id,
                                 @RequestHeader("Authorization") String token) {
        MerchantProducts product = merchantProductsService.getById(id);
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    /**
     * 根据商户ID获取该商户的所有商品
     */
    @GetMapping("/merchant/{merchantId}")
    public Result getProductsByMerchant(@PathVariable Integer merchantId,
                                        @RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestHeader("Authorization") String token) {
        Page<MerchantProducts> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MerchantProducts> wrapper = new QueryWrapper<>();
        wrapper.eq("merchant_id", merchantId).orderByDesc("create_time");
        merchantProductsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 添加新商品
     */
    @PostMapping
    public Result addProduct(@RequestBody MerchantProducts product,
                             @RequestHeader("Authorization") String token) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        boolean saved = merchantProductsService.save(product);
        return saved ? Result.success("新增商品成功") : Result.error("新增失败");
    }

    /**
     * 更新商品信息
     */
    @PutMapping
    public Result updateProduct(@RequestBody MerchantProducts product,
                                @RequestHeader("Authorization") String token) {
        product.setUpdateTime(LocalDateTime.now());
        boolean updated = merchantProductsService.updateById(product);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result deleteProduct(@PathVariable Integer id,
                                @RequestHeader("Authorization") String token) {
        boolean removed = merchantProductsService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 修改商品状态（上架/下架）
     */
    @PatchMapping("/status/{id}")
    public Result updateProductStatus(@PathVariable Integer id, @RequestBody String status,
                                      @RequestHeader("Authorization") String token) {
        MerchantProducts product = new MerchantProducts();
        product.setProductId(id);
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        boolean updated = merchantProductsService.updateById(product);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}

