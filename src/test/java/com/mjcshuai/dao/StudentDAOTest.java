package com.mjcshuai.dao;

import com.mjcshuai.dao.impl.StudentDAOImpl;
import com.mjcshuai.model.Student;
import com.mjcshuai.util.DerbyDbUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StudentDAOTest {

    private StudentDAO studentDAO;
    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        studentDAO = new StudentDAOImpl();
        connection = DerbyDbUtil.getConnection();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // 清理测试数据
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * 测试学生登录功能
     */
    @Test
    public void testStudentLogin() {
        // 测试登录成功的情况
        Student student = studentDAO.login("student", "1234");

        assertNotNull(student, "账号密码错误");
        assertEquals("student", student.getName(), "登录用户名应为student");
    }

    /**
     * 测试查询所有学生功能
     */
    @Test
    public void testQueryAllStudents() {
        // 测试查询所有学生的情况
        List<Student> studentList = studentDAO.findAllStudents();

        assertNotNull(studentList, "查询成功");
        assertFalse(studentList.isEmpty(), "学生列表不应为空");
    }

}
