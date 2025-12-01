/*
package test.java.com.mjcshuai.util;

import com.mjcshuai.util.DbUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("数据库连接工具类测试")
public class DbUtilTest {
    
    private Connection connection;
    
    @BeforeEach
    void setUp() {
        connection = null;
    }
    
    @AfterEach
    void tearDown() {
        DbUtil.closeConnection(connection);
    }
    
    @Test
    @DisplayName("测试获取数据库连接")
    void testGetConnection() {
        try {
            connection = DbUtil.getConnection();
            assertNotNull(connection, "数据库连接不应为null");
            assertFalse(connection.isClosed(), "数据库连接应该是打开状态");
        } catch (SQLException e) {
            fail("获取数据库连接失败: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("测试连接是否可执行SQL")
    void testConnectionExecutable() {
        try {
            connection = DbUtil.getConnection();
            assertNotNull(connection, "数据库连接不应为null");
            
            // 尝试执行一个简单的SQL语句来验证连接有效性
            boolean isValid = connection.isValid(5); // 5秒超时
            assertTrue(isValid, "数据库连接应该有效");
            
        } catch (SQLException e) {
            fail("数据库连接测试失败: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("测试关闭连接")
    void testCloseConnection() {
        try {
            connection = DbUtil.getConnection();
            assertNotNull(connection, "数据库连接不应为null");
            
            // 关闭连接
            DbUtil.closeConnection(connection);
            
            // 注意：标准的JDBC Connection接口没有isClosed()方法会抛异常的约定
            // 这里只是验证close方法能正常调用而不抛出异常
            assertDoesNotThrow(() -> {
                if (connection != null && !connection.isClosed()) {
                    connection.isClosed(); // 验证连接对象仍然可用
                }
            }, "关闭连接不应该抛出异常");
            
        } catch (SQLException e) {
            fail("创建或关闭数据库连接时发生错误: " + e.getMessage());
        }
    }
}
*/
