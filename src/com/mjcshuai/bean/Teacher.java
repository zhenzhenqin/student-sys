package com.mjcshuai.bean;

import lombok.Data;

/**
 * 教师实体类
 * author:mjc
 * date:2025-11-21
 */
@Data
public class Teacher {
    //教师id
    private Integer id;
    //教师姓名
    private String name;
    //教师性别
    private String sex;
    //教师职称
    private String title;
    //教师年龄
    private Integer age;
    //教师密码
    private String password;
}
