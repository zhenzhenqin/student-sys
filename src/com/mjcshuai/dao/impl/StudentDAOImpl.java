package com.mjcshuai.dao.impl;

import com.mjcshuai.bean.Student;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public Student login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            conn = DbUtil.getConnection();
            // 注意：Student类的classId字段对应表中的class_id（下划线转驼峰）
            String sql = "SELECT id, name, class_id AS classId, sex, password FROM student WHERE name = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setClassId(rs.getInt("classId"));
                student.setSex(rs.getString("sex"));
                student.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("学生登录查询异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(conn, pstmt, rs);
        }
        return student;
    }

    // StudentDAOImpl.java 实现类
    @Override
    public List<Student> findAllStudents() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Student> studentList = new ArrayList<>();

        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT id, name, class_id AS classId, sex, password FROM student";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setClassId(rs.getInt("classId"));
                student.setSex(rs.getString("sex"));
                student.setPassword(rs.getString("password"));
                studentList.add(student);
            }
        } catch (SQLException e) {
            System.err.println("查询所有学生异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(conn, pstmt, rs);
        }
        return studentList;
    }

    @Override
    public List<Student> findStudentsByCourseId(Integer courseId) {
        // 实现：查询课程下的学生（关联student_course表）
        return new ArrayList<>(); // 实际需对接数据库
    }
}