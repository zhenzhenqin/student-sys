package com.mjcshuai.dao.impl;

import com.mjcshuai.model.Teacher;
import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.resource.DerbySQL;
import com.mjcshuai.util.DbUtil;
import com.mjcshuai.util.DerbyDbUtil;

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
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Teacher teacher = null;

        try {
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            pstmt = DerbyConn.prepareStatement(DerbySQL.teacherLoginSQL);
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
            //DbUtil.closeAll(conn, pstmt, rs);
            DerbyDbUtil.closeAll(rs, pstmt, DerbyConn);
        }
        return teacher;
    }

    // 新增：查询所有教师
    @Override
    public List<Teacher> findAllTeachers() {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Teacher> teacherList = new ArrayList<>();

        try {
            // 获取数据库连接（调用静态工具类）
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            // SQL查询所有教师信息
            //String sql = "SELECT id, name, sex, title, age, password FROM teacher ORDER BY id ASC";
            pstmt = DerbyConn.prepareStatement(DerbySQL.queryAllTeacherSQL);
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
            //DbUtil.closeAll(conn, pstmt, rs);
            DerbyDbUtil.closeAll(rs, pstmt, DerbyConn);
        }
        return teacherList;
    }

    // 新增教师
    @Override
    public boolean addTeacher(Teacher teacher) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            // SQL新增教师信息
            //String sql = "INSERT INTO teacher (name, sex, title, age, password) VALUES (?, ?, ?, ?, ?)";
            pstmt = DerbyConn.prepareStatement(DerbySQL.addTeacherSQL);
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getSex());
            pstmt.setString(3, teacher.getTitle());
            pstmt.setInt(4, teacher.getAge());
            pstmt.setString(5, teacher.getPassword());
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("新增教师异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null, pstmt, DerbyConn);
        }
    }

    // 更新教师
    @Override
    public boolean updateTeacher(Teacher teacher) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            // SQL更新教师信息
            //String sql = "UPDATE teacher SET name = ?, sex = ?, title = ?, age = ?, password = ? WHERE id = ?";
            pstmt = DerbyConn.prepareStatement(DerbySQL.updateTeacherSQL);
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getSex());
            pstmt.setString(3, teacher.getTitle());
            pstmt.setInt(4, teacher.getAge());
            pstmt.setString(5, teacher.getPassword());
            pstmt.setInt(6, teacher.getId());
            return pstmt.executeUpdate() > 0; // 执行成功返回true
        } catch (SQLException e) {
            System.err.println("更新教师异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null, pstmt, DerbyConn);
        }
    }

    // 删除教师
    @Override
    public boolean deleteTeacher(Integer id) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        try {
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            // 先检查是否有关联课程（避免外键约束错误）
            //String checkSql = "SELECT id FROM course WHERE teacher_id = ?";
            //pstmt = conn.prepareStatement(checkSql);
            pstmt = DerbyConn.prepareStatement(DerbySQL.checkTeacherCourseSQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) { // 存在关联课程，不允许删除
                rs.close();
                return false;
            }
            rs.close();
            pstmt.close();

            // 执行删除
            //String sql = "DELETE FROM teacher WHERE id = ?";
            //pstmt = conn.prepareStatement(deleteSql);
            pstmt = DerbyConn.prepareStatement(DerbySQL.deleteTeacherSQL);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除教师异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, null);
            DerbyDbUtil.closeAll(null, pstmt, DerbyConn);
        }
    }
}