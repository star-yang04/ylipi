package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.TaskAcceptance;
import com.example.ylipi.service.impl.TaskAcceptanceServiceImpl;
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
@RequestMapping("/taskAcceptance")
public class TaskAcceptanceController {

    @Autowired
    private TaskAcceptanceServiceImpl taskAcceptanceService;

    /**
     * 添加任务接收记录
     */
    @PostMapping
    public Result addAcceptance(@RequestBody TaskAcceptance acceptance) {
        acceptance.setAcceptTime(LocalDateTime.now());
        boolean saved = taskAcceptanceService.save(acceptance);
        return saved ? Result.success("任务接收记录添加成功") : Result.error("添加失败");
    }

    /**
     * 更新任务接收记录
     */
    @PutMapping
    public Result updateAcceptance(@RequestBody TaskAcceptance acceptance) {
        boolean updated = taskAcceptanceService.updateById(acceptance);
        return updated ? Result.success("任务接收记录更新成功") : Result.error("更新失败");
    }

    /**
     * 删除任务接收记录
     */
    @DeleteMapping("/{id}")
    public Result deleteAcceptance(@PathVariable Integer id) {
        boolean deleted = taskAcceptanceService.removeById(id);
        return deleted ? Result.success("任务接收记录删除成功") : Result.error("删除失败");
    }

    /**
     * 根据 ID 查询任务接收记录
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        TaskAcceptance acceptance = taskAcceptanceService.getById(id);
        return acceptance != null ? Result.success(acceptance) : Result.error("记录未找到");
    }

    /**
     * 分页查询所有记录
     */
    @GetMapping
    public Result listAll(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "10") int pageSize) {
        Page<TaskAcceptance> page = new Page<>(pageNum, pageSize);
        taskAcceptanceService.page(page);
        return Result.success(page);
    }

    /**
     * 根据任务ID或接收者ID筛选记录（可选参数）
     */
    @GetMapping("/search")
    public Result search(@RequestParam(required = false) Integer taskId,
                         @RequestParam(required = false) Integer accepterId,
                         @RequestParam(required = false) String status,
                         @RequestParam(defaultValue = "1") int pageNum,
                         @RequestParam(defaultValue = "10") int pageSize) {

        QueryWrapper<TaskAcceptance> wrapper = new QueryWrapper<>();
        if (taskId != null) wrapper.eq("task_id", taskId);
        if (accepterId != null) wrapper.eq("accepter_id", accepterId);
        if (status != null && !status.isEmpty()) wrapper.eq("status", status);

        Page<TaskAcceptance> page = new Page<>(pageNum, pageSize);
        taskAcceptanceService.page(page, wrapper);
        return Result.success(page);
    }
}

