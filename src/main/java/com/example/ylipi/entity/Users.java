package com.example.ylipi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("role")
    private String role;

    @TableField("user_type")
    private String userType;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("school_number")
    private String schoolNumber;

    @TableField("school_password")
    private String schoolPassword;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("credit_score")
    private Integer creditScore;

    @TableField("account_balance")
    private BigDecimal accountBalance;
}
