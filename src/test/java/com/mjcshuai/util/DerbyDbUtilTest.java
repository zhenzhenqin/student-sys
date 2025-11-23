package test.java.com.mjcshuai.util;

import com.mjcshuai.util.DerbyDbUtil;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DerbyDbUtil 测试类
 */
public class DerbyDbUtilTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // 获取数据库连接
        connection = DerbyDbUtil.getConnection();
    }

    @AfterEach
    void tearDown() {
        // 关闭数据库连接
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterAll
    static void shutdown() {
        // 关闭Derby数据库
        DerbyDbUtil.shutdownDerby();
    }

    @Test
    @DisplayName("测试获取数据库连接")
    void testGetConnection() {
        assertDoesNotThrow(() -> {
            Connection conn = DerbyDbUtil.getConnection();
            assertNotNull(conn);
            assertFalse(conn.isClosed());
            conn.close();
        });
    }

    @Test
    @DisplayName("测试数据库连接URL配置")
    void testConnectionUrl() {
        assertDoesNotThrow(() -> {
            Connection conn = DerbyDbUtil.getConnection();
            assertEquals("jdbc:derby:student_management_db", 
                conn.getMetaData().getURL().split(";")[0]);
            conn.close();
        });
    }

    @Test
    @DisplayName("测试关闭资源方法 - 正常情况")
    void testCloseAllNormal() {
        assertDoesNotThrow(() -> {
            final ResultSet[] rsHolder = {null};
            final PreparedStatement[] pstmtHolder = {null};
            final Connection[] connHolder = {null};

            try {
                connHolder[0] = DerbyDbUtil.getConnection();
                pstmtHolder[0] = connHolder[0].prepareStatement("SELECT 1 FROM SYSIBM.SYSDUMMY1");
                rsHolder[0] = pstmtHolder[0].executeQuery();

                // 验证查询执行成功
                assertTrue(rsHolder[0].next());
            } finally {
                // 测试关闭所有资源
                assertDoesNotThrow(() -> DerbyDbUtil.closeAll(
                        rsHolder[0], pstmtHolder[0], connHolder[0]));
            }
        });
    }


    @Test
    @DisplayName("测试关闭资源方法 - 部分资源为null")
    void testCloseAllWithNullResources() {
        assertDoesNotThrow(() -> {
            DerbyDbUtil.closeAll(null, null, null);
        });
        
        assertDoesNotThrow(() -> {
            Connection conn = DerbyDbUtil.getConnection();
            DerbyDbUtil.closeAll(null, null, conn);
        });
    }

    @Test
    @DisplayName("测试Derby数据库关闭功能")
    void testShutdownDerby() {
        // 注意：此测试不会真正关闭数据库，因为后续测试还需要使用
        assertDoesNotThrow(() -> DerbyDbUtil.shutdownDerby());
    }

    @Test
    @DisplayName("测试驱动加载")
    void testDriverLoading() {
        // 驱动已在静态块中加载，这里验证连接可用性
        assertDoesNotThrow(() -> {
            Connection conn = DerbyDbUtil.getConnection();
            assertNotNull(conn);
            conn.close();
        });
    }
}
