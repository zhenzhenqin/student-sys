package com.mjcshuai.model;

/**
 * 选择课程实体类
 * author:mjc
 * date:2025-11-21
 */
public class SelectedCourse {
    public SelectedCourse(Integer id, Integer student_id, Integer course_id) {
        this.id = id;
        this.student_id = student_id;
        this.course_id = course_id;
    }

    public SelectedCourse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public Integer getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Integer course_id) {
        this.course_id = course_id;
    }


    //选择课程id
    private Integer id;
    //学生id
    private Integer student_id;
    //课程id
    private Integer course_id;
}
