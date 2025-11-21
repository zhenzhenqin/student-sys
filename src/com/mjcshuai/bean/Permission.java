package com.mjcshuai.bean;

/**
 * 权限枚举类
 * 用于定义不同用户角色的访问权限
 * author:mjc
 * date:2025-11-21
 */
public enum Permission {
    // 管理员权限 - 可以访问所有功能
    ADMIN(
        new String[]{
            "view_students",      // 查看学生信息
            "edit_students",      // 编辑学生信息
            "delete_students",    // 删除学生信息
            "view_teachers",      // 查看教师信息
            "edit_teachers",      // 编辑教师信息
            "delete_teachers",    // 删除教师信息
            "view_courses",       // 查看课程信息
            "edit_courses",       // 编辑课程信息
            "delete_courses",     // 删除课程信息
            "assign_teachers",    // 分配教师
            "manage_system"       // 系统管理
        }
    ),
    
    // 学生权限 - 只能访问特定功能
    STUDENT(
        new String[]{
            "view_own_info",      // 查看自己的信息
            "edit_own_info",      // 编辑自己的信息
            "view_selected_courses", // 查看已选课程
            "select_courses",     // 选课
            "drop_courses"        // 退课
        }
    ),
    
    // 教师权限 - 访问教学相关功能
    TEACHER(
        new String[]{
            "view_own_info",      // 查看自己的信息
            "edit_own_info",      // 编辑自己的信息
            "view_teaching_courses", // 查看教授的课程
            "view_course_students",  // 查看课程学生名单
            "grade_students",     // 给学生打分
            "edit_course_info"    // 编辑课程信息
        }
    );

    private final String[] permissions;

    Permission(String[] permissions) {
        this.permissions = permissions;
    }

    /**
     * 获取角色权限列表
     * @return 权限数组
     */
    public String[] getPermissions() {
        return permissions;
    }

    /**
     * 检查是否拥有指定权限
     * @param permission 权限名称
     * @return 是否拥有权限
     */
    public boolean hasPermission(String permission) {
        for (String p : permissions) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }
}
