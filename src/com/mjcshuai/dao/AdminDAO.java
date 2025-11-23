package com.mjcshuai.dao;

import com.mjcshuai.model.Admin;

public interface AdminDAO {
    // 根据用户名和密码查询管理员
    Admin login(String username, String password);
}