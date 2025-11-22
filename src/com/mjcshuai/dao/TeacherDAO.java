package com.mjcshuai.dao;

import com.mjcshuai.bean.Teacher;

import java.util.List;

public interface TeacherDAO {
    Teacher login(String username, String password); // 登录
    List<Teacher> findAllTeachers(); // 查询所有教师
    boolean addTeacher(Teacher teacher); // 新增教师
    boolean updateTeacher(Teacher teacher); // 更新教师
    boolean deleteTeacher(Integer id); // 删除教师
}