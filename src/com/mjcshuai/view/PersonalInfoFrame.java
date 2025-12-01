package com.mjcshuai.view;

import com.mjcshuai.model.Admin;
import com.mjcshuai.model.Student;
import com.mjcshuai.model.Teacher;
import com.mjcshuai.dao.StudentDAO;
import com.mjcshuai.dao.TeacherDAO;
import com.mjcshuai.dao.impl.StudentDAOImpl;
import com.mjcshuai.dao.impl.TeacherDAOImpl;
import com.mjcshuai.util.UserContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 个人信息管理界面 - 支持学生/教师角色的信息查看与编辑
 */
public class PersonalInfoFrame extends JInternalFrame {
    // 组件声明（根据角色动态显示）
    private JLabel idLabel, nameLabel, sexLabel, pwdLabel, AdminPwdLabel;
    private JTextField idField, nameField, sexField;
    private JPasswordField pwdField;
    private JTextField AdminPwdField;
    // 学生特有字段
    private JLabel classIdLabel;
    private JTextField classIdField;
    // 教师特有字段
    private JLabel titleLabel, ageLabel;
    private JTextField titleField, ageField;
    //管理员持有字段
    private JLabel dateLabel, remarkLabel;
    private JTextField dateField, remarkField;

    private UserContext userContext;
    private Student loginStudent; // 登录学生（角色为学生时非空）
    private Teacher loginTeacher; // 登录教师（角色为教师时非空）
    private Admin loginAdmin; //登录管理员（角色为管理员非空）

    // DAO实例
    private StudentDAO studentDAO = new StudentDAOImpl();
    private TeacherDAO teacherDAO = new TeacherDAOImpl();

    public PersonalInfoFrame() {
        super("个人信息管理", true, true, true, true);
        userContext = UserContext.getInstance();
        initUser(); // 初始化登录用户信息
        initUI(); // 初始化界面
        loadUserInfo(); // 加载用户信息到表单
        setSize(600, 400);

        // 添加窗口样式
        setBackground(Color.WHITE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // 初始化登录用户（判断角色）
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

    // 初始化界面（根据角色动态生成表单）
    private void initUI() {
        // 创建带背景色的主面板
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(248, 249, 250), 0, getHeight(), new Color(233, 236, 239));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 设置标题样式
        Font labelFont = new Font("微软雅黑", Font.PLAIN, 12);
        Font fieldFont = new Font("微软雅黑", Font.PLAIN, 12);

        // 公共字段：ID（不可编辑）、用户名、性别、密码
        idLabel = new JLabel("ID：");
        idLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        idLabel.setForeground(new Color(51, 51, 51));
        idField = new JTextField(20);
        idField.setEditable(false);
        idField.setFont(fieldFont);
        idField.setBackground(new Color(245, 245, 245));
        styleTextField(idField);

        nameLabel = new JLabel("用户名：");
        nameLabel.setFont(labelFont);
        nameField = new JTextField(20);
        nameField.setFont(fieldFont);
        styleTextField(nameField);

        sexLabel = new JLabel("性别：");
        sexLabel.setFont(labelFont);
        sexField = new JTextField(20);
        sexField.setFont(fieldFont);
        styleTextField(sexField);

        // 添加公共字段到面板
        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row++;
        mainPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        mainPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        mainPanel.add(sexLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(sexField, gbc);

        // 根据角色添加特有字段
        if (loginStudent != null) { // 学生角色
            pwdLabel = new JLabel("密码：");
            pwdLabel.setFont(labelFont);
            pwdField = new JPasswordField(20);
            pwdField.setFont(fieldFont);
            styleTextField(pwdField);

            classIdLabel = new JLabel("班级ID：");
            classIdLabel.setFont(labelFont);
            classIdField = new JTextField(20);
            classIdField.setFont(fieldFont);
            styleTextField(classIdField);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(pwdLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(pwdField, gbc);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(classIdLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(classIdField, gbc);
        } else if (loginTeacher != null) { // 教师角色
            pwdLabel = new JLabel("密码：");
            pwdLabel.setFont(labelFont);
            pwdField = new JPasswordField(20);
            pwdField.setFont(fieldFont);
            styleTextField(pwdField);

            titleLabel = new JLabel("职称：");
            titleLabel.setFont(labelFont);
            titleField = new JTextField(20);
            titleField.setFont(fieldFont);
            styleTextField(titleField);

            ageLabel = new JLabel("年龄：");
            ageLabel.setFont(labelFont);
            ageField = new JTextField(20);
            ageField.setFont(fieldFont);
            styleTextField(ageField);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(pwdLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(pwdField, gbc);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(titleLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(titleField, gbc);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(ageLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(ageField, gbc);
        } else if(loginAdmin != null) { //管理猿角色
            nameField.setEditable(false);
            sexField.setEditable(false);

            AdminPwdLabel = new JLabel("密码：");
            AdminPwdLabel.setFont(labelFont);
            AdminPwdField = new JTextField(20);
            AdminPwdField.setEditable(false);
            AdminPwdField.setFont(fieldFont);
            AdminPwdField.setBackground(new Color(245, 245, 245));
            styleTextField(AdminPwdField);

            dateLabel = new JLabel("创建日期：");
            dateLabel.setFont(labelFont);
            dateField = new JTextField(20);
            dateField.setEditable(false);
            dateField.setFont(fieldFont);
            dateField.setBackground(new Color(245, 245, 245));
            styleTextField(dateField);

            remarkLabel = new JLabel("说明：");
            remarkLabel.setFont(labelFont);
            remarkField = new JTextField(20);
            remarkField.setEditable(false);
            remarkField.setFont(fieldFont);
            remarkField.setBackground(new Color(245, 245, 245));
            styleTextField(remarkField);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(AdminPwdLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(AdminPwdField, gbc);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(dateLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(dateField, gbc);

            gbc.gridx = 0;
            gbc.gridy = row++;
            mainPanel.add(remarkLabel, gbc);
            gbc.gridx = 1;
            mainPanel.add(remarkField, gbc);
        }

        // 保存按钮美化
        JButton saveBtn = new JButton("保存修改");
        saveBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        saveBtn.setBackground(new Color(70, 130, 180));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setPreferredSize(new Dimension(120, 35));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加按钮悬停效果
        saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveBtn.setBackground(new Color(100, 149, 237));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveBtn.setBackground(new Color(70, 130, 180));
            }
        });

        saveBtn.addActionListener(new SaveListener());
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(saveBtn, gbc);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // 美化滚动面板
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }


    // 加载用户信息到表单
    private void loadUserInfo() {
        if (loginStudent != null) {
            idField.setText(loginStudent.getId().toString());
            nameField.setText(loginStudent.getName());
            sexField.setText(loginStudent.getSex());
            pwdField.setText(loginStudent.getPassword());
            classIdField.setText(loginStudent.getClassId().toString());
        } else if (loginTeacher != null) {
            idField.setText(loginTeacher.getId().toString());
            nameField.setText(loginTeacher.getName());
            sexField.setText(loginTeacher.getSex());
            pwdField.setText(loginTeacher.getPassword());
            titleField.setText(loginTeacher.getTitle());
            ageField.setText(loginTeacher.getAge().toString());
        } else if(loginAdmin != null){
            idField.setText(loginAdmin.getId().toString());
            nameField.setText(loginAdmin.getName());
            sexField.setText("This is a very mysterious secret");
            AdminPwdField.setText("Back to the time when I loved you");
            dateField.setText("2025-11-21 10:00:00");
            remarkField.setText("https://github.com/zhenzhenqin");

            // 为管理员字段添加特殊样式
            styleAdminField(AdminPwdField);
            styleAdminField(dateField);
            styleAdminField(remarkField);
        }
    }

    // 为管理员字段添加特殊样式
    private void styleAdminField(JTextField field) {
        field.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        field.setForeground(new Color(100, 100, 100));
        field.setBackground(new Color(248, 248, 248));
    }

    // 添加文本框样式方法
    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 添加焦点效果
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(206, 212, 218)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }


    // 保存修改的监听器
    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (loginStudent != null) {
                saveStudentInfo(); // 保存学生信息
            } else if (loginTeacher != null) {
                saveTeacherInfo(); // 保存教师信息
            } else if (loginAdmin != null){
                //如果是管理元修改自己信息 则无法成功 管理员信息固定 便于登录
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                                                "Administrator information cannot be modified",
                                                "mjc prompt",
                                                    JOptionPane.WARNING_MESSAGE);
            }
        }

        // 保存学生信息
        private void saveStudentInfo() {
            // 输入验证
            String name = nameField.getText().trim();
            String sex = sexField.getText().trim();
            String password = new String(pwdField.getPassword()).trim();
            String classIdStr = classIdField.getText().trim();

            if (name.isEmpty() || password.isEmpty() || classIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "用户名、密码和班级ID不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!classIdStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "班级ID必须是数字！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 封装修改后的学生对象
            Student updatedStudent = new Student();
            updatedStudent.setId(loginStudent.getId());
            updatedStudent.setName(name);
            updatedStudent.setSex(sex);
            updatedStudent.setPassword(password);
            updatedStudent.setClassId(Integer.parseInt(classIdStr));

            // 调用DAO更新
            boolean success = studentDAO.updateStudent(updatedStudent);
            if (success) {
                // 更新上下文信息（保持内存与数据库一致）
                userContext.initUser(updatedStudent);
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "个人信息更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                //更新修改之后的数据 直接将修改后的参数赋给登录的参数
                loginStudent = updatedStudent;

                loadUserInfo(); // 刷新表单显示
            } else {
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "更新失败，请重试！", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 保存教师信息
        private void saveTeacherInfo() {
            // 输入验证
            String name = nameField.getText().trim();
            String sex = sexField.getText().trim();
            String password = new String(pwdField.getPassword()).trim();
            String title = titleField.getText().trim();
            String ageStr = ageField.getText().trim();

            if (name.isEmpty() || password.isEmpty() || title.isEmpty() || ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "带*的字段不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!ageStr.matches("\\d+") || Integer.parseInt(ageStr) < 18 || Integer.parseInt(ageStr) > 65) {
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "年龄必须是18-65之间的数字！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 封装修改后的教师对象
            Teacher updatedTeacher = new Teacher();
            updatedTeacher.setId(loginTeacher.getId());
            updatedTeacher.setName(name);
            updatedTeacher.setSex(sex);
            updatedTeacher.setPassword(password);
            updatedTeacher.setTitle(title);
            updatedTeacher.setAge(Integer.parseInt(ageStr));

            // 调用DAO更新
            boolean success = teacherDAO.updateTeacher(updatedTeacher);
            if (success) {
                // 更新上下文信息
                userContext.initUser(updatedTeacher);
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "个人信息更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                loginTeacher = updatedTeacher;

                loadUserInfo(); // 刷新表单显示
            } else {
                JOptionPane.showMessageDialog(PersonalInfoFrame.this,
                        "更新失败，请重试！", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}