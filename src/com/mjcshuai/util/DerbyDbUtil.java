package com.mjcshuai.util;

import com.mjcshuai.resource.DbProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // 静态加载驱动（JDK1.8+ 可省略，但为兼容建议保留）
    static {
        try {
            Class.forName(DRIVER);
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