package com.mjcshuai.model;

/**
 * 课程实体类
 * author:mjc
 * date:2025-11-21
 */
public class Course {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(Integer teacher_id) {
        this.teacher_id = teacher_id;
    }

    public Integer getMax_student_num() {
        return max_student_num;
    }

    public void setMax_student_num(Integer max_student_num) {
        this.max_student_num = max_student_num;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getSelected_num() {
        return selected_num;
    }

    public void setSelected_num(Integer selected_num) {
        this.selected_num = selected_num;
    }

    public Course(Integer id, String name, Integer teacher_id, Integer max_student_num, String info, Integer selected_num) {
        this.id = id;
        this.name = name;
        this.teacher_id = teacher_id;
        this.max_student_num = max_student_num;
        this.info = info;
        this.selected_num = selected_num;
    }

    public Course() {
    }

    //课程id
    private Integer id;
    //课程名称
    private String name;
    //教师id
    private Integer teacher_id;
    //最大学生人数
    private Integer max_student_num;
    //课程信息
    private String info;
    //已选学生人数
    private Integer selected_num = 0;
}