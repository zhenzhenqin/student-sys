package com.mjcshuai.constant;

public class DbProperties {

    //MySQL 数据库连接配置
    /*public static final String MySQL_URL = "jdbc:mysql://localhost:3306/student_sys?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    public static final String MySQL_USERNAME = "root";
    public static final String MySQL_PASSWORD = "1234";
    public static final String MySQL_DRIVER = "com.mysql.cj.jdbc.Driver";*/


    //DerBy数据库连接配置
    public static final String Derby_URL = "jdbc:derby:student_management_db;create=true";
    public static final String Derby_USERNAME = "";
    public static final String Derby_PASSWORD = "";
    public static final String Derby_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    //DerBy数据库初始化SQL脚本路径
    public static final String SQL_FILE_PATH = "sql/DerBy.sql";
}
