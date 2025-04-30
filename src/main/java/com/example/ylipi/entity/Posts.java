package com.example.ylipi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@Getter
@Setter
@ToString
@TableName("posts")
public class Posts implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "post_id", type = IdType.AUTO)
    private Integer postId;

    @TableField("user_id")
    private Integer userId;

    @TableField("is_anonymous")
    private Boolean isAnonymous;

    @TableField("category")
    private String category;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("status")
    private String status;
}
