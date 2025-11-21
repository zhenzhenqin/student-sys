package com.mjcshuai.bean;

import lombok.Data;

/**
 * 管理员实体类
 * author:mjc
 * date:2025-11-21
 */
@Data
public class Admin {
    //管理员id
    private Integer id;
    //管理员姓名
    private String name;
    //管理员密码
    private String password;
    //管理员创建时间
    private String createDate;
}
