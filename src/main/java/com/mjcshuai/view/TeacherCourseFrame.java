package com.mjcshuai.view;

import com.mjcshuai.constant.DerbySQL;
import com.mjcshuai.util.DerbyDbUtil;
import com.mjcshuai.util.UserContext;
import com.mjcshuai.model.Teacher; // 必须导入你的Teacher模型类
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherCourseFrame extends JInternalFrame {

    // 核心组件：授课列表表格、表格模型、功能按钮
    private JTable teachCourseTable;
    private DefaultTableModel tableModel;
    private JButton refreshBtn, viewStudentsBtn;

    // 表格列名（展示授课核心信息，适配教师需求）
    private String[] columnNames = {
            "Teacher ID", "Course ID", "Course Name", "Credit", "Hours",
            "Semester", "Year", "Enrollment Count"
    };

    public TeacherCourseFrame() {
        // 窗口基础配置（与其他内部窗口风格一致）
        super("My Course List", true, true, true, true);
        setSize(900, 600);

        // 初始化界面组件
        initComponents();

        // 加载当前教师的授课数据
        loadTeacherCourseData();
    }

    // 初始化界面组件（表格+功能按钮）
    private void initComponents() {
        // 表格模型：不可编辑
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 授课表格+滚动条
        teachCourseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(teachCourseTable);
        // 表格样式优化
        teachCourseTable.setRowHeight(25);
        teachCourseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // 调整列宽（适配内容）
        teachCourseTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 授课ID
        teachCourseTable.getColumnModel().getColumn(1).setPreferredWidth(60);  // 课程ID
        teachCourseTable.getColumnModel().getColumn(2).setPreferredWidth(180); // 课程名称
        teachCourseTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // 学分
        teachCourseTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // 课时
        teachCourseTable.getColumnModel().getColumn(5).setPreferredWidth(120); // 授课学期
        teachCourseTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 授课年份
        teachCourseTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // 选课人数

        // 功能按钮（刷新+查看选课学生）
        refreshBtn = new JButton("Refresh List");
        viewStudentsBtn = new JButton("View Selected Students");
        // 按钮样式统一
        JButton[] buttons = {refreshBtn, viewStudentsBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("宋体", Font.PLAIN, 14));
            btn.setPreferredSize(new Dimension(120, 30));
        }

        // 按钮点击事件
        refreshBtn.addActionListener(e -> {
            loadTeacherCourseData();
            JOptionPane.showMessageDialog(this, "Refresh Success！", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        viewStudentsBtn.addActionListener(e -> viewSelectedStudents());

        // 顶部按钮面板
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(refreshBtn);
        topPanel.add(viewStudentsBtn);

        // 整体布局
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // 核心功能：加载当前教师的所有授课数据（适配UserContext）
    private void loadTeacherCourseData() {
        // 清空表格
        tableModel.setRowCount(0);

        // 1. 从UserContext获取当前登录用户（单例模式，需通过getInstance()获取）
        UserContext userContext = UserContext.getInstance();
        Object loginUser = userContext.getLoginUser();

        // 2. 校验登录用户是否为教师（避免非教师角色访问）
        if (loginUser == null || !(loginUser instanceof Teacher)) {
            JOptionPane.showMessageDialog(this, "No Login Teacher Info Detected！\nPlease Log In Again！", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. 强转成Teacher对象，获取教师ID（依赖Teacher模型类的getId()方法）
        Teacher currentTeacher = (Teacher) loginUser;
        Integer currentTeacherId = currentTeacher.getId(); // 确保Teacher类有getId()方法返回主键id

        // 数据库资源
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            // 查询当前教师的授课记录：关联teacher_courses和courses表，统计选课人数
            pstmt = conn.prepareStatement(DerbySQL.queryTeacherCoursesSQL);
            pstmt.setInt(1, currentTeacherId); // 传入当前教师ID
            rs = pstmt.executeQuery();

            // 遍历结果集，填充表格
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Object[] rowData = {
                        rs.getInt("teach_id"),          // 授课记录ID（teacher_courses.id）
                        rs.getInt("course_id"),         // 课程ID
                        rs.getString("course_name"),    // 课程名称
                        rs.getBigDecimal("credit"),     // 学分
                        rs.getInt("class_hours"),       // 课时
                        rs.getString("teach_semester"), // 授课学期
                        rs.getInt("teach_year"),        // 授课年份
                        rs.getInt("student_count")      // 选课人数（统计值）
                };
                tableModel.addRow(rowData);
            }

            // 无授课记录时提示
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "No Course Record Found！\nPlease Check Your Teaching Schedule！", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load Course Data Failed！\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    // 功能：查看选中授课课程的选课学生列表
    private void viewSelectedStudents() {
        // 校验是否选中行
        int selectedRow = teachCourseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please Select a Course to View Selected Students！", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选中行的授课记录ID（teacher_courses.id）和课程名称
        int teachId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseName = tableModel.getValueAt(selectedRow, 2).toString();

        // 打开学生列表弹窗
        new SelectedStudentDialog(teachId, courseName).setVisible(true);
    }

    // 内部类：选课学生列表弹窗
    class SelectedStudentDialog extends JDialog {
        private JTable studentTable;
        private DefaultTableModel studentTableModel;
        private int teachId; // 授课记录ID（关联student_courses.teacher_course_id）

        // 学生表格列名
        private String[] studentColumnNames = {
                "Student ID", "Student Name", "Class ID", "Gender", "Enrollment Time", "Grade"
        };

        public SelectedStudentDialog(int teachId, String courseName) {
            // 弹窗配置（父窗口为顶层Frame，避免构造方法错误）
            super((Frame) TeacherCourseFrame.this.getTopLevelAncestor(),
                    courseName + " - Selected Student List",
                    true); // 模态窗口
            this.teachId = teachId;
            setSize(800, 500);
            setLocationRelativeTo(TeacherCourseFrame.this);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            // 初始化学生表格
            initStudentTable();

            // 加载选课学生数据
            loadSelectedStudentData();
        }

        // 初始化学生表格
        private void initStudentTable() {
            studentTableModel = new DefaultTableModel(null, studentColumnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            studentTable = new JTable(studentTableModel);
            JScrollPane scrollPane = new JScrollPane(studentTable);
            studentTable.setRowHeight(22);
            studentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            // 布局
            getContentPane().add(scrollPane, BorderLayout.CENTER);
        }

        // 加载该授课课程的选课学生数据
        private void loadSelectedStudentData() {
            studentTableModel.setRowCount(0);
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = DerbyDbUtil.getConnection();
                // 查询该授课记录下的所有学生：关联student_courses、student表
                pstmt = conn.prepareStatement(DerbySQL.querySelectedStudentsSQL);
                pstmt.setInt(1, teachId);
                rs = pstmt.executeQuery();

                boolean hasStudent = false;
                while (rs.next()) {
                    hasStudent = true;
                    Object[] rowData = {
                            rs.getInt("student_id"),    // 学生ID
                            rs.getString("student_name"),// 学生姓名
                            rs.getInt("class_id"),      // 班级ID
                            rs.getString("sex"),        // 性别
                            rs.getString("select_date"),// 选课时间
                            // 成绩：null显示为"未批改"
                            rs.getBigDecimal("score") == null ? "未批改" : rs.getBigDecimal("score")
                    };
                    studentTableModel.addRow(rowData);
                }

                // 无学生选课时提示
                if (!hasStudent) {
                    JOptionPane.showMessageDialog(this, "No Student Enrolled in This Course！\nPlease Check the Enrollment Record！", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Load Student Data Failed！\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                DerbyDbUtil.closeAll(rs, pstmt, conn);
            }
        }
    }
}