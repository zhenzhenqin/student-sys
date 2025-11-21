package com.mjcshuai.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员实体类
 * author:mjc
 * date:2025-11-21
 */
public class Admin {
    public Admin(Integer id, String name, String password, String createDate) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.createDate = createDate;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Admin() {
    }

    //管理员id
    private Integer id;
    //管理员姓名
    private String name;
    //管理员密码
    private String password;
    //管理员创建时间
    private String createDate;


}
