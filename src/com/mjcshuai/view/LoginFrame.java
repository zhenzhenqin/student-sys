package com.mjcshuai.view;

import com.mjcshuai.constant.PromptConstant;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URL;

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

        //管理员账户提示
        JOptionPane.showMessageDialog(LoginFrame.this,
                PromptConstant.ADMIN_LOGIN_PROMPT,
                PromptConstant.ADMIN_LOGIN_PROMPT_TITLE,
                JOptionPane.INFORMATION_MESSAGE);

        // 使用HTML格式美化文本显示
        showGithubMessage();

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

        /**
         * 以下逻辑为键盘按键监听事件绑定
         * author: mjc
         * date: 2025-10-30
         */
        // 启用键盘事件监听
        setFocusable(true);
        requestFocusInWindow(); // 确保窗体获得焦点

        // 添加键盘监听器实现右键切换角色
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) { //按下右键向下一个切换角色
                    switchRole();
                }
            }
        });

        // 给用户名输入框添加回车键监听
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 焦点转移到密码输入框
                passwordField.requestFocus();
            }
        });

        // 给密码输入框添加回车键监听
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 触发登录按钮的点击事件
                loginButton.doClick();
            }
        });

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

    // 添加角色切换方法 新增右键切换角色
    private void switchRole() {
        if (adminRadio.isSelected()) {
            teacherRadio.setSelected(true);
        } else if (teacherRadio.isSelected()) {
            studentRadio.setSelected(true);
        } else if (studentRadio.isSelected()) {
            adminRadio.setSelected(true);
        }
    }

    //此处用于展示跳转github仓库
    private void showGithubMessage() {
        // 创建自定义面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 添加消息文本
        JLabel messageLabel = new JLabel("Open source project address:");
        JLabel urlLabel = new JLabel("<html><a href=''>https://github.com/zhenzhenqin/student-sys</a></html>");
        urlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel tipLabel = new JLabel("Welcome to Star and contribute code");

        // 尝试加载GitHub图标
        try {
            ImageIcon githubIcon = new ImageIcon(new URL("https://github.githubassets.com/favicons/favicon.png"));
            // 缩放图标大小
            Image scaledImage = githubIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            urlLabel.setIcon(scaledIcon);
            urlLabel.setHorizontalTextPosition(JLabel.RIGHT);
            urlLabel.setIconTextGap(5);
        } catch (Exception e) {
            // 图标加载失败时忽略
        }

        // 为链接添加点击事件
        urlLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    // 使用系统默认浏览器打开链接
                    Desktop.getDesktop().browse(new URI("https://github.com/zhenzhenqin/student-sys"));
                } catch (Exception ex) {
                    // 如果无法打开浏览器，显示错误信息
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Unable to open the browser automatically, please manually copy the following link:\nhttps://github.com/zhenzhenqin/student-sys",
                            "warning", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        panel.add(messageLabel);
        panel.add(urlLabel);
        panel.add(tipLabel);

        JOptionPane.showMessageDialog(this, panel, "Zhenzhenqin's prompt", JOptionPane.INFORMATION_MESSAGE);
    }
}