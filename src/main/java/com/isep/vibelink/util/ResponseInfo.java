package com.isep.vibelink.util;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用响应封装类（泛型版），用于统一返回格式。
 * 包含消息、操作状态和返回数据。
 *
 * @param <T> 返回数据的类型
 */
@Setter
@Getter
public class ResponseInfo<T> {
    private String msg;
    private Boolean ok;
    private T data;

    public ResponseInfo(String msg, Boolean ok, T data) {
        this.msg = msg;
        this.ok = ok;
        this.data = data;
    }

    /**
     * 快速创建成功响应。
     *
     * @param msg  成功信息
     * @param data 返回数据
     * @param <T>  数据类型
     * @return ResponseInfo 实例
     */
    public static <T> ResponseInfo<T> success(String msg,T data) {
        return new ResponseInfo<>(msg, true, data);
    }

    /**
     * 快速创建失败响应。
     *
     * @param msg 失败信息
     * @param <T> 数据类型（返回 null）
     * @return ResponseInfo 实例
     */
    public static <T> ResponseInfo<T> fail(String msg) {
        return new ResponseInfo<>(msg, false, null);
    }
}
