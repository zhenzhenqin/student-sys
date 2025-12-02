package com.mjcshuai.view;

import com.mjcshuai.model.Teacher;
import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.dao.impl.TeacherDAOImpl;
import com.mjcshuai.constant.DerbySQL;
import com.mjcshuai.util.DerbyDbUtil;
//import com.mjcshuai.util.DbUtil;

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
 * 教师管理界面 - 完整实现新增/编辑/删除功能
 */
public class TeacherManageFrame extends JInternalFrame {
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private TeacherDAO teacherDAO = new TeacherDAOImpl();

    public TeacherManageFrame() {
        super("Teacher Management", true, true, true, true);
        setSize(1000, 600);
        initTable();
        initButtons();
        loadTeacherData();
    }

    // 初始化表格
    private void initTable() {
        String[] columnNames = {"Teacher ID", "Username", "Gender", "Title", "Age", "Password"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);
        teacherTable.setRowHeight(30);
        teacherTable.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
        teacherTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        add(new JScrollPane(teacherTable), BorderLayout.CENTER);
    }

    // 初始化操作按钮（实现完整功能）
    private void initButtons() {
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Teacher");
        JButton editBtn = new JButton("Edit Teacher");
        JButton deleteBtn = new JButton("Delete Teacher");
        JButton refreshBtn = new JButton("Refresh Data");

        Dimension btnSize = new Dimension(120, 35);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);
        refreshBtn.setPreferredSize(btnSize);

        // 1. 新增教师功能
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel addPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                addPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField();
                JTextField sexField = new JTextField();
                JTextField titleField = new JTextField();
                JTextField ageField = new JTextField();
                JTextField pwdField = new JTextField();

                addPanel.add(new JLabel("Username:"));
                addPanel.add(nameField);
                addPanel.add(new JLabel("Gender:"));
                addPanel.add(sexField);
                addPanel.add(new JLabel("Title:"));
                addPanel.add(titleField);
                addPanel.add(new JLabel("Age:"));
                addPanel.add(ageField);
                addPanel.add(new JLabel("Password:"));
                addPanel.add(pwdField);

                int result = JOptionPane.showConfirmDialog(TeacherManageFrame.this,
                        addPanel, "Add Teacher", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // 输入验证
                    String name = nameField.getText().trim();
                    String sex = sexField.getText().trim();
                    String title = titleField.getText().trim();
                    String ageStr = ageField.getText().trim();
                    String pwd = pwdField.getText().trim();

                    if (name.isEmpty() || pwd.isEmpty()) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Username and Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!ageStr.matches("\\d+") || Integer.parseInt(ageStr) < 18 || Integer.parseInt(ageStr) > 65) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Age must be between 18 and 65!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (isUsernameExists(name)) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 封装教师对象
                    Teacher newTeacher = new Teacher();
                    newTeacher.setName(name);
                    newTeacher.setSex(sex);
                    newTeacher.setTitle(title);
                    newTeacher.setAge(Integer.parseInt(ageStr));
                    newTeacher.setPassword(pwd);

                    // 调用DAO新增
                    boolean success = teacherDAO.addTeacher(newTeacher);
                    if (success) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Add Teacher Succeeded!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadTeacherData(); // 刷新表格
                    } else {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Add Teacher Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 2. 编辑教师功能
        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TeacherManageFrame.this,
                            "Please select the teacher to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 获取选中教师信息
                Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
                String oldName = (String) tableModel.getValueAt(selectedRow, 1);
                String oldSex = (String) tableModel.getValueAt(selectedRow, 2);
                String oldTitle = (String) tableModel.getValueAt(selectedRow, 3);
                Integer oldAge = (Integer) tableModel.getValueAt(selectedRow, 4);
                String oldPwd = (String) tableModel.getValueAt(selectedRow, 5);

                // 编辑对话框
                JPanel editPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField(oldName);
                JTextField sexField = new JTextField(oldSex);
                JTextField titleField = new JTextField(oldTitle);
                JTextField ageField = new JTextField(oldAge.toString());
                JTextField pwdField = new JTextField(oldPwd);

                editPanel.add(new JLabel("Username:"));
                editPanel.add(nameField);
                editPanel.add(new JLabel("Sex:"));
                editPanel.add(sexField);
                editPanel.add(new JLabel("Title:"));
                editPanel.add(titleField);
                editPanel.add(new JLabel("Age:"));
                editPanel.add(ageField);
                editPanel.add(new JLabel("密码："));
                editPanel.add(pwdField);

                int result = JOptionPane.showConfirmDialog(TeacherManageFrame.this,
                        editPanel, "Edit Teacher", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // 输入验证
                    String name = nameField.getText().trim();
                    String sex = sexField.getText().trim();
                    String title = titleField.getText().trim();
                    String ageStr = ageField.getText().trim();
                    String pwd = pwdField.getText().trim();

                    if (name.isEmpty() || pwd.isEmpty()) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Username and password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!ageStr.matches("\\d+") || Integer.parseInt(ageStr) < 18 || Integer.parseInt(ageStr) > 65) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Age must be between 18 and 65!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // 验证用户名是否重复（排除当前教师）
                    if (!name.equals(oldName) && isUsernameExists(name)) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 封装更新对象
                    Teacher updatedTeacher = new Teacher();
                    updatedTeacher.setId(id);
                    updatedTeacher.setName(name);
                    updatedTeacher.setSex(sex);
                    updatedTeacher.setTitle(title);
                    updatedTeacher.setAge(Integer.parseInt(ageStr));
                    updatedTeacher.setPassword(pwd);

                    // 调用DAO更新
                    boolean success = teacherDAO.updateTeacher(updatedTeacher);
                    if (success) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Edit Teacher Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadTeacherData(); // 刷新表格
                    } else {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Edit Teacher Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 3. 删除教师功能
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(TeacherManageFrame.this,
                            "Please select the teacher to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(TeacherManageFrame.this,
                        "Are you sure you want to delete teacher【" + name + "】?\n" +
                                "Courses associated with this teacher will not be displayed!",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 调用DAO删除
                    boolean success = teacherDAO.deleteTeacher(id);
                    if (success) {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Delete Teacher Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadTeacherData(); // 刷新表格
                    } else {
                        JOptionPane.showMessageDialog(TeacherManageFrame.this,
                                "Delete Teacher Failed (Courses may be associated with this teacher)！", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 4. 刷新数据功能
        refreshBtn.addActionListener(e -> loadTeacherData());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // 加载教师数据（从数据库查询）
    private void loadTeacherData() {
        tableModel.setRowCount(0); // 清空表格
        try {
            List<Teacher> teacherList = teacherDAO.findAllTeachers();
            for (Teacher teacher : teacherList) {
                Object[] rowData = {
                        teacher.getId(),
                        teacher.getName(),
                        teacher.getSex(),
                        teacher.getTitle(),
                        teacher.getAge(),
                        teacher.getPassword()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Load Teacher Data Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 校验用户名是否已存在（新增/编辑时使用）
    private boolean isUsernameExists(String username) {
        //Connection conn = null;
        Connection DerbyConn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            //conn = DbUtil.getConnection();
            DerbyConn = DerbyDbUtil.getConnection();
            //String sql = "SELECT id FROM teacher WHERE name = ?";
            pstmt = DerbyConn.prepareStatement(DerbySQL.queryTeacNameById);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            return rs.next(); // 存在返回true
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //DbUtil.closeAll(conn, pstmt, rs);
            DerbyDbUtil.closeAll(rs, pstmt, DerbyConn);
        }
    }
}