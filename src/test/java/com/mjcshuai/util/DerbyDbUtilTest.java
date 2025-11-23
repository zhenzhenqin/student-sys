package test.java.com.mjcshuai.util;

import com.mjcshuai.util.DerbyDbUtil;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Derby数据库工具类测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DerbyDbUtilTest {

    private Connection connection;

    @BeforeEach
    void setUp() {
        connection = null;
    }

    @AfterEach
    void tearDown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("关闭连接失败: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("测试获取Derby数据库连接")
    void testGetConnection() {
        try {
            connection = DerbyDbUtil.getConnection();
            assertNotNull(connection, "数据库连接不应为null");
            assertFalse(connection.isClosed(), "数据库连接应该是打开状态");
        } catch (SQLException e) {
            fail("获取数据库连接失败: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("测试Derby连接是否可执行SQL")
    void testConnectionExecutable() {
        try {
            connection = DerbyDbUtil.getConnection();
            assertNotNull(connection, "数据库连接不应为null");

            // 尝试执行一个简单的SQL语句来验证连接有效性
            boolean isValid = connection.isValid(5); // 5秒超时
            assertTrue(isValid, "数据库连接应该有效");
        } catch (SQLException e) {
            fail("数据库连接测试失败: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("测试关闭所有资源")
    void testCloseAllResources() {
        try {
            final Connection conn = DerbyDbUtil.getConnection();
            assertNotNull(conn, "数据库连接不应为null");

            // 创建一个简单查询来获得ResultSet
            final PreparedStatement pstmt = conn.prepareStatement("VALUES(1)");
            final ResultSet rs = pstmt.executeQuery();

            // 验证资源都已创建
            assertNotNull(rs, "ResultSet不应为null");
            assertNotNull(pstmt, "PreparedStatement不应为null");

            // 正常执行closeAll方法
            assertDoesNotThrow(() -> {
                DerbyDbUtil.closeAll(rs, pstmt, conn);
            }, "关闭所有资源不应该抛出异常");

        } catch (SQLException e) {
            fail("创建或操作数据库资源时发生错误: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试关闭部分为null的资源")
    void testCloseAllWithNullResources() {
        // 测试当某些资源为null时不会抛出异常
        assertDoesNotThrow(() -> {
            DerbyDbUtil.closeAll(null, null, null);
        }, "关闭null资源不应该抛出异常");

        assertDoesNotThrow(() -> {
            Connection conn = DerbyDbUtil.getConnection();
            DerbyDbUtil.closeAll(null, null, conn);
            conn.close(); // 清理连接
        }, "关闭部分null资源不应该抛出异常");
    }

    @Test
    @Order(5)
    @DisplayName("测试Derby数据库关闭功能")
    void testShutdownDerby() {
        // 测试关闭Derby数据库功能
        assertDoesNotThrow(() -> {
            DerbyDbUtil.shutdownDerby();
        }, "关闭Derby数据库不应该抛出未预期的异常");
    }
}
