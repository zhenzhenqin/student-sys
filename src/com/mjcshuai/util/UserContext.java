package com.mjcshuai.util;

import com.mjcshuai.model.Admin;
import com.mjcshuai.model.Permission;
import com.mjcshuai.model.Student;
import com.mjcshuai.model.Teacher;

/**
 * 用户上下文 - 保存登录后的用户信息和权限
 */
public class UserContext {
    private static UserContext instance; // 单例模式
    private Object loginUser; // 登录用户实体（Admin/Teacher/Student）
    private Permission userPermission; // 登录用户权限

    private UserContext() {}

    // 获取单例实例
    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    // 初始化登录用户信息
    public void initUser(Object user) {
        this.loginUser = user;
        // 根据用户类型设置权限
        if (user instanceof Admin) {
            this.userPermission = Permission.ADMIN;
        } else if (user instanceof Teacher) {
            this.userPermission = Permission.TEACHER;
        } else if (user instanceof Student) {
            this.userPermission = Permission.STUDENT;
        }
    }

    // 清除登录状态（退出登录）
    public void clearUser() {
        this.loginUser = null;
        this.userPermission = null;
    }

    // 检查是否拥有指定权限
    public boolean hasPermission(String permission) {
        return userPermission != null && userPermission.hasPermission(permission);
    }

    // Getter 方法
    public Object getLoginUser() {
        return loginUser;
    }

    public Permission getUserPermission() {
        return userPermission;
    }

    // 获取用户角色名称
    public String getRoleName() {
        if (loginUser instanceof Admin) {
            return "管理员";
        } else if (loginUser instanceof Teacher) {
            return "教师";
        } else if (loginUser instanceof Student) {
            return "学生";
        }
        return "";
    }
}