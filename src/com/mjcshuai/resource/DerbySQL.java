package com.mjcshuai.resource;

public class DerbySQL {
    //登录
    public static final String loginSQL = "select ID, NAME, PASSWORD from ADMIN where NAME = ? and PASSWORD = ?";
    //学生登陆
    public static final String studentLoginSQL = "SELECT id, name, class_id AS classId, sex, password FROM student WHERE name = ? AND password = ?";
    //查询所有学生
    public static final String queryAllStudentSQL = "SELECT id, name, class_id AS classId, sex, password FROM student";
    //添加学生
    public static final String addStudentSQL = "INSERT INTO student (name, class_id, sex, password) VALUES (?, ?, ?, ?)";
}
