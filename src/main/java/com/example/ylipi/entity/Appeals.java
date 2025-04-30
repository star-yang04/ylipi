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
@TableName("appeals")
public class Appeals implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "appeal_id", type = IdType.AUTO)
    private Integer appealId;

    @TableField("user_id")
    private Integer userId;

    @TableField("related_id")
    private Integer relatedId;

    @TableField("related_type")
    private String relatedType;

    @TableField("content")
    private String content;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("status")
    private String status;
}
