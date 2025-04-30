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
@TableName("task_acceptance")
public class TaskAcceptance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "acceptance_id", type = IdType.AUTO)
    private Integer acceptanceId;

    @TableField("task_id")
    private Integer taskId;

    @TableField("accepter_id")
    private Integer accepterId;

    @TableField("accept_time")
    private LocalDateTime acceptTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    @TableField("status")
    private String status;
}
