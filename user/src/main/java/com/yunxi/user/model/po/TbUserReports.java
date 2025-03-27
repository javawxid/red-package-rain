package com.yunxi.user.model.po;



public class TbUserReports {

  private long id;
  private String createBy;
  private java.sql.Timestamp createDate;
  private String updateBy;
  private java.sql.Timestamp updateDate;
  private long isDelete;
  private long report;
  private String reportContent;


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


  public long getReport() {
    return report;
  }

  public void setReport(long report) {
    this.report = report;
  }


  public String getReportContent() {
    return reportContent;
  }

  public void setReportContent(String reportContent) {
    this.reportContent = reportContent;
  }

}
