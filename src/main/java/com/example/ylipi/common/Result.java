package com.example.ylipi.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能
 * 作者：yangxin
 * 日期：2025/4/29 23:56
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    public static final String CODE_SUCCESS = "200";
    public static final String CODE_AUTH_ERROR = "401";
    public static final String CODE_SYS_ERROR = "500";

    /**
     * 请求的返回编码 200 401(没权限) 404(没资源) 500(系统错误)
     * 编码表示这次请求是成功还是失败
     * 或者说 可以看出失败的类型是什么
     */
    /**
     * 404接口路径写错了或参数写错了
     * 405接口请求类型错误了
     * 500即后台错误，应该去看后端控制台找出错误
     */

    private String code;

    /**
     * msg表示错误的详细信息
     */
    private String msg;

    /**
     * 数据从什么地方出去的
     * 就是这个data
     * user Object类型就是User
     * List Object类型就是List
     * Map  Object类型就是Map
     */
    private Object data;




    public static Result success() {
        return Result.builder().code(CODE_SUCCESS).msg("请求成功").data(null).build();
    }

    public static Result success(Object data) {
        return Result.builder().code(CODE_SUCCESS).msg("请求成功").data(data).build();
    }

    public static Result error(String msg) {
        return Result.builder().code(CODE_SYS_ERROR).msg(msg).data(null).build();
    }

    public static Result error(String code, String msg) {
        return Result.builder().code(code).msg(msg).data(null).build();
    }

    public static Result error(String code, String msg, Object data) {
        return Result.builder().code(CODE_SUCCESS).msg("系统错误").data(null).build();
    }


}
