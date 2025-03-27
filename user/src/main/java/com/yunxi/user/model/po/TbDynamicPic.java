package com.yunxi.user.model.po;



public class TbDynamicPic {

  private long dynamicId;
  private String pictureUrl;
  private String order;
  private long userId;


  public long getDynamicId() {
    return dynamicId;
  }

  public void setDynamicId(long dynamicId) {
    this.dynamicId = dynamicId;
  }


  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
  }


  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
