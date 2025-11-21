package com.mjcshuai.dao.impl;

import com.mjcshuai.bean.Admin;
import com.mjcshuai.dao.AdminDAO;
import com.mjcshuai.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public Admin login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Admin admin = null;

        try {
            // 调用静态方法获取连接
            conn = DbUtil.getConnection();
            // SQL查询（匹配数据库表字段，假设admin表的创建时间字段为create_date）
            String sql = "SELECT id, name, password, create_date AS createDate FROM admin WHERE name = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            // 封装查询结果
            if (rs.next()) {
                admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setName(rs.getString("name"));
                admin.setPassword(rs.getString("password"));
                admin.setCreateDate(rs.getString("createDate"));
            }
        } catch (SQLException e) {
            System.err.println("管理员登录查询异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 调用静态方法关闭所有资源
            DbUtil.closeAll(conn, pstmt, rs);
        }
        return admin;
    }
}