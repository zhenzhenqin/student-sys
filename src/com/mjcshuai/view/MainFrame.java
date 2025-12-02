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
        setTitle("Student Management System - " + userContext.getRoleName() + " End");
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
        // 创建一个带有样式的面板来放置用户信息
        JPanel userInfoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // 圆角矩形背景
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                // 边框
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };

        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        // 判断当前登录的用户信息
        String userInfoText = "";
        if (loginAdmin != null){
            userInfoText = "Welcome, distinguished administrator: " + loginAdmin.getName();
        } else if (loginStudent != null){
            userInfoText = "Welcome, student: " + loginStudent.getName();
        } else if (loginTeacher != null){
            userInfoText = "Welcome, teacher: " + loginTeacher.getName();
        }

        userInfoLabel = new JLabel(userInfoText);
        userInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userInfoLabel.setForeground(new Color(51, 51, 51));

        // 添加鼠标手势和点击事件
        userInfoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userInfoLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                openInternalFrame(new PersonalInfoFrame(), "Personal Information Management");
            }

            public void mouseEntered(MouseEvent evt) {
                userInfoLabel.setForeground(new Color(0, 123, 255));
            }

            public void mouseExited(MouseEvent evt) {
                userInfoLabel.setForeground(new Color(51, 51, 51));
            }
        });

        userInfoPanel.add(userInfoLabel);

        // 将用户信息面板添加到窗口的右上角
        userInfoPanel.setBounds(getWidth() - 250, 5, 230, 30);
        add(userInfoPanel);

        // 监听窗口大小变化以保持位置
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                userInfoPanel.setBounds(getWidth() - 250, 5, 230, 30);
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
        menuBar.setBackground(new Color(51, 51, 51)); // 深灰色背景
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        menuBar.setOpaque(true);

        // 为菜单项添加图标和样式
        Font menuFont = new Font("微软雅黑", Font.PLAIN, 12);

        // 1. 个人中心菜单（所有角色都有）
        JMenu personalMenu = new JMenu("Personal Center");
        personalMenu.setFont(menuFont);
        personalMenu.setForeground(Color.WHITE); // 白色字体
        personalMenu.setOpaque(true);
        personalMenu.setBackground(new Color(51, 51, 51));

        // 添加悬停效果
        personalMenu.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                personalMenu.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(MouseEvent e) {
                personalMenu.setBackground(new Color(51, 51, 51));
            }
        });

        JMenuItem ownInfoItem = new JMenuItem("View/Edit Personal Information");
        ownInfoItem.setFont(menuFont);
        JMenuItem logoutItem = new JMenuItem("Log out");
        logoutItem.setFont(menuFont);

        ownInfoItem.addActionListener(e -> openInternalFrame(new PersonalInfoFrame(), "Personal Information Management"));
        logoutItem.addActionListener(e -> logout());

        personalMenu.add(ownInfoItem);
        personalMenu.addSeparator();
        personalMenu.add(logoutItem);
        menuBar.add(personalMenu);

        // 2. 学生管理菜单（仅管理员可见）
        if (userContext.hasPermission("view_all_students")) {
            JMenu studentMenu = new JMenu("Student Management");
            studentMenu.setFont(menuFont);
            studentMenu.setForeground(Color.WHITE);
            studentMenu.setOpaque(true);
            studentMenu.setBackground(new Color(51, 51, 51));

            // 添加悬停效果
            studentMenu.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    studentMenu.setBackground(new Color(70, 70, 70));
                }

                public void mouseExited(MouseEvent e) {
                    studentMenu.setBackground(new Color(51, 51, 51));
                }
            });

            JMenuItem viewStudentItem = new JMenuItem("View All Students");
            viewStudentItem.setFont(menuFont);
            viewStudentItem.addActionListener(e -> openInternalFrame(new StudentManageFrame(), "Student Management"));
            studentMenu.add(viewStudentItem);
            menuBar.add(studentMenu);
        }

        // 3. 教师管理菜单（仅管理员可见）
        if (userContext.hasPermission("view_all_teachers")) {
            JMenu teacherMenu = new JMenu("Teacher Management");
            teacherMenu.setFont(menuFont);
            teacherMenu.setForeground(Color.WHITE);
            teacherMenu.setOpaque(true);
            teacherMenu.setBackground(new Color(51, 51, 51));

            // 添加悬停效果
            teacherMenu.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    teacherMenu.setBackground(new Color(70, 70, 70));
                }

                public void mouseExited(MouseEvent e) {
                    teacherMenu.setBackground(new Color(51, 51, 51));
                }
            });

            JMenuItem viewTeacherItem = new JMenuItem("View All Teachers");
            viewTeacherItem.setFont(menuFont);
            viewTeacherItem.addActionListener(e -> openInternalFrame(new TeacherManageFrame(), "Teacher Management"));
            teacherMenu.add(viewTeacherItem);
            menuBar.add(teacherMenu);
        }

        // 4. 课程管理菜单（不同角色显示不同菜单项）
        JMenu courseMenu = new JMenu("Course Management");
        courseMenu.setFont(menuFont);
        courseMenu.setForeground(Color.WHITE);
        courseMenu.setOpaque(true);
        courseMenu.setBackground(new Color(51, 51, 51));

        // 添加悬停效果
        courseMenu.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                courseMenu.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(MouseEvent e) {
                courseMenu.setBackground(new Color(51, 51, 51));
            }
        });

        if (userContext.hasPermission("view_all_courses")) { // 管理员/学生
            JMenuItem viewAllCourseItem = new JMenuItem("View All Courses");
            viewAllCourseItem.setFont(menuFont);
            viewAllCourseItem.addActionListener(e -> openInternalFrame(new CourseManageFrame(), "Course Management"));
            courseMenu.add(viewAllCourseItem);
        }
        if (userContext.hasPermission("view_teaching_courses")) { // 教师
            JMenuItem teachingCourseItem = new JMenuItem("View My Courses");
            teachingCourseItem.setFont(menuFont);
            teachingCourseItem.addActionListener(e -> openInternalFrame(new TeacherCourseFrame(), "Course Management"));
            courseMenu.add(teachingCourseItem);
        }
        if (userContext.hasPermission("view_selected_courses")) { // 学生
            JMenuItem selectedCourseItem = new JMenuItem("View Selected Courses");
            selectedCourseItem.setFont(menuFont);
            selectedCourseItem.addActionListener(e -> openInternalFrame(new StudentSelectedCourseFrame(), "Course Management"));
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
        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // 渐变背景
                GradientPaint gp = new GradientPaint(0, 0, new Color(248, 249, 250), 0, getHeight(), new Color(233, 236, 239));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 添加水印文字
                g2d.setColor(new Color(200, 200, 200, 50));
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 48));
                FontMetrics fm = g2d.getFontMetrics();
                String watermark = "Course Management System";
                int x = (getWidth() - fm.stringWidth(watermark)) / 2;
                int y = getHeight() / 3;
                g2d.drawString(watermark, x, y);
            }
        };

        desktopPane.setBackground(Color.WHITE);

        // 添加居中的GitHub链接
        addCenterGithubLink();

        // 添加 GitHub 图标链接
        addGithubLink();

        add(desktopPane, BorderLayout.CENTER);
    }

    // 打开内部窗口（避免重复打开同一个窗口）
    // 打开内部窗口（避免重复打开同一个窗口）
    private void openInternalFrame(JInternalFrame frame, String title) {
        // 检查窗口是否已打开，若已打开则激活
        for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
            if (internalFrame.getTitle().equals(title)) {
                try {
                    internalFrame.setSelected(true);
                    // 添加视觉反馈，突出显示被激活的窗口
                    internalFrame.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2));
                    // 一段时间后恢复原样式
                    Timer timer = new Timer(500, e -> {
                        internalFrame.setBorder(UIManager.getBorder("InternalFrame.border"));
                    });
                    timer.setRepeats(false);
                    timer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        // 未打开则添加到桌面并显示
        desktopPane.add(frame);
        frame.setVisible(true);

        // 设置窗口样式
        frame.setResizable(true);
        frame.setMaximizable(true);
        frame.setIconifiable(true);
        frame.setClosable(true);

        // 居中显示内部窗口
        frame.setLocation((desktopPane.getWidth() - frame.getWidth()) / 2,
                (desktopPane.getHeight() - frame.getHeight()) / 2);

        // 添加窗口焦点监听器
        frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
                frame.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2));
            }

            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent e) {
                frame.setBorder(UIManager.getBorder("InternalFrame.border"));
            }
        });
    }

    // 退出登录（清除上下文，返回登录界面）
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm exit", JOptionPane.YES_NO_OPTION);
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
            githubLabel.setToolTipText("Visit the GitHub repository");

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
                                "Prompt", JOptionPane.INFORMATION_MESSAGE);
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
                                "Prompt", JOptionPane.INFORMATION_MESSAGE);
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
            // 创建带阴影效果的面板
            JPanel centerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // 绘制阴影
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 15, 15);

                    // 绘制面板背景
                    g2d.setColor(new Color(255, 255, 255, 220));
                    g2d.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 15, 15);

                    // 绘制边框
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.drawRoundRect(0, 0, getWidth()-4, getHeight()-4, 15, 15);
                }
            };

            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            // 加载GitHub图标
            ImageIcon githubIcon = new ImageIcon(new URL("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"));
            Image scaledImage = githubIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // 创建图标标签
            JLabel iconLabel = new JLabel(scaledIcon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // 创建文本标签
            JLabel textLabel = new JLabel("Visit Our GitHub Repository");
            textLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            textLabel.setForeground(new Color(51, 51, 51));
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel urlLabel = new JLabel("<html><u>github.com/zhenzhenqin/student-sys</u></html>");
            urlLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            urlLabel.setForeground(new Color(0, 123, 255));
            urlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            urlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // 添加间距
            centerPanel.add(iconLabel);
            centerPanel.add(Box.createVerticalStrut(8));
            centerPanel.add(textLabel);
            centerPanel.add(Box.createVerticalStrut(5));
            centerPanel.add(urlLabel);

            // 设置面板大小和位置（居中偏下）
            centerPanel.setBounds(
                    (desktopPane.getWidth() - 220) / 2,
                    (desktopPane.getHeight() - 120) / 2 + 50,
                    220, 120
            );

            // 添加鼠标手势和点击事件
            MouseAdapter mouseAdapter = new MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/zhenzhenqin/student-sys"));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "无法打开浏览器，请手动访问:\nhttps://github.com/zhenzhenqin/student-sys",
                                "Prompt", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    // 鼠标悬停效果
                    centerPanel.setOpaque(true);
                    centerPanel.setBackground(new Color(248, 248, 248));
                    textLabel.setForeground(new Color(0, 123, 255));
                    centerPanel.repaint();
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    // 恢复原状
                    centerPanel.setOpaque(false);
                    textLabel.setForeground(new Color(51, 51, 51));
                    centerPanel.repaint();
                }
            };

            centerPanel.addMouseListener(mouseAdapter);
            iconLabel.addMouseListener(mouseAdapter);
            textLabel.addMouseListener(mouseAdapter);
            urlLabel.addMouseListener(mouseAdapter);

            // 添加到桌面面板
            desktopPane.add(centerPanel);

            // 监听窗口大小变化以保持居中位置
            desktopPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    centerPanel.setBounds(
                            (desktopPane.getWidth() - 220) / 2,
                            (desktopPane.getHeight() - 120) / 2 + 50,
                            220, 120
                    );
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}