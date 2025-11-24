package com.mjcshuai.dao.impl;

import com.mjcshuai.model.Student;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.resource.DerbySQL;
import com.mjcshuai.util.DbUtil;
import com.mjcshuai.util.DerbyDbUtil;

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
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            // 注意：Student类的classId字段对应表中的class_id（下划线转驼峰）
            //String sql = "SELECT id, name, class_id AS classId, sex, password FROM student WHERE name = ? AND password = ?";
            //pstmt = conn.prepareStatement(sql);
            pstmt = DerbyConn.prepareStatement(DerbySQL.studentLoginSQL);
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
            DerbyDbUtil.closeAll(rs,pstmt, DerbyConn);
        }
        return student;
    }

    // StudentDAOImpl.java 实现类
    @Override
    public List<Student> findAllStudents() {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Student> studentList = new ArrayList<>();

        try {
            conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            //String sql = "SELECT id, name, class_id AS classId, sex, password FROM student";
            pstmt = DerbyConn.prepareStatement(DerbySQL.queryAllStudentSQL);
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
            DerbyDbUtil.closeAll(rs,pstmt, DerbyConn);
        }
        return studentList;
    }

    @Override
    public boolean addStudent(Student student) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DbUtil.getConnection();
            //String sql = "INSERT INTO student (name, class_id, sex, password) VALUES (?, ?, ?, ?)";
            DerbyConn = DerbyDbUtil.getConnection();
            pstmt = DerbyConn.prepareStatement(DerbySQL.addStudentSQL);
            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getClassId());
            pstmt.setString(3, student.getSex());
            pstmt.setString(4, student.getPassword());
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("新增学生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null,pstmt, DerbyConn);
        }
    }

    // 更新学生
    @Override
    public boolean updateStudent(Student student) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE student SET name = ?, class_id = ?, sex = ?, password = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getClassId());
            pstmt.setString(3, student.getSex());
            pstmt.setString(4, student.getPassword());
            pstmt.setInt(5, student.getId());
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("更新学生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null,pstmt, DerbyConn);
        }
    }

    // 删除学生
    @Override
    public boolean deleteStudent(Integer id) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM student WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("删除学生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null,pstmt, DerbyConn);
        }
    }
}