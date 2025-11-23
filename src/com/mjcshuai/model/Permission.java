package com.mjcshuai.model;

/**
 * 权限枚举类 - 优化后与功能模块精准对应
 * author:mjc
 * date:2025-11-21
 */
public enum Permission {
    // 管理员：所有功能权限
    ADMIN(
            new String[]{
                    // 个人信息
                    "view_own_info",      // 查看个人信息
                    "edit_own_info",      // 编辑个人信息
                    // 学生管理
                    "view_all_students",  // 查看所有学生
                    "add_student",        // 新增学生
                    "edit_student",       // 编辑学生
                    "delete_student",     // 删除学生
                    // 教师管理
                    "view_all_teachers",  // 查看所有教师
                    "add_teacher",        // 新增教师
                    "edit_teacher",       // 编辑教师
                    "delete_teacher",     // 删除教师
                    // 课程管理
                    "view_all_courses",   // 查看所有课程
                    "add_course",         // 新增课程
                    "edit_course",        // 编辑课程
                    "delete_course",      // 删除课程
                    "assign_teacher_course", // 分配教师授课
                    // 系统管理
                    "manage_system"       // 系统参数设置
            }
    ),

    // 学生：仅个人相关和选课功能
    STUDENT(
            new String[]{
                    // 个人信息
                    "view_own_info",      // 查看个人信息
                    "edit_own_info",      // 编辑个人信息
                    // 课程相关
                    "view_all_courses",   // 查看所有可选课程
                    "view_selected_courses", // 查看已选课程
                    "select_course",      // 选课
                    "drop_course"         // 退课
            }
    ),

    // 教师：个人信息 + 授课管理 + 成绩管理
    TEACHER(
            new String[]{
                    // 个人信息
                    "view_own_info",      // 查看个人信息
                    "edit_own_info",      // 编辑个人信息
                    // 课程相关
                    "view_teaching_courses", // 查看自己教授的课程
                    "edit_own_course",    // 编辑自己的课程信息
                    "view_course_students",  // 查看课程选课学生
                    // 成绩管理
                    "grade_students"      // 给学生打分
            }
    );

    private final String[] permissions;

    Permission(String[] permissions) {
        this.permissions = permissions;
    }

    public String[] getPermissions() {
        return permissions;
    }

    // 检查是否拥有指定权限
    public boolean hasPermission(String permission) {
        for (String p : permissions) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }
}