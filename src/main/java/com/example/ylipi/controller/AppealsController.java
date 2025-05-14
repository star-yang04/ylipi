package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.Appeals;
import com.example.ylipi.service.impl.AppealsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/appeals")
public class AppealsController {

    @Autowired
    private AppealsServiceImpl appealsService;

    /**
     * 根据指定id获取申诉记录
     */
    @GetMapping("/selectById/{id}")
    public Result getAppealsById(@PathVariable Integer id,
                                 @RequestHeader("Authorization") String token) {
        Appeals appeal = appealsService.getById(id);
        return Result.success(appeal);
    }

    /**
     * 查询所有申诉记录
     */
    @GetMapping("/list")
    public Result getAllAppeals(@RequestHeader("Authorization") String token) {
        List<Appeals> list = appealsService.list();
        return Result.success(list);
    }

    /**
     * 新增申诉记录
     */
    @PostMapping("/add")
    public Result addAppeal(@RequestBody Appeals appeal,
                            @RequestHeader("Authorization") String token) {
        appeal.setCreateTime(LocalDateTime.now());
        boolean saved = appealsService.save(appeal);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改申诉记录
     */
    @PutMapping("/update")
    public Result updateAppeal(@RequestBody Appeals appeal,
                               @RequestHeader("Authorization") String token) {
        boolean updated = appealsService.updateById(appeal);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除申诉记录
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteAppeal(@PathVariable Integer id,
                               @RequestHeader("Authorization") String token) {
        boolean removed = appealsService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据用户ID查询申诉记录
     */
    @GetMapping("/user/{userId}")
    public Result getAppealsByUserId(@PathVariable Integer userId,
                                     @RequestHeader("Authorization") String token) {
        QueryWrapper<Appeals> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Appeals> appealsList = appealsService.list(queryWrapper);
        return Result.success(appealsList);
    }

    /**
     * 分页查询申诉记录（支持按用户ID、状态、类型、内容模糊等查询）
     * 前端请求示例：GET /appeals/page?pageNum=1&pageSize=5&userId=101&status=待处理&content=举报
     */
    @GetMapping("/page")
    public Result getAppealsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String relatedType,
            @RequestParam(required = false) String content ,// 模糊搜索
            @RequestHeader("Authorization") String token
    ) {
        Page<Appeals> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Appeals> queryWrapper = new QueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (relatedType != null && !relatedType.isEmpty()) {
            queryWrapper.eq("related_type", relatedType);
        }
        if (content != null && !content.isEmpty()) {
            queryWrapper.like("content", content);
        }

        Page<Appeals> resultPage = appealsService.page(page, queryWrapper);
        return Result.success(resultPage);
    }

    /**
     * 查询各状态申诉数量（如：待处理、已处理等）
     */
    @GetMapping("/statusCount")
    public Result getAppealStatusCount(@RequestHeader("Authorization") String token) {
        List<Map<String, Object>> result = appealsService.listMaps(
                new QueryWrapper<Appeals>().select("status", "count(*) as count").groupBy("status")
        );
        return Result.success(result);
    }

    /**
     * 快速更新申诉状态
     *请求示例：
     * axios.patch('/appeals/status/123', null, {
     *   params: {
     *     status: '已处理'
     *   }
     * }).then(res => {
     *   console.log(res.data);
     * });
     */
    @PatchMapping("/status/{id}")
    public Result updateAppealStatus(@PathVariable Integer id, @RequestParam String status,
                                     @RequestHeader("Authorization") String token) {
        Appeals appeal = new Appeals();
        appeal.setAppealId(id);
        appeal.setStatus(status);
        boolean updated = appealsService.updateById(appeal);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }




}

