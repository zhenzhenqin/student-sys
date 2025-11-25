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

    //教师查询自己的授课记录（关联课程表+统计选课人数）
    public static final String queryTeacherCoursesSQL = "SELECT " +
            "tc.id AS teach_id, " +
            "c.course_id, " +
            "c.course_name, " +
            "c.credit, " +
            "c.class_hours, " +
            "tc.teach_semester, " +
            "tc.teach_year, " +
            "COUNT(sc.id) AS student_count " + // 统计选课人数
            "FROM teacher_courses tc " +
            "LEFT JOIN courses c ON tc.course_id = c.course_id " +
            "LEFT JOIN student_courses sc ON tc.id = sc.teacher_course_id " +
            "WHERE tc.teacher_id = ? " + // 当前教师ID
            "GROUP BY tc.id, c.course_id, c.course_name, c.credit, c.class_hours, tc.teach_semester, tc.teach_year " +
            "ORDER BY tc.teach_year DESC, tc.teach_semester DESC";

    //查看授课课程的选课学生列表
    public static final String querySelectedStudentsSQL = "SELECT " +
            "s.id AS student_id, " +
            "s.name AS student_name, " +
            "s.class_id, " +
            "s.sex, " +
            "sc.select_date, " +
            "sc.score " +
            "FROM student_courses sc " +
            "JOIN student s ON sc.student_id = s.id " +
            "WHERE sc.teacher_course_id = ? " + // 授课记录ID
            "ORDER BY sc.select_date DESC";

    public static final String queryStudentSelectedCoursesSQL = "SELECT " +
            "c.course_id, " +
            "c.course_name, " +
            "c.credit, " +
            "c.class_hours, " +
            "t.name AS teacher_name, " + // 授课教师姓名
            "tc.teach_semester, " +     // 授课学期
            "sc.select_date, " +         // 选课时间（注意：你的student_courses表缺少该字段！下面会修正）
            "sc.score " +                // 成绩
            "FROM student_courses sc " +
            "JOIN teacher_courses tc ON sc.teacher_course_id = tc.id " + // 先关联授课记录
            "JOIN courses c ON tc.course_id = c.course_id " + // 再通过授课记录关联课程
            "LEFT JOIN teacher t ON tc.teacher_id = t.id " + // 关联教师
            "WHERE sc.student_id = ? " + // 当前学生ID
            "ORDER BY sc.select_date DESC"; // 按选课时间倒序

    // 2. 学生退课（修正条件：通过teacher_course_id精准删除，避免复杂子查询）
    public static final String dropStudentCourseSQL = "DELETE FROM student_courses " +
            "WHERE student_id = ? " +
            "AND teacher_course_id = (SELECT id FROM teacher_courses " +
            "WHERE course_id = (SELECT course_id FROM courses WHERE course_name = ?) " +
            "AND teach_semester = ?)";
}