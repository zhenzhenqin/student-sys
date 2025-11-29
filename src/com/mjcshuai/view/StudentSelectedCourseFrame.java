package com.mjcshuai.view;

import com.mjcshuai.constant.DerbySQL;
import com.mjcshuai.util.DerbyDbUtil;
import com.mjcshuai.util.UserContext;
import com.mjcshuai.model.Student; // 导入学生模型类
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentSelectedCourseFrame extends JInternalFrame {

    // 核心组件：已选课程表格、表格模型、功能按钮
    private JTable selectedCourseTable;
    private DefaultTableModel tableModel;
    private JButton refreshBtn, dropCourseBtn;

    // 表格列名（学生关心的核心信息）
    private String[] columnNames = {
            "课程ID", "课程名称", "学分", "课时", "授课教师",
            "授课学期", "选课时间", "成绩"
    };

    public StudentSelectedCourseFrame() {
        // 窗口基础配置（与其他内部窗口统一风格）
        super("已选课程列表", true, true, true, true);
        setSize(900, 600);

        // 初始化界面组件
        initComponents();

        // 加载当前学生的已选课程数据
        loadSelectedCourseData();
    }

    // 初始化界面组件（表格+功能按钮）
    private void initComponents() {
        // 表格模型：不可编辑（避免误操作）
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 已选课程表格+滚动条
        selectedCourseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(selectedCourseTable);
        // 表格样式优化（与之前窗口保持一致）
        selectedCourseTable.setRowHeight(25);
        selectedCourseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // 调整列宽（适配内容显示）
        selectedCourseTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 课程ID
        selectedCourseTable.getColumnModel().getColumn(1).setPreferredWidth(180); // 课程名称
        selectedCourseTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // 学分
        selectedCourseTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // 课时
        selectedCourseTable.getColumnModel().getColumn(4).setPreferredWidth(120); // 授课教师
        selectedCourseTable.getColumnModel().getColumn(5).setPreferredWidth(120); // 授课学期
        selectedCourseTable.getColumnModel().getColumn(6).setPreferredWidth(150); // 选课时间
        selectedCourseTable.getColumnModel().getColumn(7).setPreferredWidth(60);  // 成绩

        // 功能按钮（刷新+退课）
        refreshBtn = new JButton("刷新列表");
        dropCourseBtn = new JButton("退课");
        // 按钮样式统一
        JButton[] buttons = {refreshBtn, dropCourseBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("宋体", Font.PLAIN, 14));
            btn.setPreferredSize(new Dimension(100, 30));
        }

        // 按钮点击事件绑定
        refreshBtn.addActionListener(e -> {
            loadSelectedCourseData();
            JOptionPane.showMessageDialog(this, "刷新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        dropCourseBtn.addActionListener(e -> dropSelectedCourse());

        // 顶部按钮面板（横向排列，间距统一）
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(refreshBtn);
        topPanel.add(dropCourseBtn);

        // 整体布局（按钮在上，表格在中）
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // 核心功能：加载当前学生的已选课程数据（修正字段引用）
    private void loadSelectedCourseData() {
        // 清空表格原有数据（避免重复）
        tableModel.setRowCount(0);

        // 1. 从UserContext获取当前登录学生（单例模式）
        UserContext userContext = UserContext.getInstance();
        Object loginUser = userContext.getLoginUser();

        // 2. 角色校验：仅学生能访问，避免越权
        if (loginUser == null || !(loginUser instanceof Student)) {
            JOptionPane.showMessageDialog(this, "未检测到登录学生信息，请重新登录！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. 强转Student对象，获取学生ID（依赖Student类的getId()方法）
        Student currentStudent = (Student) loginUser;
        Integer currentStudentId = currentStudent.getId();

        // 数据库资源（声明在外部，方便finally关闭）
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 4. 数据库查询：使用修正后的SQL，关联逻辑正确
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.queryStudentSelectedCoursesSQL);
            pstmt.setInt(1, currentStudentId); // 传入当前学生ID
            rs = pstmt.executeQuery();

            // 5. 遍历结果集，填充表格（处理选课时间字段缺失的情况）
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Object[] rowData = {
                        rs.getInt("course_id"),         // 课程ID
                        rs.getString("course_name"),    // 课程名称
                        rs.getBigDecimal("credit"),     // 学分
                        rs.getInt("class_hours"),       // 课时
                        // 授课教师：空值显示“无”
                        rs.getString("teacher_name") == null ? "无" : rs.getString("teacher_name"),
                        rs.getString("teach_semester"), // 授课学期
                        // 选课时间：如果表中没有该字段，显示默认值（下面会教你添加该字段）
                        rs.getString("select_date") == null ? "未知" : rs.getString("select_date"),
                        // 成绩：未批改显示“未批改”
                        rs.getBigDecimal("score") == null ? "未批改" : rs.getBigDecimal("score")
                };
                tableModel.addRow(rowData);
            }

            // 6. 无已选课程时提示（友好交互）
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "暂无已选课程，可前往课程列表选课！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            // 异常处理：明确提示用户（如果报错“列 SELECT_DATE 不存在”，说明需要添加该字段）
            String errorMsg = e.getMessage().contains("SELECT_DATE") ?
                    "加载已选课程失败！\n原因：student_courses表缺少select_date字段，请执行添加字段SQL！" :
                    "加载已选课程失败！\n原因：" + e.getMessage();
            JOptionPane.showMessageDialog(this, errorMsg, "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // 7. 关闭数据库资源（使用工具类统一方法）
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    // 核心功能：退课操作（删除学生选课记录，使用修正后的SQL）
    private void dropSelectedCourse() {
        // 1. 校验是否选中课程行
        int selectedRow = selectedCourseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选中要退课的课程！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 二次确认：避免误操作
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退选该课程吗？\n退课后不可恢复！", "确认退课", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 3. 从UserContext获取当前学生ID
        UserContext userContext = UserContext.getInstance();
        Student currentStudent = (Student) userContext.getLoginUser();
        Integer studentId = currentStudent.getId();

        // 4. 获取选中课程的关键信息（课程名称+授课学期，用于精准删除）
        String courseName = tableModel.getValueAt(selectedRow, 1).toString();
        String teachSemester = tableModel.getValueAt(selectedRow, 5).toString();

        // 数据库资源
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            // 5. 执行退课：使用修正后的SQL，条件更简洁精准
            pstmt = conn.prepareStatement(DerbySQL.dropStudentCourseSQL);
            pstmt.setInt(1, studentId);       // 学生ID
            pstmt.setString(2, courseName);   // 课程名称
            pstmt.setString(3, teachSemester); // 授课学期

            // 6. 执行删除，判断影响行数
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "退课成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadSelectedCourseData(); // 退课后刷新表格
            } else {
                JOptionPane.showMessageDialog(this, "退课失败，未找到对应的选课记录！", "失败", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "退课异常！\n原因：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }
}