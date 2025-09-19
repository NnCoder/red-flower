package com.redflower.common.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    public Result() {
        this.timestamp = LocalDateTime.now();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success");
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "系统错误");
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权");
    }

    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问");
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源不存在");
    }
}