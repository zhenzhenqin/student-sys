package com.mjcshuai.dao.impl;

import com.mjcshuai.bean.Teacher;
import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // 新增：查询所有教师
    @Override
    public List<Teacher> findAllTeachers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Teacher> teacherList = new ArrayList<>();

        try {
            // 获取数据库连接（调用静态工具类）
            conn = DbUtil.getConnection();
            // SQL查询所有教师信息
            String sql = "SELECT id, name, sex, title, age, password FROM teacher ORDER BY id ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // 遍历结果集，封装Teacher对象
            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setName(rs.getString("name"));
                teacher.setSex(rs.getString("sex"));
                teacher.setTitle(rs.getString("title"));
                teacher.setAge(rs.getInt("age"));
                teacher.setPassword(rs.getString("password"));
                teacherList.add(teacher);
            }
        } catch (SQLException e) {
            System.err.println("查询所有教师异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            DbUtil.closeAll(conn, pstmt, rs);
        }
        return teacherList;
    }
}