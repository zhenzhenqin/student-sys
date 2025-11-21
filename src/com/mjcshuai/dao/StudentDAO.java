package com.mjcshuai.dao;

import com.mjcshuai.bean.Student;

import java.util.List;

public interface StudentDAO {
    Student login(String username, String password);
    List<Student> findAllStudents();
    List<Student> findStudentsByCourseId(Integer courseId);
}