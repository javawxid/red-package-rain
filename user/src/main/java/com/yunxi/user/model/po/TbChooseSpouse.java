package com.yunxi.user.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_choose_spouse")
public class TbChooseSpouse implements Cloneable{

  @TableId(value = "id",type= IdType.AUTO)
  private Integer id;
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
  private Integer departuretarget;
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
  private String createBy;
  private Date createDate;
  private String updateBy;
  private Date updateDate;
  private Integer isDelete;


  @Override
  public TbChooseSpouse clone() {
    TbChooseSpouse tbUser = null;
    try {
      tbUser = (TbChooseSpouse) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return tbUser;
  }

}
