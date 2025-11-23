package com.mjcshuai.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import static com.mjcshuai.resource.DbProperties.MySQL_URL;
import static com.mjcshuai.resource.DbProperties.MySQL_USERNAME;
import static com.mjcshuai.resource.DbProperties.MySQL_PASSWORD;
import static com.mjcshuai.resource.DbProperties.MySQL_DRIVER;

public class DbUtil {
	// 数据库连接配置
	private static final String URL = MySQL_URL;
	private static final String USERNAME = MySQL_USERNAME;
	private static final String PASSWORD = MySQL_PASSWORD;
	private static final String DRIVER = MySQL_DRIVER;

	// 使用静态块加载驱动
	static {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("数据库驱动程序未找到: " + e.getMessage());
			throw new RuntimeException("数据库驱动程序加载失败", e);
		}
	}

	/**
	 * 获取数据库连接
	 * @return Connection 对象
	 * @throws SQLException 数据库连接异常
	 */
	public static Connection getConnection() throws SQLException {
		try {
			return DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			System.err.println("数据库连接异常: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * 关闭数据库连接
	 * @param conn Connection 对象
	 */
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("关闭数据库失败: " + e.getMessage());
			}
		}
	}

	/**
	 * 关闭Statement
	 * @param stmt Statement 对象
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				System.err.println("关闭Statement失败: " + e.getMessage());
			}
		}
	}

	/**
	 * 关闭ResultSet
	 * @param rs ResultSet 对象
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.err.println("关闭ResultSet失败: " + e.getMessage());
			}
		}
	}

	/**
	 * 关闭所有数据库资源
	 * @param conn Connection 对象
	 * @param stmt Statement 对象
	 * @param rs ResultSet 对象
	 */
	public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
		closeResultSet(rs);
		closeStatement(stmt);
		closeConnection(conn);
	}
}
