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
@TableName("audit_logs")
public class AuditLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    @TableField("admin_id")
    private Integer adminId;

    @TableField("operation")
    private String operation;

    @TableField("target_id")
    private Integer targetId;

    @TableField("target_type")
    private String targetType;

    @TableField("description")
    private String description;

    @TableField("action_time")
    private LocalDateTime actionTime;
}
