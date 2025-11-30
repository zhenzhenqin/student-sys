package com.mjcshuai.context;

// 基于ThreadLocal封装的线程变量 用于存储当前登录的用户信息
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //存储当前登录用户的id
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //获取当前登录用户的id
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    //移除当前线程存储的id
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
