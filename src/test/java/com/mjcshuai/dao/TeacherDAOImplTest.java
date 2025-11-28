package test.java.com.mjcshuai.dao;

import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.dao.impl.TeacherDAOImpl;
import com.mjcshuai.model.Admin;
import com.mjcshuai.model.Student;
import com.mjcshuai.model.Teacher;
import com.mjcshuai.util.DerbyDbUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherDAOImplTest {

    private TeacherDAO teacherDAO;
    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        teacherDAO = new TeacherDAOImpl();
        connection = DerbyDbUtil.getConnection();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // 清理测试数据
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testLoginSuccess() {
        // 测试登录成功的情况
        Teacher teacher = teacherDAO.login("teacher", "1234");

        assertNotNull(teacher, "账号密码错误");
        assertEquals("teacher", teacher.getName(), "登录用户名应为teacher");
    }

    @Test
    public void testQueryAllTeachers() {
        // 测试查询所有教师的情况
        List<Teacher> teacherList = teacherDAO.findAllTeachers();

        assertNotNull(teacherList, "查询成功");
        assertFalse(teacherList.isEmpty(), "教师列表不应为空");
    }
}
