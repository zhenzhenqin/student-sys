package com.mjcshuai.util;

import com.mjcshuai.constant.DbProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * Apache Derby 数据库工具类（嵌入式模式）
 */
public class DerbyDbUtil {
    // 1. Derby 嵌入式驱动（无需启动服务）
    private static final String DRIVER = DbProperties.Derby_DRIVER;
    // 2. 数据库连接 URL：jdbc:derby:数据库名;create=true（不存在则自动创建）
    // 数据库文件存储在项目根目录下的 student_management_db 文件夹
    private static final String URL = DbProperties.Derby_URL;
    // 3. 嵌入式模式默认无需用户名密码（可留空，如需密码可添加 ;user=xxx;password=xxx）
    private static final String USER = DbProperties.Derby_USERNAME;
    private static final String PASSWORD = DbProperties.Derby_PASSWORD;

    // 静态加载驱动
    static {
        try {
            Class.forName(DRIVER);
            initDatabase();
            //System.out.println("Derby 驱动加载成功！");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Derby 驱动加载失败！请检查依赖是否正确", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        //System.out.println("Derby 数据库连接成功！");
        return conn;
    }

    //新增 45 - 150
    /**
     * 核心逻辑：初始化数据库
     * 检查 admin 表是否存在，如果不存在，则执行 SQL 脚本建表
     */
    private static void initDatabase() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnection();

            // 1. 检查关键表 'admin' 是否已经存在
            DatabaseMetaData meta = conn.getMetaData();
            // Derby 中表名通常存储为大写
            rs = meta.getTables(null, null, "ADMIN", null);

            if (rs.next()) {
                // System.out.println("检测到数据库表已存在，跳过初始化。");
                return;
            }

            System.out.println("检测到空数据库，开始执行初始化脚本...");
            executeSqlScript(conn, DbProperties.SQL_FILE_PATH);
            System.out.println("数据库初始化完成！");

        } catch (SQLException e) {
            System.err.println("数据库初始化检查失败：" + e.getMessage());
        } finally {
            closeAll(rs, null, conn);
        }
    }

    /**
     * 读取并执行 SQL 文件
     */
    private static void executeSqlScript(Connection conn, String filePath) {
        File sqlFile = new File(filePath);
        if (!sqlFile.exists()) {
            System.err.println("警告：找不到初始化SQL文件: " + sqlFile.getAbsolutePath());
            return;
        }

        Statement stmt = null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {

            conn.setAutoCommit(false); // 开启事务，保证原子性
            stmt = conn.createStatement();

            StringBuilder command = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // 去除两端空格
                String trimmedLine = line.trim();

                // 跳过空行和注释行（-- 开头）
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }

                // 处理行内注释（简单处理，去掉 -- 及其后面的内容）
                if (trimmedLine.contains("--")) {
                    trimmedLine = trimmedLine.substring(0, trimmedLine.indexOf("--")).trim();
                }

                command.append(trimmedLine);
                command.append(" "); // 防止换行导致粘连

                // 如果行末是分号，则认为是一条完整的 SQL 语句
                if (trimmedLine.endsWith(";")) {
                    // 去掉最后的分号
                    String sql = command.toString().replace(";", "").trim();
                    if (!sql.isEmpty()) {
                        // System.out.println("Executing: " + sql); // 调试用
                        stmt.execute(sql);
                    }
                    command.setLength(0); // 清空 StringBuilder，准备下一条语句
                }
            }

            conn.commit(); // 提交事务

        } catch (Exception e) {
            System.err.println("SQL脚本执行出错：" + e.getMessage());
            e.printStackTrace();
            try {
                conn.rollback(); // 出错回滚
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (stmt != null) stmt.close();
                conn.setAutoCommit(true); // 恢复自动提交
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭资源（ResultSet + PreparedStatement + Connection）
     */
    public static void closeAll(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close(); // 关闭连接，释放资源
        } catch (SQLException e) {
            //System.err.println("资源关闭失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 程序退出时关闭 Derby 数据库（避免数据损坏）
     * 必须在程序终止前调用（如 MainFrame 退出、登录界面关闭时）
     */
    public static void shutdownDerby() {
        try {
            // Derby 关闭特殊语法：jdbc:derby:;shutdown=true
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            // 正常关闭会抛出 SQLState=XJ015 的异常，无需处理
            if ("XJ015".equals(e.getSQLState())) {
                //System.out.println("Derby 数据库正常关闭！");
            } else {
                System.err.println("Derby 关闭异常：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}