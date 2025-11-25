package com.mjcshuai.view;

import com.mjcshuai.resource.DerbySQL;
import com.mjcshuai.util.DerbyDbUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CourseManageFrame extends JInternalFrame {

    // 核心组件：表格、表格模型、功能按钮（新增/修改/删除/刷新）
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn;

    // 表格列名（与数据库字段对应）
    private String[] columnNames = {"课程ID", "课程名称", "学分", "课时", "课程描述", "主讲教师"};

    public CourseManageFrame() {
        // 窗口基础配置
        super("所有课程列表", true, true, true, true);
        setSize(900, 600);

        // 初始化界面（含新增按钮）
        initComponents();

        // 加载课程数据
        loadCourseData();
    }

    // 初始化界面组件（新增增删改按钮+布局）
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
        // 设置表格行高
        courseTable.setRowHeight(25);
        // 设置列宽（优化显示）
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // 课程ID
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(150); // 课程名称
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(60);  // 学分
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // 课时
        courseTable.getColumnModel().getColumn(4).setPreferredWidth(300); // 课程描述
        courseTable.getColumnModel().getColumn(5).setPreferredWidth(120); // 主讲教师

        // 功能按钮（新增/修改/删除/刷新）
        addBtn = new JButton("新增课程");
        editBtn = new JButton("修改课程");
        deleteBtn = new JButton("删除课程");
        refreshBtn = new JButton("刷新列表");
        // 按钮样式统一
        JButton[] buttons = {addBtn, editBtn, deleteBtn, refreshBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("宋体", Font.PLAIN, 14));
            btn.setPreferredSize(new Dimension(100, 30));
        }

        // 按钮点击事件绑定
        addBtn.addActionListener(e -> openCourseForm(null)); // 新增：传入null
        editBtn.addActionListener(e -> editCourse());       // 修改：传入选中行数据
        deleteBtn.addActionListener(e -> deleteCourse());   // 删除：选中行
        refreshBtn.addActionListener(e -> {
            loadCourseData();
            JOptionPane.showMessageDialog(this, "刷新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        // 顶部按钮面板（横向排列）
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);
        topPanel.add(refreshBtn);

        // 整体布局
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    // 核心：加载所有课程数据
    private void loadCourseData() {
        tableModel.setRowCount(0);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.queryAllCourseSQL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] rowData = {
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getBigDecimal("credit"),
                        rs.getInt("class_hours"),
                        rs.getString("course_desc") == null ? "无" : rs.getString("course_desc"),
                        rs.getString("teacher_name") == null ? "无" : rs.getString("teacher_name")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "加载课程失败！\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    // 1. 新增课程：打开表单弹窗
    private void openCourseForm(Map<String, Object> courseData) {
        new CourseFormDialog(courseData).setVisible(true);
    }

    // 2. 修改课程：校验选中行→打开表单（填充数据）
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

    // 3. 删除课程：校验选中行→确认→执行删除
    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选中要删除的课程！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该课程吗？\n删除后不可恢复！", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 获取课程ID
        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.deleteCourseSQL);
            pstmt.setInt(1, courseId);

            // 执行删除（影响行数>0说明成功）
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "课程删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData(); // 刷新表格
            } else {
                JOptionPane.showMessageDialog(this, "课程删除失败！", "失败", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "删除课程异常！\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }

    // 内部类：新增/修改课程共用表单弹窗（核心修正此处！）
    class CourseFormDialog extends JDialog {
        // 表单组件
        private JTextField nameField, creditField, hoursField, descField;
        private JComboBox<String> teacherCombo;
        private JButton confirmBtn, cancelBtn;
        // 存储教师姓名→教师ID的映射（下拉框用）
        private Map<String, Integer> teacherMap = new HashMap<>();
        // 标记：是新增（null）还是修改（有course_id）
        private Integer courseId;

        // 修正：构造方法改为「获取顶层主窗口」作为父窗口，解决参数不匹配
        public CourseFormDialog(Map<String, Object> courseData) {
            // 关键修正：获取CourseManageFrame的顶层主窗口（必须是JFrame类型），作为JDialog的合法父窗口
            super((Frame) CourseManageFrame.this.getTopLevelAncestor(),
                    courseData == null ? "新增课程" : "修改课程",
                    true); // 第三个参数true=模态窗口（必须关闭弹窗才能操作其他窗口）

            // 弹窗基础配置
            setSize(450, 350);
            setLocationRelativeTo(CourseManageFrame.this); // 相对于课程列表窗口居中
            setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 关闭弹窗时释放资源

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
            } catch (SQLException e) {
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
            Integer teacherId = teacherMap.get(selectedTeacher); // 可能为null（无教师）

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DerbyDbUtil.getConnection();
                pstmt = conn.prepareStatement(DerbySQL.insertCourseSQL);
                // 填充SQL参数（对应insert语句的字段顺序）
                pstmt.setString(1, name);
                pstmt.setBigDecimal(2, credit);
                pstmt.setInt(3, hours);
                pstmt.setString(4, desc.isEmpty() ? null : desc);
                pstmt.setObject(5, teacherId); // 允许为null

                // 执行插入
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "新增课程成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 关闭弹窗
                    loadCourseData(); // 刷新表格
                } else {
                    JOptionPane.showMessageDialog(this, "新增课程失败！", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "新增课程异常！\n" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                DerbyDbUtil.closeAll(null, pstmt, conn);
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

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DerbyDbUtil.getConnection();
                pstmt = conn.prepareStatement(DerbySQL.updateCourseSQL);
                // 填充SQL参数（对应update语句的字段顺序，最后是course_id）
                pstmt.setString(1, name);
                pstmt.setBigDecimal(2, credit);
                pstmt.setInt(3, hours);
                pstmt.setString(4, desc.isEmpty() ? null : desc);
                pstmt.setObject(5, teacherId);
                pstmt.setInt(6, courseId); // 条件：课程ID

                // 执行更新
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "修改课程成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    loadCourseData();
                } else {
                    JOptionPane.showMessageDialog(this, "修改课程失败！", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "修改课程异常！\n" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                DerbyDbUtil.closeAll(null, pstmt, conn);
            }
        }
    }
}