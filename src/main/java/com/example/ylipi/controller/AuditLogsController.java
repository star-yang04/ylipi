package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.AuditLogs;
import com.example.ylipi.service.impl.AuditLogsServiceImpl;
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
@RequestMapping("/auditLogs")
public class AuditLogsController {

    @Autowired
    private AuditLogsServiceImpl auditLogsService;

    /**
     * 分页查询所有审计日志（支持按管理员ID、目标类型过滤）
     */
    @GetMapping("/list")
    public Result listAuditLogs(@RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "10") int pageSize,
                                @RequestParam(required = false) Integer adminId,
                                @RequestParam(required = false) String targetType,
                                @RequestHeader("Authorization") String token) {
        Page<AuditLogs> page = new Page<>(pageNum, pageSize);
        QueryWrapper<AuditLogs> wrapper = new QueryWrapper<>();

        if (adminId != null) {
            wrapper.eq("admin_id", adminId);
        }
        if (targetType != null && !targetType.isEmpty()) {
            wrapper.eq("target_type", targetType);
        }

        wrapper.orderByDesc("action_time");
        auditLogsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 添加一条审计日志（通常由后台自动记录）
     */
    @PostMapping
    public Result addAuditLog(@RequestBody AuditLogs auditLog,
                              @RequestHeader("Authorization") String token) {
        auditLog.setActionTime(LocalDateTime.now());
        boolean saved = auditLogsService.save(auditLog);
        return saved ? Result.success("日志记录成功") : Result.error("日志记录失败");
    }

    /**
     * 根据日志ID获取审计日志详情
     */
    @GetMapping("/{id}")
    public Result getAuditLogById(@PathVariable Integer id,
                                  @RequestHeader("Authorization") String token) {
        AuditLogs log = auditLogsService.getById(id);
        return log != null ? Result.success(log) : Result.error("日志不存在");
    }

    /**
     * 删除某条审计日志（通常不建议暴露，保留接口以备管理）
     */
    @DeleteMapping("/{id}")
    public Result deleteAuditLog(@PathVariable Integer id,
                                 @RequestHeader("Authorization") String token) {
        boolean removed = auditLogsService.removeById(id);
        return removed ? Result.success("日志删除成功") : Result.error("删除失败");
    }
}

