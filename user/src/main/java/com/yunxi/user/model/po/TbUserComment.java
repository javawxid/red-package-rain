package com.yunxi.user.model.po;



public class TbUserComment {

  private long id;
  private String createBy;
  private java.sql.Timestamp createDate;
  private String updateBy;
  private java.sql.Timestamp updateDate;
  private long isDelete;
  private long userId;
  private String comment;
  private long type;
  private String associationId;
  private long beUserId;
  private long dynamicId;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }


  public java.sql.Timestamp getCreateDate() {
    return createDate;
  }

  public void setCreateDate(java.sql.Timestamp createDate) {
    this.createDate = createDate;
  }


  public String getUpdateBy() {
    return updateBy;
  }

  public void setUpdateBy(String updateBy) {
    this.updateBy = updateBy;
  }


  public java.sql.Timestamp getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(java.sql.Timestamp updateDate) {
    this.updateDate = updateDate;
  }


  public long getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(long isDelete) {
    this.isDelete = isDelete;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }


  public long getType() {
    return type;
  }

  public void setType(long type) {
    this.type = type;
  }


  public String getAssociationId() {
    return associationId;
  }

  public void setAssociationId(String associationId) {
    this.associationId = associationId;
  }


  public long getBeUserId() {
    return beUserId;
  }

  public void setBeUserId(long beUserId) {
    this.beUserId = beUserId;
  }


  public long getDynamicId() {
    return dynamicId;
  }

  public void setDynamicId(long dynamicId) {
    this.dynamicId = dynamicId;
  }

}
