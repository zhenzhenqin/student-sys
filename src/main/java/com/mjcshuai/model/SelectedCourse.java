package com.mjcshuai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选择课程实体类
 * author:mjc
 * date:2025-11-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedCourse {
    //选择课程id
    private Integer id;
    //学生id
    private Integer student_id;
    //课程id
    private Integer course_id;
}
