package com.mjcshuai.dao;

import java.util.List;
import java.util.Map;

public interface CourseDAO {
    // 查询所有课程
    List<Map<String, Object>> findAllCourses();

    // 根据ID删除课程
    boolean deleteCourseById(int courseId);

    // 检查学生是否已选课程
    boolean isCourseSelectedByStudent(int studentId, int courseId);

    // 获取课程的授课记录ID
    Integer getFirstTeacherCourseId(int courseId);

    // 添加学生选课记录
    boolean insertStudentCourse(int studentId, int teacherCourseId, String selectTime);

    // 更新课程信息
    boolean updateCourse(int courseId, String name, java.math.BigDecimal credit,
                         int classHours, String desc, Integer teacherId);

    // 插入新课程
    int insertCourse(String name, java.math.BigDecimal credit,
                     int classHours, String desc, Integer teacherId);

    // 插入教师课程关联
    boolean insertTeacherCourse(int teacherId, int courseId, String academicYear, int year);
}
