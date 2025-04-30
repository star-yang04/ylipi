package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.Tasks;
import com.example.ylipi.service.impl.TasksServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/tasks")
public class TasksController {

    @Autowired
    private TasksServiceImpl tasksService;

    /**
     * 根据任务 ID 查询任务详情
     */
    @GetMapping("/{id}")
    public Result getTaskById(@PathVariable Integer id) {
        Tasks task = tasksService.getById(id);
        return task != null ? Result.success(task) : Result.error("任务不存在");
    }

    /**
     * 查询所有任务（支持分页）
     */
    @GetMapping("/list")
    public Result listTasks(@RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Tasks> page = new Page<>(pageNum, pageSize);
        tasksService.page(page, new QueryWrapper<>());
        return Result.success(page);
    }

    /**
     * 新增任务
     */
    @PostMapping
    public Result addTask(@RequestBody Tasks task) {
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        boolean saved = tasksService.save(task);
        return saved ? Result.success("任务发布成功") : Result.error("发布失败");
    }

    /**
     * 更新任务
     */
    @PutMapping
    public Result updateTask(@RequestBody Tasks task) {
        task.setUpdateTime(LocalDateTime.now());
        boolean updated = tasksService.updateById(task);
        return updated ? Result.success("任务更新成功") : Result.error("更新失败");
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public Result deleteTask(@PathVariable Integer id) {
        boolean removed = tasksService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 修改任务状态
     */
    @PatchMapping("/status/{id}")
    public Result updateTaskStatus(@PathVariable Integer id, @RequestParam String status) {
        Tasks task = new Tasks();
        task.setTaskId(id);
        task.setStatus(status);
        task.setUpdateTime(LocalDateTime.now());
        boolean updated = tasksService.updateById(task);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    /**
     * 根据发布者 ID 查询其发布的所有任务
     */
    @GetMapping("/publisher/{publisherId}")
    public Result getTasksByPublisher(@PathVariable Integer publisherId) {
        QueryWrapper<Tasks> wrapper = new QueryWrapper<>();
        wrapper.eq("publisher_id", publisherId);
        List<Tasks> tasks = tasksService.list(wrapper);
        return Result.success(tasks);
    }

}

