package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.Comments;
import com.example.ylipi.service.impl.CommentsServiceImpl;
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
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsServiceImpl commentsService;

    /**
     * 根据帖子ID获取所有评论
     */
    @GetMapping("/post/{postId}")
    public Result getCommentsByPostId(@PathVariable Integer postId,
                                      @RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        Page<Comments> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Comments> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).orderByAsc("create_time"); // 按时间升序排列
        commentsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 根据评论ID获取单条评论
     */
    @GetMapping("/{id}")
    public Result getCommentById(@PathVariable Integer id) {
        Comments comment = commentsService.getById(id);
        return comment != null ? Result.success(comment) : Result.error("评论不存在");
    }

    /**
     * 创建一条评论
     */
    @PostMapping
    public Result addComment(@RequestBody Comments comment) {
        comment.setCreateTime(LocalDateTime.now());
        boolean saved = commentsService.save(comment);
        return saved ? Result.success("评论成功") : Result.error("评论失败");
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        boolean removed = commentsService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新评论内容
     */
    @PutMapping("/{id}")
    public Result updateComment(@PathVariable Integer id, @RequestBody String newContent) {
        Comments comment = new Comments();
        comment.setCommentId(id);
        comment.setContent(newContent);
        comment.setCreateTime(LocalDateTime.now()); // 更新评论时间
        boolean updated = commentsService.updateById(comment);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }
}

