package com.mjcshuai.view;

import com.mjcshuai.model.Admin;
import com.mjcshuai.model.Student;
import com.mjcshuai.model.Teacher;
import com.mjcshuai.dao.AdminDAO;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.dao.impl.AdminDAOImpl;
import com.mjcshuai.dao.impl.StudentDAOImpl;
import com.mjcshuai.dao.impl.TeacherDAOImpl;
import com.mjcshuai.util.UserContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JRadioButton adminRadio;
    private JRadioButton teacherRadio;
    private JRadioButton studentRadio;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private AdminDAO adminDAO = new AdminDAOImpl();
    private TeacherDAO teacherDAO = new TeacherDAOImpl();
    private StudentDAO studentDAO = new StudentDAOImpl();

    public LoginFrame() {
        // 窗口基本属性
        setTitle("学生管理系统-登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中
        setResizable(false); // 禁止窗口缩放

        // 布局管理（GridBagLayout适配不同屏幕）
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // 组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. 角色选择区（单选按钮组）
        JPanel rolePanel = new JPanel();
        adminRadio = new JRadioButton("管理员", true); // 默认选中
        teacherRadio = new JRadioButton("教师");
        studentRadio = new JRadioButton("学生");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio);
        roleGroup.add(teacherRadio);
        roleGroup.add(studentRadio);
        rolePanel.add(adminRadio);
        rolePanel.add(teacherRadio);
        rolePanel.add(studentRadio);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(rolePanel, gbc);

        // 2. 用户名输入区
        JLabel usernameLabel = new JLabel("用户名:");
        usernameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // 3. 密码输入区
        JLabel passwordLabel = new JLabel("密码:");
        passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // 4. 登录按钮
        loginButton = new JButton("登录");
        loginButton.setFont(new Font("宋体", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        add(mainPanel);

        // 登录按钮点击事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取输入值（去除空格）
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // 空值校验
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "用户名和密码不能为空！", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean loginSuccess = false;
                String roleName = "";

                // 分角色验证
                if (adminRadio.isSelected()) {
                    Admin admin = adminDAO.login(username, password);
                    if (admin != null) {
                        loginSuccess = true;
                        roleName = "管理员";
                    }
                } else if (teacherRadio.isSelected()) {
                    Teacher teacher = teacherDAO.login(username, password);
                    if (teacher != null) {
                        loginSuccess = true;
                        roleName = "教师";
                    }
                } else if (studentRadio.isSelected()) {
                    Student student = studentDAO.login(username, password);
                    if (student != null) {
                        loginSuccess = true;
                        roleName = "学生";
                    }
                }

                // 登录结果反馈
                if (loginSuccess) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "登录成功！欢迎" + roleName + "：" + username, "成功", JOptionPane.INFORMATION_MESSAGE);
                    // 初始化用户上下文
                    UserContext userContext = UserContext.getInstance();
                    if (adminRadio.isSelected()) {
                        userContext.initUser(adminDAO.login(username, password));
                    } else if (teacherRadio.isSelected()) {
                        userContext.initUser(teacherDAO.login(username, password));
                    } else if (studentRadio.isSelected()) {
                        userContext.initUser(studentDAO.login(username, password));
                    }
                    // 关闭登录窗口，打开主界面
                    dispose();
                    new MainFrame().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "输入的账户或密码有错误哦！", "失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}