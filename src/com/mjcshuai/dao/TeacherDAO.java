package com.mjcshuai.dao;

import com.mjcshuai.bean.Teacher;

import java.util.List;

public interface TeacherDAO {
    Teacher login(String username, String password);
    List<Teacher> findAllTeachers();
}