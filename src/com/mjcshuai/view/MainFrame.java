package com.mjcshuai.view;

import com.mjcshuai.model.Admin;
import com.mjcshuai.model.Student;
import com.mjcshuai.model.Teacher;
import com.mjcshuai.util.AppIconUtil;
import com.mjcshuai.util.UserContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;

/**
 * 系统主界面 - 根据角色权限动态显示菜单
 */
public class MainFrame extends JFrame {
    private JDesktopPane desktopPane; // 桌面面板（容纳内部窗口）
    private UserContext userContext;
    private JLabel userInfoLabel; //显示用户登录账户信息
    private Student loginStudent; // 登录学生（角色为学生时非空）
    private Teacher loginTeacher; // 登录教师（角色为教师时非空）
    private Admin loginAdmin; //登录管理员（角色为管理员非空）


    public MainFrame() {
        userContext = UserContext.getInstance();
        AppIconUtil.setWindowIcon(this); // 设置主窗口图标
        initFrame();
        initMenuBar();
        initDesktop();
    }

    // 初始化窗口基本属性
    private void initFrame() {
        setTitle("学生管理系统 - " + userContext.getRoleName() + "端");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initUser(); //判断当前登录的用户的身份信息

        //显示当前登录用户信息
        showUserInfo();
    }

    //添加用户信息方法
    private void showUserInfo() {
        // 创建一个面板来放置用户信息
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.X_AXIS));

        // 创建用户信息标签

        //判断当前登录的用户信息
        if (loginAdmin != null){
            userInfoLabel = new JLabel("欢迎尊贵的管理猿: " + loginAdmin.getName());
        } else if (loginStudent != null){
            userInfoLabel = new JLabel("亲爱的同学: " + loginStudent.getName());
        } else if (loginTeacher != null){
            userInfoLabel = new JLabel("亲爱的教师: " + loginTeacher.getName());
        }

        userInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userInfoLabel.setForeground(Color.BLACK);

        // 添加鼠标手势和点击事件
        userInfoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userInfoLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                openInternalFrame(new PersonalInfoFrame(), "个人信息管理");
            }
        });

        userInfoPanel.add(userInfoLabel);

        // 将用户信息面板添加到窗口的右上角
        userInfoPanel.setBounds(getWidth() - 200, 0, 180, 30);
        add(userInfoPanel);

        // 监听窗口大小变化以保持位置
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                userInfoPanel.setBounds(getWidth() - 200, 0, 180, 30);
            }
        });
    }

    //判断当前登录的用户信息
    private void initUser() {
        Object loginUser = userContext.getLoginUser();
        if (loginUser instanceof Student) {
            loginStudent = (Student) loginUser;
        } else if (loginUser instanceof Teacher) {
            loginTeacher = (Teacher) loginUser;
        } else if(loginUser instanceof Admin){
            loginAdmin = (Admin) loginUser;
        }
    }

    // 初始化菜单条（根据权限动态显示菜单项）
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 1. 个人中心菜单（所有角色都有）
        JMenu personalMenu = new JMenu("个人中心");
        JMenuItem ownInfoItem = new JMenuItem("查看/编辑个人信息");
        JMenuItem logoutItem = new JMenuItem("退出登录");

        ownInfoItem.addActionListener(e -> openInternalFrame(new PersonalInfoFrame(), "个人信息管理"));
        logoutItem.addActionListener(e -> logout());

        personalMenu.add(ownInfoItem);
        personalMenu.addSeparator();
        personalMenu.add(logoutItem);
        menuBar.add(personalMenu);

        // 2. 学生管理菜单（仅管理员可见）
        if (userContext.hasPermission("view_all_students")) {
            JMenu studentMenu = new JMenu("学生管理");
            JMenuItem viewStudentItem = new JMenuItem("查看所有学生");
            viewStudentItem.addActionListener(e -> openInternalFrame(new StudentManageFrame(), "学生管理"));
            studentMenu.add(viewStudentItem);
            menuBar.add(studentMenu);
        }

        // 3. 教师管理菜单（仅管理员可见）
        if (userContext.hasPermission("view_all_teachers")) {
            JMenu teacherMenu = new JMenu("教师管理");
            JMenuItem viewTeacherItem = new JMenuItem("查看所有教师");
            viewTeacherItem.addActionListener(e -> openInternalFrame(new TeacherManageFrame(), "教师管理"));
            teacherMenu.add(viewTeacherItem);
            menuBar.add(teacherMenu);
        }

        // 4. 课程管理菜单（不同角色显示不同菜单项）
        JMenu courseMenu = new JMenu("课程管理");
        if (userContext.hasPermission("view_all_courses")) { // 管理员/学生
            JMenuItem viewAllCourseItem = new JMenuItem("查看所有课程");
            viewAllCourseItem.addActionListener(e -> openInternalFrame(new CourseManageFrame(), "课程列表"));
            courseMenu.add(viewAllCourseItem);
        }
        if (userContext.hasPermission("view_teaching_courses")) { // 教师
            JMenuItem teachingCourseItem = new JMenuItem("查看我的授课");
            teachingCourseItem.addActionListener(e -> openInternalFrame(new TeacherCourseFrame(), "我的授课"));
            courseMenu.add(teachingCourseItem);
        }
        if (userContext.hasPermission("view_selected_courses")) { // 学生
            JMenuItem selectedCourseItem = new JMenuItem("查看已选课程");
            selectedCourseItem.addActionListener(e -> openInternalFrame(new StudentSelectedCourseFrame(), "已选课程"));
            courseMenu.add(selectedCourseItem);
        }
        menuBar.add(courseMenu);

        // 5. 成绩管理菜单（仅教师可见）
        /*if (userContext.hasPermission("grade_students")) {
            JMenu gradeMenu = new JMenu("成绩管理");
            JMenuItem gradeItem = new JMenuItem("给学生打分");
            gradeItem.addActionListener(e -> openInternalFrame(new GradeManageFrame(), "成绩管理"));
            gradeMenu.add(gradeItem);
            menuBar.add(gradeMenu);
        }*/

        // 6. 系统管理菜单（仅管理员可见）
        /*if (userContext.hasPermission("manage_system")) {
            JMenu systemMenu = new JMenu("系统管理");
            JMenuItem systemSetItem = new JMenuItem("系统参数设置");
            systemSetItem.addActionListener(e -> openInternalFrame(new SystemSetFrame(), "系统设置"));
            systemMenu.add(systemSetItem);
            menuBar.add(systemMenu);
        }*/

        setJMenuBar(menuBar);
    }

    // 初始化桌面面板
    private void initDesktop() {
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.WHITE);

        // 添加居中的GitHub链接
        addCenterGithubLink();

        // 添加 GitHub 图标链接
        addGithubLink();

        add(desktopPane, BorderLayout.CENTER);
    }

    // 打开内部窗口（避免重复打开同一个窗口）
    private void openInternalFrame(JInternalFrame frame, String title) {
        // 检查窗口是否已打开，若已打开则激活
        for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
            if (internalFrame.getTitle().equals(title)) {
                try {
                    internalFrame.setSelected(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        // 未打开则添加到桌面并显示
        desktopPane.add(frame);
        frame.setVisible(true);
        // 居中显示内部窗口
        frame.setLocation((desktopPane.getWidth() - frame.getWidth()) / 2,
                         (desktopPane.getHeight() - frame.getHeight()) / 2);
    }

    // 退出登录（清除上下文，返回登录界面）
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要退出登录吗？", "确认退出", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userContext.clearUser();
            dispose(); // 关闭主界面
            new LoginFrame().setVisible(true); // 打开登录界面
        }
    }


    // 添加 GitHub 链接图标和URL
    private void addGithubLink() {
        try {
            // 加载 GitHub 图标
            ImageIcon githubIcon = new ImageIcon(new URL("https://github.githubassets.com/favicons/favicon.png"));
            // 缩放图标
            Image scaledImage = githubIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // 创建图标标签
            JLabel githubLabel = new JLabel(scaledIcon);
            githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            githubLabel.setToolTipText("访问 GitHub 仓库");

            // 创建URL标签
            JLabel urlLabel = new JLabel("github.com/zhenzhenqin");
            urlLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            urlLabel.setForeground(Color.GRAY);
            urlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // 创建包含图标和URL的面板
            JPanel githubPanel = new JPanel();
            githubPanel.setLayout(new BoxLayout(githubPanel, BoxLayout.Y_AXIS));
            githubPanel.setOpaque(false);
            githubPanel.add(githubLabel);
            githubPanel.add(Box.createVerticalStrut(2)); // 添加垂直间距
            githubPanel.add(urlLabel);
            githubPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            // 将面板添加到desktopPane
            githubPanel.setBounds(desktopPane.getWidth() - 120, desktopPane.getHeight() - 60, 100, 50);
            desktopPane.add(githubPanel);

            // 添加点击事件到整个面板
            java.awt.event.MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/zhenzhenqin/student-sys"));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "无法打开浏览器，请手动访问:\nhttps://github.com/zhenzhenqin/student-sys",
                                "提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            };

            githubLabel.addMouseListener(mouseAdapter);
            urlLabel.addMouseListener(mouseAdapter);

            // 监听窗口大小变化以保持位置
            desktopPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    githubPanel.setBounds(desktopPane.getWidth() - 120, desktopPane.getHeight() - 60, 100, 50);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            // 如果图标加载失败，添加简化版链接
            JLabel githubLabel = new JLabel("GitHub: github.com/zhenzhenqin");
            githubLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            githubLabel.setForeground(Color.BLUE.darker());

            githubLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/zhenzhenqin/student-sys"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "无法打开浏览器，请手动访问:\nhttps://github.com/zhenzhenqin/student-sys",
                                "提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            githubLabel.setBounds(desktopPane.getWidth() - 150, desktopPane.getHeight() - 30, 140, 20);
            desktopPane.add(githubLabel);
        }
    }


    // 添加居中的GitHub链接面板
    private void addCenterGithubLink() {
        try {
            // 创建透明面板
            JPanel centerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // 设置透明背景
                    g.setColor(new Color(255, 255, 255, 180)); // 半透明白色背景
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
            };
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            // 加载GitHub图标
            ImageIcon githubIcon = new ImageIcon(new URL("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"));
            Image scaledImage = githubIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // 创建图标标签
            JLabel iconLabel = new JLabel(scaledIcon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // 创建文本标签
            JLabel textLabel = new JLabel("Visit Our GitHub Repository");
            textLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            textLabel.setForeground(new Color(51, 51, 51));
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel urlLabel = new JLabel("github.com/zhenzhenqin/student-sys");
            urlLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            urlLabel.setForeground(Color.GRAY);
            urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // 添加间距
            centerPanel.add(iconLabel);
            centerPanel.add(Box.createVerticalStrut(5));
            centerPanel.add(textLabel);
            centerPanel.add(Box.createVerticalStrut(3));
            centerPanel.add(urlLabel);

            // 设置面板大小和位置（居中偏下）
            centerPanel.setBounds(
                    (desktopPane.getWidth() - 200) / 2,
                    (desktopPane.getHeight() - 100) / 2 + 50,
                    200, 100
            );

            // 添加鼠标手势和点击事件
            centerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            centerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/zhenzhenqin/student-sys"));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "无法打开浏览器，请手动访问:\nhttps://github.com/zhenzhenqin/student-sys",
                                "提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    // 鼠标悬停效果
                    centerPanel.setOpaque(true);
                    centerPanel.setBackground(new Color(240, 240, 240));
                    centerPanel.repaint();
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    // 恢复原状
                    centerPanel.setOpaque(false);
                    centerPanel.repaint();
                }
            });

            // 添加到桌面面板
            desktopPane.add(centerPanel);

            // 监听窗口大小变化以保持居中位置
            desktopPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    centerPanel.setBounds(
                            (desktopPane.getWidth() - 200) / 2,
                            (desktopPane.getHeight() - 100) / 2 + 50,
                            200, 100
                    );
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            // 如果图片加载失败，则不显示中心链接
        }
    }


}