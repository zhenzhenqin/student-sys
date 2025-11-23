package com.mjcshuai.model;

/**
 * 教师实体类
 * author:mjc
 * date:2025-11-21
 */
public class Teacher {
    public Teacher(Integer id, String name, String sex, String title, Integer age, String password) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.title = title;
        this.age = age;
        this.password = password;
    }

    public Teacher(){}

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

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
