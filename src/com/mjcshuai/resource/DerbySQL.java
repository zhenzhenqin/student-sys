package com.mjcshuai.resource;

public class DerbySQL {
    //管理员登录
    public static final String loginSQL = "select ID, NAME, PASSWORD from ADMIN where NAME = ? and PASSWORD = ?";

    //学生登陆
    public static final String studentLoginSQL = "SELECT id, name, class_id AS classId, sex, password FROM student WHERE name = ? AND password = ?";
    //查询所有学生
    public static final String queryAllStudentSQL = "SELECT id, name, class_id AS classId, sex, password FROM student";
    //添加学生
    public static final String addStudentSQL = "INSERT INTO student (name, class_id, sex, password) VALUES (?, ?, ?, ?)";
    //更新学生
    public static final String updateStudentSQL = "UPDATE student SET name = ?, class_id = ?, sex = ?, password = ? WHERE id = ?";
    //删除学生
    public static final String deleteStudentSQL = "DELETE FROM student WHERE id = ?";
    //根据学生姓名查询id
    public static final String queryStudentById = "SELECT id FROM student WHERE name = ?";


    //教师登录
    public static final String teacherLoginSQL = "SELECT id, name, sex, title, age, password FROM teacher WHERE name = ? AND password = ?";
    //查询所有教师
    public static final String queryAllTeacherSQL = "SELECT id, name, sex, title, age, password FROM teacher ORDER BY id ASC";
    //添加教师
    public static final String addTeacherSQL = "INSERT INTO teacher (name, sex, title, age, password) VALUES (?, ?, ?, ?, ?)";
    //更新教师
    public static final String updateTeacherSQL = "UPDATE teacher SET name = ?, sex = ?, title = ?, age = ?, password = ? WHERE id = ?";
    //删除教师前先检查是否有关联课程
    public static final String checkTeacherCourseSQL = "SELECT id FROM course WHERE teacher_id = ?";
    //删除教师
    public static final String deleteTeacherSQL = "DELETE FROM teacher WHERE id = ?";
    //根据教师姓名查询id
    public static final String queryTeacNameById = "SELECT id FROM teacher WHERE name = ?";


    //查询所有课程
    public static final String queryAllCourseSQL = "SELECT c.course_id, c.course_name, c.credit, c.class_hours, " +
            "c.course_desc, t.name AS teacher_name " +
            "FROM courses c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "ORDER BY c.course_id";

    //新增课程
    public static final String insertCourseSQL = "INSERT INTO courses (course_name, credit, class_hours, course_desc, teacher_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    //修改课程
    public static final String updateCourseSQL = "UPDATE courses " +
            "SET course_name = ?, credit = ?, class_hours = ?, course_desc = ?, teacher_id = ? " +
            "WHERE course_id = ?";

    //删除课程
    public static final String deleteCourseSQL = "DELETE FROM courses WHERE course_id = ?";
}