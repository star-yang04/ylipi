package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.SensitiveWords;
import com.example.ylipi.service.impl.SensitiveWordsServiceImpl;
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
@RequestMapping("/sensitiveWords")
public class SensitiveWordsController {

    @Autowired
    private SensitiveWordsServiceImpl sensitiveWordsService;

    /**
     * 新增敏感词
     */
    @PostMapping
    public Result addWord(@RequestBody SensitiveWords word,
                          @RequestHeader("Authorization") String token) {
        word.setCreateTime(LocalDateTime.now());
        boolean saved = sensitiveWordsService.save(word);
        return saved ? Result.success("敏感词添加成功") : Result.error("敏感词添加失败");
    }

    /**
     * 删除敏感词
     */
    @DeleteMapping("/{id}")
    public Result deleteWord(@PathVariable Integer id,
                             @RequestHeader("Authorization") String token) {
        boolean removed = sensitiveWordsService.removeById(id);
        return removed ? Result.success("敏感词删除成功") : Result.error("敏感词删除失败");
    }

    /**
     * 修改敏感词内容
     */
    @PutMapping
    public Result updateWord(@RequestBody SensitiveWords word,
                             @RequestHeader("Authorization") String token) {
        boolean updated = sensitiveWordsService.updateById(word);
        return updated ? Result.success("敏感词更新成功") : Result.error("敏感词更新失败");
    }

    /**
     * 查询所有敏感词（分页）
     */
    @GetMapping
    public Result listWords(@RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize,
                            @RequestHeader("Authorization") String token) {
        Page<SensitiveWords> page = new Page<>(pageNum, pageSize);
        sensitiveWordsService.page(page);
        return Result.success(page);
    }

    /**
     * 根据关键词模糊搜索敏感词
     */
    @GetMapping("/search")
    public Result searchWord(@RequestParam String keyword,
                             @RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestHeader("Authorization") String token) {
        Page<SensitiveWords> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SensitiveWords> wrapper = new QueryWrapper<>();
        wrapper.like("word", keyword);
        sensitiveWordsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 获取敏感词详情（根据ID）
     */
    @GetMapping("/{id}")
    public Result getWord(@PathVariable Integer id,
                          @RequestHeader("Authorization") String token) {
        SensitiveWords word = sensitiveWordsService.getById(id);
        return word != null ? Result.success(word) : Result.error("敏感词不存在");
    }
}

