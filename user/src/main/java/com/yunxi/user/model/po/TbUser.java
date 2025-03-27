package com.yunxi.user.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@TableName("tb_user")
public class TbUser implements Cloneable, Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id",type= IdType.AUTO)
  private Integer id;
  private String createBy;
  private Date createDate;
  private String updateBy;
  private Date updateDate;
  private Integer isDelete;
  private String username;
  private String password;
  private String photo;
  private Integer sex;
  private Integer age;
  private String nickename;
  private Integer height;
  private Integer weight;
  private Integer industry;
  private String occupation;
  private Integer maritalStatus;
  private String income;
  private String mobile;
  private String departuretarget;
  private String education;
  private String address;
  private Integer userStatus;

  @Override
  public TbUser clone() {
    TbUser tbUser = null;
    try {
      tbUser = (TbUser) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return tbUser;
  }

}
