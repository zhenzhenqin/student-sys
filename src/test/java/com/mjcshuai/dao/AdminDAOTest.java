package test.java.com.mjcshuai.dao;

import com.mjcshuai.dao.AdminDAO;
import com.mjcshuai.dao.impl.AdminDAOImpl;
import com.mjcshuai.model.Admin;
import com.mjcshuai.util.DerbyDbUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class AdminDAOTest {

    private AdminDAO adminDAO;
    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        adminDAO = new AdminDAOImpl();
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
        Admin admin = adminDAO.login("admin", "1234");

        assertNotNull(admin, "账号密码错误");
        assertEquals("admin", admin.getName(), "登录用户名应为admin");
    }

    @Test
    public void testLoginFailureWithWrongPassword() {
        // 测试密码错误的登录情况
        Admin result = adminDAO.login("admin", "wrongpass");

        assertNull(result, "密码错误");
    }

    @Test
    public void testLoginFailureWithNonExistentUser() {
        // 测试用户名不存在的登录情况
        Admin result = adminDAO.login("", "1234");

        assertNull(result, "用户名不存在");
    }
}
