package com.mjcshuai.dao;

import com.mjcshuai.bean.Student;

import java.util.List;

public interface StudentDAO {
    Student login(String username, String password);
    List<Student> findAllStudents(); // 查询所有学生
    boolean addStudent(Student student); // 新增学生
    boolean updateStudent(Student student); // 更新学生
    boolean deleteStudent(Integer id); // 删除学生
}