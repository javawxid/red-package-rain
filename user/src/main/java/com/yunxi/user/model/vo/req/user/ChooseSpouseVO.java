package com.yunxi.user.model.vo.req.user;

import lombok.Data;

@Data
public class ChooseSpouseVO {
    /**
     * 地址
     */
    private String address;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 学历
     */
    private String education;
    /**
     * 身高
     */
    private Integer height;
    /**
     * 收入
     */
    private String income;
    /**
     * 行业
     */
    private Integer industry;
    /**
     * 婚姻状况
     */
    private Integer maritalStatus;
    /**
     * 籍贯
     */
    private String nativeplace;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 体重
     */
    private Integer weight;
    private Integer userId;
    private Integer departuretarget;
}
