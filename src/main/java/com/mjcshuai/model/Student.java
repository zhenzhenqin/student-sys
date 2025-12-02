package com.mjcshuai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生实体类
 * author:mjc
 * data:2025-11-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    //学生id
    private Integer id;
    //学生姓名
    private String name;
    //学生班级id
    private Integer ClassId;
    //学生密码
    private String password;
    //学生性别
    private String sex;
}
