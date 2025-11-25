package com.mjcshuai.view;

import com.mjcshuai.util.DerbyDbUtil; // 你的数据库工具类
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseManageFrame extends JInternalFrame {

    // 核心组件：表格（展示课程）、表格模型（数据载体）、刷新按钮
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton refreshBtn;

    // 表格列名（对应数据库字段，直观易懂）
    private String[] columnNames = {"课程ID", "课程名称", "学分", "课时", "课程描述", "主讲教师"};

    public CourseManageFrame() {
        // 1. 配置内部窗口基本属性
        super("所有课程列表", true, true, true, true); // 标题、可关闭、可最大化、可最小化、可缩放
        setSize(800, 600); // 窗口大小

        // 2. 初始化界面组件
        initComponents();

        // 3. 初始化时自动加载课程数据
        loadCourseData();
    }

    // 初始化界面组件（布局+组件绑定）
    private void initComponents() {
        // 表格模型：仅展示数据，不可编辑
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格单元格不可编辑
            }
        };

        // 课程表格：绑定模型，添加滚动条
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        // 表格列宽自动调整（适配内容）
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // 刷新按钮：重新加载数据
        refreshBtn = new JButton("刷新课程列表");
        refreshBtn.setFont(new Font("宋体", Font.PLAIN, 14));
        // 按钮点击事件
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCourseData(); // 重新加载数据
                JOptionPane.showMessageDialog(CourseManageFrame.this, "刷新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 布局管理：按钮在顶部，表格在中间
        JPanel topPanel = new JPanel();
        topPanel.add(refreshBtn);
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // 核心功能：从数据库加载所有课程数据（适配你的 DerbyDbUtil）
    private void loadCourseData() {
        // 清空表格原有数据（避免重复）
        tableModel.setRowCount(0);

        // 数据库资源：声明在try外部，方便finally关闭
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 数据库查询SQL：关联courses和teacher表，获取教师姓名（而非ID）
        String sql = "SELECT c.course_id, c.course_name, c.credit, c.class_hours, " +
                "c.course_desc, t.name AS teacher_name " +
                "FROM courses c " +
                "LEFT JOIN teacher t ON c.teacher_id = t.id " + // 左连接：允许无主讲教师的课程
                "ORDER BY c.course_id"; // 按课程ID排序

        try {
            // 1. 获取数据库连接（使用你的工具类）
            conn = DerbyDbUtil.getConnection();
            // 2. 预处理SQL
            pstmt = conn.prepareStatement(sql);
            // 3. 执行查询，获取结果集
            rs = pstmt.executeQuery();

            // 4. 遍历查询结果，填充到表格
            while (rs.next()) {
                Object[] rowData = {
                        rs.getInt("course_id"),          // 课程ID
                        rs.getString("course_name"),     // 课程名称
                        rs.getBigDecimal("credit"),      // 学分
                        rs.getInt("class_hours"),        // 课时
                        // 空值处理：无描述显示“无”
                        rs.getString("course_desc") == null ? "无" : rs.getString("course_desc"),
                        // 空值处理：无教师显示“无”
                        rs.getString("teacher_name") == null ? "无" : rs.getString("teacher_name"),
                };
                tableModel.addRow(rowData); // 添加一行数据到表格
            }

        } catch (SQLException e) {
            // 异常处理：提示用户查询失败
            JOptionPane.showMessageDialog(this, "加载课程数据失败！\n原因：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // 5. 关闭资源（使用你的工具类的closeAll方法，统一释放）
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    // 测试：单独运行窗口（可选，用于调试）
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new CourseManageFrame());
        frame.setVisible(true);

        // 测试时，程序退出前关闭Derby数据库（避免数据损坏）
        Runtime.getRuntime().addShutdownHook(new Thread(DerbyDbUtil::shutdownDerby));
    }
}