package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.PrivateMessages;
import com.example.ylipi.service.impl.PrivateMessagesServiceImpl;
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
@RequestMapping("/privateMessages")
public class PrivateMessagesController {

    @Autowired
    private PrivateMessagesServiceImpl privateMessagesService;

    /**
     * 发送私信
     */
    @PostMapping
    public Result sendMessage(@RequestBody PrivateMessages message,
                              @RequestHeader("Authorization") String token) {
        message.setSendTime(LocalDateTime.now());
        boolean saved = privateMessagesService.save(message);
        return saved ? Result.success("发送成功") : Result.error("发送失败");
    }

    /**
     * 获取当前用户与某人的私信记录（双向）
     */
    @GetMapping("/chat")
    public Result getChatMessages(@RequestParam Integer userId1,
                                  @RequestParam Integer userId2,
                                  @RequestParam(defaultValue = "1") int pageNum,
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  @RequestHeader("Authorization") String token) {
        Page<PrivateMessages> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PrivateMessages> wrapper = new QueryWrapper<>();
        wrapper
                .and(qw -> qw.eq("sender_id", userId1).eq("receiver_id", userId2))
                .or(qw -> qw.eq("sender_id", userId2).eq("receiver_id", userId1))
                .orderByAsc("send_time");
        privateMessagesService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 获取某个用户收到的所有私信（分页）
     */
    @GetMapping("/received/{receiverId}")
    public Result getReceivedMessages(@PathVariable Integer receiverId,
                                      @RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestHeader("Authorization") String token) {
        Page<PrivateMessages> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PrivateMessages> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver_id", receiverId).orderByDesc("send_time");
        privateMessagesService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 删除一条私信（仅发信人或管理员可调用）
     */
    @DeleteMapping("/{id}")
    public Result deleteMessage(@PathVariable Integer id,
                                @RequestHeader("Authorization") String token) {
        boolean removed = privateMessagesService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }
}

