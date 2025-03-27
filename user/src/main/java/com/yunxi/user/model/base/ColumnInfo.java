package com.yunxi.user.model.base;

public class ColumnInfo {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 是否修改
     */
    private boolean addColumn;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 是否默认值
     */
    private String columnDef;
    /**
     * 是否允许为空（NO：不能为空、YES：允许为空）
     */
    private String isNull;
    /**
     * 字段类型（如：varchar(512)、text、bigint(20)、datetime）
     */
    private String columnType;
    /**
     * 字段备注（如：备注）
     */
    private String comment;
    /**
     *
     */
    private String alterName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isAddColumn() {
        return addColumn;
    }

    public void setAddColumn(boolean addColumn) {
        this.addColumn = addColumn;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDef() {
        return columnDef;
    }

    public void setColumnDef(String columnDef) {
        this.columnDef = columnDef;
    }

    public String getIsNull() {
        return isNull;
    }

    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAlterName() {
        return alterName;
    }

    public void setAlterName(String alterName) {
        this.alterName = alterName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ColumnInfo that = (ColumnInfo) o;
        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null){
            return false;
        }
        if (columnDef != null ? !columnDef.equals(that.columnDef) : that.columnDef != null){
            return false;
        }
        if (isNull != null ? !isNull.equals(that.isNull) : that.isNull != null){
            return false;
        }
        if (columnType != null ? !columnType.equals(that.columnType) : that.columnType != null){
            return false;
        }
        if (comment != null ? !comment.equals(that.comment) : that.comment != null){
            return false;
        }
        return comment != null ? !comment.equals(that.comment) : that.comment != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
        result = prime * result + ((columnDef == null) ? 0 : columnDef.hashCode());
        result = prime * result + ((isNull == null) ? 0 : isNull.hashCode());
        result = prime * result + ((columnType == null) ? 0 : columnType.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        /*result = prime * result + ((alterName == null) ? 0 : alterName.hashCode());*/
        return result;
    }
}
