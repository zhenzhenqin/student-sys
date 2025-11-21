package com.mjcshuai.view;

import com.mjcshuai.bean.Student;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.dao.impl.StudentDAOImpl;
import com.mjcshuai.util.UserContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 学生管理界面 - 仅管理员可访问
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

    // 初始化操作按钮（新增/编辑/删除）
    private void initButtons() {
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("新增学生");
        JButton editBtn = new JButton("编辑学生");
        JButton deleteBtn = new JButton("删除学生");

        // 按钮样式
        Dimension btnSize = new Dimension(100, 30);
        addBtn.setPreferredSize(btnSize);
        editBtn.setPreferredSize(btnSize);
        deleteBtn.setPreferredSize(btnSize);

        // 按钮事件（实际项目需对接DAO实现CRUD）
        addBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "新增学生功能已触发", "提示", JOptionPane.INFORMATION_MESSAGE));
        editBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请选择要编辑的学生！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "编辑学生功能已触发", "提示", JOptionPane.INFORMATION_MESSAGE);
        });
        deleteBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请选择要删除的学生！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该学生吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // 加载学生数据（实际项目需从数据库查询）
    private void loadStudentData() {
        // 清空表格
        tableModel.setRowCount(0);
        try {
            // 这里模拟查询所有学生（实际调用DAO）
            List<Student> studentList = studentDAO.findAllStudents(); // 需在DAO中新增findAllStudents方法
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
            JOptionPane.showMessageDialog(this, "加载学生数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}