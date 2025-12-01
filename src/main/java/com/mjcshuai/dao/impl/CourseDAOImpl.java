package com.mjcshuai.dao.impl;

import com.mjcshuai.dao.CourseDAO;
import com.mjcshuai.constant.DerbySQL;
import com.mjcshuai.util.DerbyDbUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程管理相关接口
 * @author mjc
 * @date 2025/11/29
 */
public class CourseDAOImpl implements CourseDAO {
    /**
     * 查询所有课程
     * @return 所有课程的列表，每个课程包含课程ID、名称、学分、学时、描述、教师姓名
     */
    @Override
    public List<Map<String, Object>> findAllCourses() {
        List<Map<String, Object>> courseList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.queryAllCourseSQL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> courseData = new HashMap<>();
                courseData.put("course_id", rs.getInt("course_id"));
                courseData.put("course_name", rs.getString("course_name"));
                courseData.put("credit", rs.getBigDecimal("credit"));
                courseData.put("class_hours", rs.getInt("class_hours"));
                courseData.put("course_desc", rs.getString("course_desc") == null ? "无" : rs.getString("course_desc"));
                courseData.put("teacher_name", rs.getString("teacher_name") == null ? "无" : rs.getString("teacher_name"));

                courseList.add(courseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }

        return courseList;
    }

    /**
     * 根据课程ID删除课程
     * @param courseId 要删除的课程ID
     * @return 如果删除成功则返回true，否则返回false
     */
    @Override
    public boolean deleteCourseById(int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.deleteCourseSQL);
            pstmt.setInt(1, courseId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }

    /**
     * 检查学生是否已选课程
     * @param studentId 要检查的学生ID
     * @param courseId 要检查的课程ID
     * @return 如果学生已选该课程则返回true，否则返回false
     */
    @Override
    public boolean isCourseSelectedByStudent(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.checkStudentCourseExistsSQL);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();

            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    /**
     * 获取课程的授课记录ID
     * @param courseId 要查询的课程ID
     * @return 该课程的第一个教师授课记录ID，如果不存在则返回null
     */
    @Override
    public Integer getFirstTeacherCourseId(int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.getFirstTeacherCourseIdSQL);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DerbyDbUtil.closeAll(rs, pstmt, conn);
        }
    }

    /**
     * 添加学生选课记录
     * @param studentId 要添加选课记录的学生ID
     * @param teacherCourseId 要添加的教师授课记录ID
     * @param selectTime 选课时间
     * @return 如果添加成功则返回true，否则返回false
     */
    @Override
    public boolean insertStudentCourse(int studentId, int teacherCourseId, String selectTime) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.insertStudentCourseSQL);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, teacherCourseId);
            pstmt.setString(3, selectTime);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }

    /**
     * 更新课程信息
     * @param courseId 要更新的课程ID
     * @param name 课程名称
     * @param credit 课程学分
     * @param classHours 课程学时
     * @param desc 课程描述
     * @param teacherId 教师ID
     * @return 如果更新成功则返回true，否则返回false
     */
    @Override
    public boolean updateCourse(int courseId, String name, BigDecimal credit,
                                int classHours, String desc, Integer teacherId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.updateCourseSQL);
            pstmt.setString(1, name);
            pstmt.setBigDecimal(2, credit);
            pstmt.setInt(3, classHours);
            pstmt.setString(4, desc.isEmpty() ? null : desc);
            pstmt.setObject(5, teacherId);
            pstmt.setInt(6, courseId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }

    /**
     * 插入新课程
     * @param name 课程名称
     * @param credit 课程学分
     * @param classHours 课程学时
     * @param desc 课程描述
     * @param teacherId 教师ID
     * @return 新插入课程的ID，如果插入失败则返回-1
     */
    @Override
    public int insertCourse(String name, BigDecimal credit,
                            int classHours, String desc, Integer teacherId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.insertCourseSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setBigDecimal(2, credit);
            pstmt.setInt(3, classHours);
            pstmt.setString(4, desc.isEmpty() ? null : desc);
            pstmt.setObject(5, teacherId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }

    /**
     * 插入教师课程关联
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param academicYear 学年
     * @param year 学期
     * @return 如果插入成功则返回true，否则返回false
     */
    @Override
    public boolean insertTeacherCourse(int teacherId, int courseId, String academicYear, int year) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DerbyDbUtil.getConnection();
            pstmt = conn.prepareStatement(DerbySQL.insertTeacherCourseSQL);
            pstmt.setObject(1, teacherId);
            pstmt.setObject(2, courseId);
            pstmt.setString(3, academicYear);
            pstmt.setInt(4, year);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DerbyDbUtil.closeAll(null, pstmt, conn);
        }
    }
}
