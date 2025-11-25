package com.mjcshuai.view;

import com.mjcshuai.util.UserContext;

import javax.swing.*;
import java.awt.*;

/**
 * 系统主界面 - 根据角色权限动态显示菜单
 */
public class MainFrame extends JFrame {
    private JDesktopPane desktopPane; // 桌面面板（容纳内部窗口）
    private UserContext userContext;

    public MainFrame() {
        userContext = UserContext.getInstance();
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
}