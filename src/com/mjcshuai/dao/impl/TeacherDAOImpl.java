package com.mjcshuai.dao.impl;

import com.mjcshuai.bean.Teacher;
import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherDAOImpl implements TeacherDAO {

    @Override
    public Teacher login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Teacher teacher = null;

        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT id, name, sex, title, age, password FROM teacher WHERE name = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setName(rs.getString("name"));
                teacher.setSex(rs.getString("sex"));
                teacher.setTitle(rs.getString("title"));
                teacher.setAge(rs.getInt("age"));
                teacher.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("教师登录查询异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(conn, pstmt, rs);
        }
        return teacher;
    }
}