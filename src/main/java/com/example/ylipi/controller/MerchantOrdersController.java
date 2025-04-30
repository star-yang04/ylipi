package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.MerchantOrders;
import com.example.ylipi.service.impl.MerchantOrdersServiceImpl;
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
@RequestMapping("/merchantOrders")
public class MerchantOrdersController {

    @Autowired
    private MerchantOrdersServiceImpl merchantOrdersService;

    /**
     * 添加订单（下单）
     */
    @PostMapping
    public Result createOrder(@RequestBody MerchantOrders order) {
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("待处理");
        boolean saved = merchantOrdersService.save(order);
        return saved ? Result.success("下单成功") : Result.error("下单失败");
    }

    /**
     * 获取所有订单（分页，管理员使用）
     */
    @GetMapping
    public Result getAllOrders(@RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize) {
        Page<MerchantOrders> page = new Page<>(pageNum, pageSize);
        merchantOrdersService.page(page, new QueryWrapper<MerchantOrders>().orderByDesc("order_time"));
        return Result.success(page);
    }

    /**
     * 根据买家ID获取订单
     */
    @GetMapping("/buyer/{buyerId}")
    public Result getOrdersByBuyer(@PathVariable Integer buyerId,
                                   @RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        Page<MerchantOrders> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MerchantOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("buyer_id", buyerId).orderByDesc("order_time");
        merchantOrdersService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 根据商家ID获取订单
     */
    @GetMapping("/merchant/{merchantId}")
    public Result getOrdersByMerchant(@PathVariable Integer merchantId,
                                      @RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        Page<MerchantOrders> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MerchantOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("merchant_id", merchantId).orderByDesc("order_time");
        merchantOrdersService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 根据订单ID获取订单详情
     */
    @GetMapping("/{id}")
    public Result getOrderById(@PathVariable Integer id) {
        MerchantOrders order = merchantOrdersService.getById(id);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }

    /**
     * 修改订单状态（如：已发货、已完成、已取消）
     */
    @PatchMapping("/status/{id}")
    public Result updateOrderStatus(@PathVariable Integer id, @RequestBody String status) {
        MerchantOrders order = new MerchantOrders();
        order.setOrderId(id);
        order.setStatus(status);
        boolean updated = merchantOrdersService.updateById(order);
        return updated ? Result.success("订单状态已更新") : Result.error("更新失败");
    }

    /**
     * 删除订单（仅管理员或商家可操作）
     */
    @DeleteMapping("/{id}")
    public Result deleteOrder(@PathVariable Integer id) {
        boolean removed = merchantOrdersService.removeById(id);
        return removed ? Result.success("订单已删除") : Result.error("删除失败");
    }
}

