package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.Posts;
import com.example.ylipi.service.impl.PostsServiceImpl;
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
@RequestMapping("/posts")
public class PostsController {

    @Autowired
    private PostsServiceImpl postsService;

    /**
     * 根据帖子ID获取帖子详情
     */
    @GetMapping("/{id}")
    public Result getPostById(@PathVariable Integer id,
                              @RequestHeader("Authorization") String token) {
        Posts post = postsService.getById(id);
        return post != null ? Result.success(post) : Result.error("帖子不存在");
    }

    /**
     * 分页获取所有帖子（可按分类过滤）
     */
    @GetMapping("/list")
    public Result listPosts(@RequestParam(defaultValue = "1") int pageNum,
                            @RequestParam(defaultValue = "10") int pageSize,
                            @RequestParam(required = false) String category,
                            @RequestHeader("Authorization") String token) {
        Page<Posts> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Posts> wrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        wrapper.orderByDesc("create_time");
        postsService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 新增帖子
     */
    @PostMapping
    public Result addPost(@RequestBody Posts post,
                          @RequestHeader("Authorization") String token) {
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        post.setStatus("正常");
        boolean saved = postsService.save(post);
        return saved ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 修改帖子
     */
    @PutMapping
    public Result updatePost(@RequestBody Posts post,
                             @RequestHeader("Authorization") String token) {
        post.setUpdateTime(LocalDateTime.now());
        boolean updated = postsService.updateById(post);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public Result deletePost(@PathVariable Integer id,
                             @RequestHeader("Authorization") String token) {
        boolean removed = postsService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新帖子状态（如封禁、隐藏）
     */
    @PatchMapping("/status/{id}")
    public Result updatePostStatus(@PathVariable Integer id,
                                   @RequestParam String status,
                                   @RequestHeader("Authorization") String token) {
        Posts post = new Posts();
        post.setPostId(id);
        post.setStatus(status);
        post.setUpdateTime(LocalDateTime.now());
        boolean updated = postsService.updateById(post);
        return updated ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    /**
     * 获取某个用户发布的所有帖子
     */
    @GetMapping("/user/{userId}")
    public Result getPostsByUserId(@PathVariable Integer userId,
                                   @RequestHeader("Authorization") String token) {
        QueryWrapper<Posts> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        List<Posts> list = postsService.list(wrapper);
        return Result.success(list);
    }

}

