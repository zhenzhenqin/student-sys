package com.mjcshuai.bean;

import lombok.Data;

/**
 * 学生实体类
 * author:mjc
 * data:2025-11-21
 */
@Data
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
