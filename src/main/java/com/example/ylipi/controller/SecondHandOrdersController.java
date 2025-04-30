package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.SecondHandOrders;
import com.example.ylipi.service.impl.SecondHandOrdersServiceImpl;
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
@RequestMapping("/secondHandOrders")
public class SecondHandOrdersController {

    @Autowired
    private SecondHandOrdersServiceImpl secondHandOrdersService;

    /**
     * 新增订单（下单）
     */
    @PostMapping
    public Result createOrder(@RequestBody SecondHandOrders order) {
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("pending"); // 初始状态
        boolean saved = secondHandOrdersService.save(order);
        return saved ? Result.success("下单成功") : Result.error("下单失败");
    }

    /**
     * 更新订单状态（如完成、取消）
     */
    @PatchMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        SecondHandOrders order = secondHandOrdersService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        order.setStatus(status);
        boolean updated = secondHandOrdersService.updateById(order);
        return updated ? Result.success("订单状态更新成功") : Result.error("更新失败");
    }

    /**
     * 根据订单ID查询订单详情
     */
    @GetMapping("/{id}")
    public Result getOrderById(@PathVariable Integer id) {
        SecondHandOrders order = secondHandOrdersService.getById(id);
        return order != null ? Result.success(order) : Result.error("订单不存在");
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public Result deleteOrder(@PathVariable Integer id) {
        boolean removed = secondHandOrdersService.removeById(id);
        return removed ? Result.success("订单删除成功") : Result.error("删除失败");
    }

    /**
     * 查询买家的所有订单
     */
    @GetMapping("/buyer/{buyerId}")
    public Result listOrdersByBuyer(@PathVariable Integer buyerId,
                                    @RequestParam(defaultValue = "1") int pageNum,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        Page<SecondHandOrders> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SecondHandOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("buyer_id", buyerId).orderByDesc("order_time");
        secondHandOrdersService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 查询卖家的所有订单
     */
    @GetMapping("/seller/{sellerId}")
    public Result listOrdersBySeller(@PathVariable Integer sellerId,
                                     @RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        Page<SecondHandOrders> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SecondHandOrders> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId).orderByDesc("order_time");
        secondHandOrdersService.page(page, wrapper);
        return Result.success(page);
    }
}

