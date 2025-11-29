package com.mjcshuai.view;

import com.mjcshuai.dao.CourseDAO;
import com.mjcshuai.dao.impl.CourseDAOImpl;
import com.mjcshuai.model.Admin;
import com.mjcshuai.model.Student;
import com.mjcshuai.model.Teacher;
import com.mjcshuai.constant.DerbySQL;
import com.mjcshuai.util.DerbyDbUtil;
import com.mjcshuai.util.UserContext;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseManageFrame extends JInternalFrame {

    // 核心组件：表格、表格模型、功能按钮（按角色显示）
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, selectCourseBtn;
    private CourseDAO courseDAO = new CourseDAOImpl();

    // 表格列名（与数据库字段对应）
    private String[] columnNames = {"课程ID", "课程名称", "学分", "课时", "课程描述", "主讲教师"};

    public CourseManageFrame() {
        // 窗口基础配置
        super("所有课程列表", true, true, true, true);
        setSize(900, 600);


        // 初始化界面（含权限控制）
        initComponents();

        // 加载课程数据
        loadCourseData();
    }

    // 初始化界面组件（按角色控制按钮显示）
    private void initComponents() {
        // 表格模型（不可编辑）
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 课程表格+滚动条
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        courseTable.setRowHeight(25);
        // 调整列宽（优化显示）
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 课程ID
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 课程名称
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // 学分
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // 课时
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(300); // 课程描述
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(120); // 主讲教师

        // 初始化所有按钮（按角色控制显示/隐藏）
        addBtn = new JButton("新增课程");
        editBtn = new JButton("修改课程");
        deleteBtn = new JButton("删除课程");
        refreshBtn = new JButton("刷新列表");
        selectCourseBtn = new JButton("选课"); // 学生专属按钮

        // 按钮样式统一
        JButton[] allButtons = {addBtn, editBtn, deleteBtn, refreshBtn, selectCourseBtn};
        for (JButton btn : allButtons) {
            btn.setFont(new Font("宋体", Font.PLAIN, 14));
            btn.setPreferredSize(new Dimension(100, 30));
        }

        // 按钮点击事件绑定（按角色分配功能）
        bindButtonEvents();

        // 顶部按钮面板（核心：按角色添加按钮）
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        // 获取当前登录角色
        UserContext userContext = UserContext.getInstance();
        Object loginUser = userContext.getLoginUser();

        if (loginUser instanceof Admin) {
            // 管理员：显示所有按钮（新增/修改/删除/刷新）
            topPanel.add(addBtn);
            topPanel.add(editBtn);
            topPanel.add(deleteBtn);
            topPanel.add(refreshBtn);
        } else if (loginUser instanceof Student) {
            // 学生：仅显示「选课」+「刷新」（无增删改权限）
            topPanel.add(selectCourseBtn);
            topPanel.add(refreshBtn);
        } else if (loginUser instanceof Teacher) {
            // 教师：仅显示「刷新」（仅查看课程）
            topPanel.add(refreshBtn);
        }

        // 整体布局
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // 绑定按钮事件（按角色区分功能）
    private void bindButtonEvents() {
        // 管理员专属事件（新增/修改/删除）
        addBtn.addActionListener(e -> openCourseForm(null));
        editBtn.addActionListener(e -> editCourse());
        deleteBtn.addActionListener(e -> deleteCourse());

        // 学生专属事件（选课）
        selectCourseBtn.addActionListener(e -> selectCourse());

        // 公共事件（刷新）
        refreshBtn.addActionListener(e -> {
            loadCourseData();
            JOptionPane.showMessageDialog(this, "刷新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // 加载所有课程数据
    private void loadCourseData() {
        tableModel.setRowCount(0); // 清空旧数据

        try {
            List<Map<String, Object>> courses = courseDAO.findAllCourses();

            for (Map<String, Object> course : courses) {
                Object[] rowData = {
                        course.get("course_id"),
                        course.get("course_name"),
                        course.get("credit"),
                        course.get("class_hours"),
                        course.get("course_desc"),
                        course.get("teacher_name")
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载课程失败！\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ---------------------- 管理员专属功能（学生不可见）----------------------
    private void openCourseForm(Map<String, Object> courseData) {
        new CourseFormDialog(courseData).setVisible(true);
    }

    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选中要修改的课程！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 提取选中行数据（course_id为关键）
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("course_id", (int) tableModel.getValueAt(selectedRow, 0));
        courseData.put("course_name", tableModel.getValueAt(selectedRow, 1).toString());
        courseData.put("credit", new BigDecimal(tableModel.getValueAt(selectedRow, 2).toString()));
        courseData.put("class_hours", (int) tableModel.getValueAt(selectedRow, 3));
        courseData.put("course_desc", tableModel.getValueAt(selectedRow, 4).toString().equals("无") ? "" : tableModel.getValueAt(selectedRow, 4).toString());
        courseData.put("teacher_name", tableModel.getValueAt(selectedRow, 5).toString());

        // 打开修改表单（传入已有数据）
        openCourseForm(courseData);
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选中要删除的课程！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该课程吗？\n删除后不可恢复！", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            boolean success = courseDAO.deleteCourseById(courseId);
            if (success) {
                JOptionPane.showMessageDialog(this, "课程删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData();
            } else {
                JOptionPane.showMessageDialog(this, "课程删除失败！", "失败", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "删除课程异常！\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 管理员新增/修改课程弹窗（学生不可见）
    class CourseFormDialog extends JDialog {
        // 表单组件
        private JTextField nameField, creditField, hoursField, descField;
        private JComboBox<String> teacherCombo;
        private JButton confirmBtn, cancelBtn;
        // 存储教师姓名→教师ID的映射（下拉框用）
        private Map<String, Integer> teacherMap = new HashMap<>();
        // 标记：是新增（null）还是修改（有course_id）
        private Integer courseId;

        public CourseFormDialog(Map<String, Object> courseData) {
            super((Frame) CourseManageFrame.this.getTopLevelAncestor(),
                    courseData == null ? "新增课程" : "修改课程",
                    true); // 模态窗口

            // 弹窗基础配置
            setSize(450, 350);
            setLocationRelativeTo(CourseManageFrame.this);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            // 标记是否为修改（有数据则为修改）
            this.courseId = courseData == null ? null : (int) courseData.get("course_id");

            // 初始化表单组件（含教师下拉框数据）
            initFormComponents(courseData);

            // 绑定按钮事件
            bindEvents();
        }

        // 初始化表单组件（输入框+下拉框+布局）
        private void initFormComponents(Map<String, Object> courseData) {
            // 1. 布局：6行2列（标签+输入组件）
            JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            formPanel.setFont(new Font("宋体", Font.PLAIN, 14));

            // 2. 组件初始化
            // 课程名称
            formPanel.add(new JLabel("课程名称*："));
            nameField = new JTextField();
            formPanel.add(nameField);

            // 学分
            formPanel.add(new JLabel("学分*："));
            creditField = new JTextField();
            formPanel.add(creditField);

            // 课时
            formPanel.add(new JLabel("课时*："));
            hoursField = new JTextField();
            formPanel.add(hoursField);

            // 课程描述
            formPanel.add(new JLabel("课程描述："));
            descField = new JTextField();
            formPanel.add(descField);

            // 主讲教师（下拉框：加载所有教师）
            formPanel.add(new JLabel("主讲教师："));
            teacherCombo = new JComboBox<>();
            loadTeachersToCombo(); // 加载教师数据到下拉框
            formPanel.add(teacherCombo);

            // 3. 填充修改数据（如果是修改操作）
            if (courseData != null) {
                nameField.setText((String) courseData.get("course_name"));
                creditField.setText(courseData.get("credit").toString());
                hoursField.setText(String.valueOf(courseData.get("class_hours")));
                descField.setText((String) courseData.get("course_desc"));
                // 选中原有教师
                String teacherName = (String) courseData.get("teacher_name");
                teacherCombo.setSelectedItem(teacherName.equals("无") ? "无教师" : teacherName);
            }

            // 4. 确认/取消按钮
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            confirmBtn = new JButton("确认");
            cancelBtn = new JButton("取消");
            confirmBtn.setPreferredSize(new Dimension(80, 30));
            cancelBtn.setPreferredSize(new Dimension(80, 30));
            btnPanel.add(confirmBtn);
            btnPanel.add(cancelBtn);

            // 5. 弹窗整体布局
            getContentPane().add(formPanel, BorderLayout.CENTER);
            getContentPane().add(btnPanel, BorderLayout.SOUTH);
        }

        // 加载所有教师到下拉框（key：教师姓名，value：教师ID）
        private void loadTeachersToCombo() {
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = DerbyDbUtil.getConnection();
                // 查询所有教师（姓名+ID）
                pstmt = conn.prepareStatement("SELECT id, name FROM teacher ORDER BY name");
                rs = pstmt.executeQuery();

                // 先添加“无教师”选项（对应teacher_id=null）
                teacherMap.put("无教师", null);
                teacherCombo.addItem("无教师");

                // 加载数据库中的教师
                while (rs.next()) {
                    int teacherId = rs.getInt("id");
                    String teacherName = rs.getString("name");
                    teacherMap.put(teacherName, teacherId);
                    teacherCombo.addItem(teacherName);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载教师列表失败！", "错误", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                DerbyDbUtil.closeAll(rs, pstmt, conn);
            }
        }

        // 绑定表单按钮事件（确认/取消）
        private void bindEvents() {
            // 取消：关闭弹窗
            cancelBtn.addActionListener(e -> dispose());

            // 确认：校验输入→执行新增/修改
            confirmBtn.addActionListener(e -> {
                if (validateInput()) { // 输入校验通过
                    if (courseId == null) {
                        saveCourse(); // 新增
                    } else {
                        updateCourse(); // 修改
                    }
                }
            });
        }

        // 输入校验（非空+数字校验）
        private boolean validateInput() {
            String name = nameField.getText().trim();
            String creditStr = creditField.getText().trim();
            String hoursStr = hoursField.getText().trim();

            // 1. 课程名称非空
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "课程名称不能为空！", "校验失败", JOptionPane.WARNING_MESSAGE);
                nameField.requestFocus(); // 焦点定位到名称输入框
                return false;
            }

            // 2. 学分：数字且>0
            try {
                BigDecimal credit = new BigDecimal(creditStr);
                if (credit.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "学分必须大于0！", "校验失败", JOptionPane.WARNING_MESSAGE);
                    creditField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "学分必须是有效数字！", "校验失败", JOptionPane.WARNING_MESSAGE);
                creditField.requestFocus();
                return false;
            }

            // 3. 课时：整数且>0
            try {
                int hours = Integer.parseInt(hoursStr);
                if (hours <= 0) {
                    JOptionPane.showMessageDialog(this, "课时必须大于0！", "校验失败", JOptionPane.WARNING_MESSAGE);
                    hoursField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "课时必须是整数！", "校验失败", JOptionPane.WARNING_MESSAGE);
                hoursField.requestFocus();
                return false;
            }

            return true; // 所有校验通过
        }

        // 新增课程：执行数据库插入
        private void saveCourse() {
            String name = nameField.getText().trim();
            BigDecimal credit = new BigDecimal(creditField.getText().trim());
            int hours = Integer.parseInt(hoursField.getText().trim());
            String desc = descField.getText().trim();
            String selectedTeacher = (String) teacherCombo.getSelectedItem();
            Integer teacherId = teacherMap.get(selectedTeacher);

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                int generatedCourseId = courseDAO.insertCourse(name, credit, hours, desc, teacherId);

                if (generatedCourseId != -1) {
                    if (teacherId != null) {
                        boolean success = courseDAO.insertTeacherCourse(teacherId, generatedCourseId, "2025-2026", 2025);
                        if (!success) {
                            JOptionPane.showMessageDialog(this, "新增课程部分成功，教师关联失败！", "警告", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    JOptionPane.showMessageDialog(this, "新增课程成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    loadCourseData();
                } else {
                    JOptionPane.showMessageDialog(this, "新增课程失败！", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "新增课程异常！\n" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        // 修改课程：执行数据库更新
        private void updateCourse() {
            String name = nameField.getText().trim();
            BigDecimal credit = new BigDecimal(creditField.getText().trim());
            int hours = Integer.parseInt(hoursField.getText().trim());
            String desc = descField.getText().trim();
            String selectedTeacher = (String) teacherCombo.getSelectedItem();
            Integer teacherId = teacherMap.get(selectedTeacher);

            try {
                boolean success = courseDAO.updateCourse(courseId, name, credit, hours, desc, teacherId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "修改课程成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    loadCourseData();
                } else {
                    JOptionPane.showMessageDialog(this, "修改课程失败！", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "修改课程异常！\n" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // ---------------------- 学生专属功能（选课）----------------------
    private void selectCourse() {
        // 1. 校验是否选中课程
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选中要选的课程！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 获取当前登录学生信息
        UserContext userContext = UserContext.getInstance();
        Student currentStudent = (Student) userContext.getLoginUser();
        Integer studentId = currentStudent.getId();

        // 3. 获取选中课程的信息（课程ID+课程名称）
        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseName = tableModel.getValueAt(selectedRow, 1).toString();

        try {
            if (courseDAO.isCourseSelectedByStudent(studentId, courseId)) {
                JOptionPane.showMessageDialog(this, "你已选过「" + courseName + "」，不可重复选课！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer teacherCourseId = courseDAO.getFirstTeacherCourseId(courseId);
            if (teacherCourseId == null) {
                JOptionPane.showMessageDialog(this, "「" + courseName + "」暂无授课记录，无法选课！", "失败", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "确定要选「" + courseName + "」吗？", "确认选课", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = courseDAO.insertStudentCourse(studentId, teacherCourseId, getCurrentTime());
            if (success) {
                JOptionPane.showMessageDialog(this, "选课成功！可前往「已选课程」查看", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "选课失败！", "失败", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "选课异常！\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 辅助方法：校验学生是否已选该课程
    private boolean isCourseSelected(Integer studentId, Integer courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.checkStudentCourseExistsSQL);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();

            return rs.next(); // 有结果则说明已选
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 异常时默认返回未选（避免误判）
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    // 辅助方法：获取课程的第一个授课记录ID（适配Derby语法）
    private Integer getFirstTeacherCourseId(Integer courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.getFirstTeacherCourseIdSQL);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    // 辅助方法：获取当前时间（格式：yyyy-MM-dd HH:mm:ss）
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}