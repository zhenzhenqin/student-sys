package com.mjcshuai.bean;

import lombok.Data;

/**
 * 课程实体类
 * author:mjc
 * date:2025-11-21
 */
@Data
public class Course {
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