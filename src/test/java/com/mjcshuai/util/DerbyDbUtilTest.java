package test.java.com.mjcshuai.util;

import com.mjcshuai.util.DerbyDbUtil;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    //新增 63 -150
    @Test
    @Order(3)
    @DisplayName("验证建表脚本执行：检查关键表是否存在")
    void testTablesCreated() {
        try {
            connection = DerbyDbUtil.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            // 定义你的 SQL 脚本中创建的所有表名 (Derby 内部通常存为大写)
            String[] expectedTables = {
                    "ADMIN",
                    "TEACHER",
                    "STUDENT",
                    "COURSES",
                    "TEACHER_COURSES",
                    "STUDENT_COURSES"
            };

            List<String> missingTables = new ArrayList<>();
            for (String tableName : expectedTables) {
                // 参数：catalog, schemaPattern, tableNamePattern, types
                ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
                if (!rs.next()) {
                    missingTables.add(tableName);
                }
                rs.close();
            }

            assertTrue(missingTables.isEmpty(),
                    "以下表未找到，说明 SQL 建表脚本可能未执行或执行失败: " + missingTables);

            // System.out.println("所有预期的数据表均已存在！");

        } catch (SQLException e) {
            fail("检查表结构时发生异常: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("验证初始化数据：检查管理员账号")
    void testAdminDataInit() {
        try {
            connection = DerbyDbUtil.getConnection();
            String sql = "SELECT * FROM admin WHERE name = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "admin");
                try (ResultSet rs = pstmt.executeQuery()) {
                    assertTrue(rs.next(), "未找到管理员账号 'admin'，初始化数据插入失败");
                    assertEquals("1234", rs.getString("password"), "管理员密码与脚本中定义的不一致");
                }
            }
        } catch (SQLException e) {
            fail("验证管理员数据时失败: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("验证关联数据：检查学生选课与课程数据")
    void testComplexDataInit() {
        try {
            connection = DerbyDbUtil.getConnection();
            // 验证课程是否存在
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT count(*) FROM courses");
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertTrue(count > 0, "课程表(courses)为空，未插入测试数据");
                }
            }

            // 验证学生是否已关联选课 (脚本中 student_courses 表有数据)
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT count(*) FROM student_courses");
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertTrue(count > 0, "学生选课表(student_courses)为空，关联数据插入失败");
                }
            }
        } catch (SQLException e) {
            fail("验证复杂数据关联时失败: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
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
    @Order(7)
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
    @Order(8)
    @DisplayName("测试Derby数据库关闭功能")
    void testShutdownDerby() {
        // 测试关闭Derby数据库功能
        assertDoesNotThrow(() -> {
            DerbyDbUtil.shutdownDerby();
        }, "关闭Derby数据库不应该抛出未预期的异常");
    }
}
