package com.mjcshuai.dao;

import com.mjcshuai.bean.Student;

public interface StudentDAO {
    Student login(String username, String password);
}