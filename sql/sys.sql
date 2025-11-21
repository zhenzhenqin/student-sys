create database student_sys;

use student_sys;

-- 1. 管理员表
CREATE TABLE IF NOT EXISTS admin (
                                     id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
                                     name VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password VARCHAR(50) NOT NULL COMMENT '管理员密码',
    create_date VARCHAR(20) NOT NULL COMMENT '创建时间（格式：yyyy-MM-dd HH:mm:ss）'
    ) COMMENT '管理员表';
-- 测试数据：用户名admin，密码1234
INSERT INTO admin (name, password, create_date) VALUES ('admin', '1234', '2025-11-21 10:00:00');

-- 2. 教师表
CREATE TABLE IF NOT EXISTS teacher (
                                       id INT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
                                       name VARCHAR(50) NOT NULL UNIQUE COMMENT '教师用户名',
    sex VARCHAR(10) COMMENT '性别',
    title VARCHAR(50) COMMENT '职称',
    age INT COMMENT '年龄',
    password VARCHAR(50) NOT NULL COMMENT '教师密码'
    ) COMMENT '教师表';
-- 测试数据：用户名teacher，密码1234
INSERT INTO teacher (name, password, sex, title, age) VALUES ('teacher', '1234', '男', '讲师', 35);

-- 3. 学生表
CREATE TABLE IF NOT EXISTS student (
                                       id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
                                       name VARCHAR(50) NOT NULL UNIQUE COMMENT '学生用户名',
    class_id INT COMMENT '班级ID',
    sex VARCHAR(10) COMMENT '性别',
    password VARCHAR(50) NOT NULL COMMENT '学生密码'
    ) COMMENT '学生表';
-- 测试数据：用户名student，密码1234
INSERT INTO student (name, password, class_id, sex) VALUES ('student', '1234', 1, '男');