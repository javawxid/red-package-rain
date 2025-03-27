package com.yunxi.user.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: liaozhiwei
 * @Description: 用户的实体类
 * @Date: Created in 18:24 2022/8/23
 */
@Setter
@Getter
public class AppUserinfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private Integer isDelete;

    private String password;
}
