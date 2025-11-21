package com.mjcshuai.dao;

import com.mjcshuai.bean.Teacher;

public interface TeacherDAO {
    Teacher login(String username, String password);
}