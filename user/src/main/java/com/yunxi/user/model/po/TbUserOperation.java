package com.yunxi.user.model.po;


public class TbUserOperation {

  private long userId;
  private String operationObject;
  private long associationId;
  private java.sql.Timestamp createDate;
  private String operationType;


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getOperationObject() {
    return operationObject;
  }

  public void setOperationObject(String operationObject) {
    this.operationObject = operationObject;
  }


  public long getAssociationId() {
    return associationId;
  }

  public void setAssociationId(long associationId) {
    this.associationId = associationId;
  }


  public java.sql.Timestamp getCreateDate() {
    return createDate;
  }

  public void setCreateDate(java.sql.Timestamp createDate) {
    this.createDate = createDate;
  }


  public String getOperationType() {
    return operationType;
  }

  public void setOperationType(String operationType) {
    this.operationType = operationType;
  }

}
