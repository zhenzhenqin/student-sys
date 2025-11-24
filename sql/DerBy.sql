
-- 此处为DerBy数据库对应的建表语句

-- 1. 管理员表（适配 Derby 自增主键 + 语法规范）
CREATE TABLE admin (
                       id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- Derby 自增主键语法
                       name VARCHAR(50) NOT NULL UNIQUE, -- 管理员用户名
                       password VARCHAR(50) NOT NULL, -- 管理员密码
                       create_date VARCHAR(20) NOT NULL -- 创建时间（格式：yyyy-MM-dd HH:mm:ss）
);

-- 测试数据：用户名admin，密码1234（自增ID无需手动插入）
INSERT INTO admin (name, password, create_date) VALUES ('admin', '1234', '2025-11-21 10:00:00');

-- 2. 教师表
CREATE TABLE teacher (
                         id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- 替换 MySQL 的 AUTO_INCREMENT
                         name VARCHAR(50) NOT NULL UNIQUE, -- 教师用户名
                         sex VARCHAR(10), -- 性别
                         title VARCHAR(50), -- 职称
                         age INT, -- 年龄
                         password VARCHAR(50) NOT NULL -- 教师密码
); --  '教师表';

-- 测试数据：用户名teacher，密码1234
INSERT INTO teacher (name, password, sex, title, age) VALUES ('teacher', '1234', '男', '讲师', 35);

-- 3. 学生表
CREATE TABLE student (
                         id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- 自增主键适配
                         name VARCHAR(50) NOT NULL UNIQUE, -- 学生用户名
                         class_id INT, -- 班级ID
                         sex VARCHAR(10), -- 性别
                         password VARCHAR(50) NOT NULL -- 学生密码
); -- '学生表';

-- 测试数据：用户名student，密码1234
INSERT INTO student (name, password, class_id, sex) VALUES ('student', '1234', 1, '男');