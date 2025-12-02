package com.mjcshuai.dao.impl;

import com.mjcshuai.model.Student;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.constant.DerbySQL;
//import com.mjcshuai.util.DbUtil;
import com.mjcshuai.util.DerbyDbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生管理相关接口
 * @author mjc
 * @date 2025/11/29
 */
public class StudentDAOImpl implements StudentDAO {

    /**
     * 学生登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public Student login(String username, String password) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            //conn = DbUtil.getConnection();
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
            System.err.println("Student login query error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //DbUtil.closeAll(conn, pstmt, rs);
            DerbyDbUtil.closeAll(rs,pstmt, DerbyConn);
        }
        return student;
    }

    /**
     * 查询所有学生
     * @return 所有学生列表
     */
    @Override
    public List<Student> findAllStudents() {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Student> studentList = new ArrayList<>();

        try {
            //conn = DbUtil.getConnection();
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
            System.err.println("Query all student exceptions: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //DbUtil.closeAll(conn, pstmt, rs);
            DerbyDbUtil.closeAll(rs,pstmt, DerbyConn);
        }
        return studentList;
    }

    /**
     * 新增学生
     * @param student 学生对象
     * @return 是否新增成功
     */
    @Override
    public boolean addStudent(Student student) {
        //Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            //conn = DbUtil.getConnection();
            //String sql = "INSERT INTO student (name, class_id, sex, password) VALUES (?, ?, ?, ?)";
            DerbyConn = DerbyDbUtil.getConnection();
            pstmt = DerbyConn.prepareStatement(DerbySQL.addStudentSQL);
            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getClassId());
            pstmt.setString(3, student.getSex());
            pstmt.setString(4, student.getPassword());
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("Add student exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null,pstmt, DerbyConn);
        }
    }

    /**
     * 更新学生
     * @param student 学生对象
     * @return 是否更新成功
     */
    @Override
    public boolean updateStudent(Student student) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            //conn = DbUtil.getConnection();
            //String sql = "UPDATE student SET name = ?, class_id = ?, sex = ?, password = ? WHERE id = ?";
            DerbyConn = DerbyDbUtil.getConnection();
            pstmt = DerbyConn.prepareStatement(DerbySQL.updateStudentSQL);
            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getClassId());
            pstmt.setString(3, student.getSex());
            pstmt.setString(4, student.getPassword());
            pstmt.setInt(5, student.getId());
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("Update student exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null,pstmt, DerbyConn);
        }
    }

    /**
     * 删除学生
     * @param id 学生ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteStudent(Integer id) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            //conn = DbUtil.getConnection();
            //String sql = "DELETE FROM student WHERE id = ?";
            DerbyConn = DerbyDbUtil.getConnection();
            pstmt = DerbyConn.prepareStatement(DerbySQL.deleteStudentSQL);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("Delete student exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null,pstmt, DerbyConn);
        }
    }
}