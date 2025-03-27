package com.yunxi.user.model.base;

import java.util.List;

public class ConfigDB {

    // 数据库名称
    private String databaseName;
    // 表名称
    private String tableName;
    // 子表后缀编号
    private List<String> labCodes;
    // 来源类型
    private Integer sourceType;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getLabCodes() {
        return labCodes;
    }

    public void setLabCodes(List<String> labCodes) {
        this.labCodes = labCodes;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}
