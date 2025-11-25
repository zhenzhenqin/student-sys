
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








-- 对于课程功能模块

-- 1. 课程表（核心：只存课程基础信息，无冗余字段）
CREATE TABLE courses (
                         course_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- 课程自增ID
                         course_name VARCHAR(100) NOT NULL UNIQUE, -- 课程名称（唯一，避免重复）
                         credit DECIMAL(2,1) NOT NULL, -- 学分（如3.0、2.5）
                         class_hours INT NOT NULL, -- 总课时（如48、64）
                         course_desc VARCHAR(500), -- 课程描述（可选，可空）
                         teacher_id INT -- 主讲教师ID（关联teacher表，可选）
);

-- 2. 教师授课关联表（核心：绑定教师和课程，明确授课学期）
CREATE TABLE teacher_courses (
                                 id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- 关联记录ID
                                 teacher_id INT NOT NULL, -- 教师ID（关联teacher表）
                                 course_id INT NOT NULL, -- 课程ID（关联courses表）
                                 teach_semester VARCHAR(20) NOT NULL, -- 授课学期（如2024-2025-1）
                                 teach_year INT NOT NULL, -- 授课年份（如2024）
    -- 外键：确保教师和课程存在；删除教师/课程时，同步删除授课记录
                                 FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE CASCADE,
                                 FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    -- 唯一约束：避免同一教师同一学期重复教同一门课
                                 UNIQUE (teacher_id, course_id, teach_semester)
);

-- 3. 学生选课关联表（核心：绑定学生和课程，记录成绩）
CREATE TABLE student_courses (
                                 id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, -- 选课记录ID
                                 student_id INT NOT NULL, -- 学生ID（关联student表）
                                 teacher_course_id INT NOT NULL, -- 关联教师授课记录（明确选哪个教师的课）
                                 score DECIMAL(5,2), -- 成绩（可选，没批改就是NULL）
    -- 外键：删除学生/授课记录时，同步删除选课记录
                                 FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
                                 FOREIGN KEY (teacher_course_id) REFERENCES teacher_courses(id) ON DELETE CASCADE,
    -- 唯一约束：避免学生重复选同一教师的同一门课
                                 UNIQUE (student_id, teacher_course_id)
);

-- 测试数据（直接用你现有表的测试用户）
-- 1. 新增2门课程（关联teacher表id=1的测试教师）
INSERT INTO courses (course_name, credit, class_hours, course_desc, teacher_id)
VALUES
    ('Java程序设计', 3.0, 48, 'Java核心语法+面向对象', 1),
    ('数据库原理', 2.5, 40, 'SQL+数据库设计', 1);

-- 2. 测试教师（id=1）的授课记录
INSERT INTO teacher_courses (teacher_id, course_id, teach_semester, teach_year)
VALUES (1, 1, '2024-2025-1', 2024), (1, 2, '2024-2025-1', 2024);

-- 3. 测试学生（id=1）的选课记录
INSERT INTO student_courses (student_id, teacher_course_id)
VALUES (1, 1), (1, 2);

-- 给学生选课表添加选课时间字段（默认值为当前时间格式，适配你的create_date字段风格）
ALTER TABLE student_courses
    ADD COLUMN select_date VARCHAR(20) DEFAULT '2025-11-25 00:00:00';

-- 更新已有测试数据的选课时间
UPDATE student_courses
SET select_date = '2025-11-25 10:00:00'
WHERE id IN (1, 2); -- 对应你的测试数据（student_id=1的两条选课记录