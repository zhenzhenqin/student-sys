package com.mjcshuai.view;

import com.mjcshuai.model.Student;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.dao.impl.StudentDAOImpl;
import com.mjcshuai.constant.DerbySQL;
//import com.mjcshuai.util.DbUtil;
import com.mjcshuai.util.DerbyDbUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * 学生管理界面 - 完整实现新增/编辑/删除功能
 */
public class StudentManageFrame extends JInternalFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO = new StudentDAOImpl();

    public StudentManageFrame() {
        super("学生管理", true, true, true, true);
        setSize(1000, 600);
        initTable();
        initButtons();
        loadStudentData();
    }

    // 初始化表格
    private void initTable() {
        String[] columnNames = {"学生ID", "用户名", "班级ID", "性别", "密码"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可直接编辑
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
        add(new JScrollPane(studentTable), BorderLayout.CENTER);
    }

    // 初始化操作按钮（实现完整功能）
    private void initButtons() {
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("新增学生");
        JButton editBtn = new JButton("编辑学生");
        JButton deleteBtn = new JButton("删除学生");
        JButton refreshBtn = new JButton("刷新数据"); // 新增刷新按钮

        // 按钮样式
        Dimension btnSize = new Dimension(100, 30);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);
        refreshBtn.setPreferredSize(btnSize);

        // 1. 新增学生按钮功能
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 弹出新增对话框
                JPanel addPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                addPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField();
                JTextField classIdField = new JTextField();
                JTextField sexField = new JTextField();
                JTextField passwordField = new JTextField();

                addPanel.add(new JLabel("用户名："));
                addPanel.add(nameField);
                addPanel.add(new JLabel("班级ID："));
                addPanel.add(classIdField);
                addPanel.add(new JLabel("性别："));
                addPanel.add(sexField);
                addPanel.add(new JLabel("密码："));
                addPanel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(StudentManageFrame.this,
                        addPanel, "新增学生", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // 输入验证
                    String name = nameField.getText().trim();
                    String classIdStr = classIdField.getText().trim();
                    String sex = sexField.getText().trim();
                    String password = passwordField.getText().trim();

                    if (name.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "用户名和密码不能为空！", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!classIdStr.matches("\\d+")) { // 验证班级ID为数字
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "班级ID必须是数字！", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (isUsernameExists(name)) { // 验证用户名是否已存在
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "用户名已存在！", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 封装学生对象
                    Student newStudent = new Student();
                    newStudent.setName(name);
                    newStudent.setClassId(Integer.parseInt(classIdStr));
                    newStudent.setSex(sex);
                    newStudent.setPassword(password);

                    // 调用DAO新增学生
                    boolean success = studentDAO.addStudent(newStudent);
                    if (success) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "新增学生成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadStudentData(); // 刷新表格
                    } else {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "新增学生失败！", "失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 2. 编辑学生按钮功能
        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StudentManageFrame.this,
                            "请选择要编辑的学生！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 获取选中行的学生信息
                Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
                String oldName = (String) tableModel.getValueAt(selectedRow, 1);
                String oldClassId = tableModel.getValueAt(selectedRow, 2).toString();
                String oldSex = (String) tableModel.getValueAt(selectedRow, 3);
                String oldPassword = (String) tableModel.getValueAt(selectedRow, 4);

                // 弹出编辑对话框
                JPanel editPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField(oldName);
                JTextField classIdField = new JTextField(oldClassId);
                JTextField sexField = new JTextField(oldSex);
                JTextField passwordField = new JTextField(oldPassword);

                editPanel.add(new JLabel("用户名："));
                editPanel.add(nameField);
                editPanel.add(new JLabel("班级ID："));
                editPanel.add(classIdField);
                editPanel.add(new JLabel("性别："));
                editPanel.add(sexField);
                editPanel.add(new JLabel("密码："));
                editPanel.add(passwordField);

                int result = JOptionPane.showConfirmDialog(StudentManageFrame.this,
                        editPanel, "编辑学生", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // 输入验证
                    String name = nameField.getText().trim();
                    String classIdStr = classIdField.getText().trim();
                    String sex = sexField.getText().trim();
                    String password = passwordField.getText().trim();

                    if (name.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "用户名和密码不能为空！", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!classIdStr.matches("\\d+")) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "班级ID必须是数字！", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // 若用户名修改，需验证新用户名是否已存在（排除当前学生）
                    if (!name.equals(oldName) && isUsernameExists(name)) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "用户名已存在！", "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 封装修改后的学生对象
                    Student updatedStudent = new Student();
                    updatedStudent.setId(id);
                    updatedStudent.setName(name);
                    updatedStudent.setClassId(Integer.parseInt(classIdStr));
                    updatedStudent.setSex(sex);
                    updatedStudent.setPassword(password);

                    // 调用DAO更新学生
                    boolean success = studentDAO.updateStudent(updatedStudent);
                    if (success) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "编辑学生成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadStudentData(); // 刷新表格
                    } else {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "编辑学生失败！", "失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 3. 删除学生按钮功能
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StudentManageFrame.this,
                            "请选择要删除的学生！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 获取选中学生的ID和姓名
                Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(StudentManageFrame.this,
                        "确定要删除学生【" + name + "】吗？", "确认删除",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 调用DAO删除学生
                    boolean success = studentDAO.deleteStudent(id);
                    if (success) {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "删除学生成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadStudentData(); // 刷新表格
                    } else {
                        JOptionPane.showMessageDialog(StudentManageFrame.this,
                                "删除学生失败！", "失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 4. 刷新数据按钮功能
        refreshBtn.addActionListener(e -> loadStudentData());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // 加载学生数据（从数据库查询）
    private void loadStudentData() {
        tableModel.setRowCount(0); // 清空表格
        try {
            List<Student> studentList = studentDAO.findAllStudents();
            for (Student student : studentList) {
                Object[] rowData = {
                        student.getId(),
                        student.getName(),
                        student.getClassId(),
                        student.getSex(),
                        student.getPassword()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "加载学生数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 校验用户名是否已存在（新增/编辑时使用）
    private boolean isUsernameExists(String username) {
        Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            //String sql = "SELECT id FROM student WHERE name = ?";
            pstmt = conn.prepareStatement(DerbySQL.queryStudentById);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            return rs.next(); // 存在则返回true
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, rs);
            DerbyDbUtil.closeAll(rs, pstmt, DerbyConn);
        }
    }
}