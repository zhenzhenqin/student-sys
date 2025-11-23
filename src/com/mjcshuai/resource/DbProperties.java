package com.mjcshuai.resource;

public class DbProperties {

    //MySQL 数据库连接配置
    public static final String MySQL_URL = "jdbc:mysql://localhost:3306/student_sys?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    public static final String MySQL_USERNAME = "root";
    public static final String MySQL_PASSWORD = "1234";
    public static final String MySQL_DRIVER = "com.mysql.cj.jdbc.Driver";


    //DerBy数据库连接配置
    public static final String Derby_URL = "jdbc:derby:student_management_db;create=true";
    public static final String Derby_USERNAME = "";
    public static final String Derby_PASSWORD = "";
    public static final String Derby_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
}
