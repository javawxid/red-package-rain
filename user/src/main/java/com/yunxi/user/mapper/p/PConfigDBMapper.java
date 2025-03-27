package com.yunxi.user.mapper.p;

import com.yunxi.user.model.base.ColumnInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PConfigDBMapper {


    /**
     * 创建表
     * @param tableName
     * @return
     */
    Integer checkTable(@Param("tableName") String tableName,@Param("databaseName") String databaseName);

    /**
     * 创建子表
     * @param sourceName
     * @param newTableName
     * @return
     */
    Integer createConfigTable(@Param("sourceName") String sourceName, @Param("newTableName") String newTableName);

    /**
     * 获取所有列
     * @param tableName
     * @param databaseName
     * @return
     */
    List<ColumnInfo> getColumnInfoList(@Param("tableName") String tableName, @Param("databaseName") String databaseName);

    /**
     * 获取所有表
     * @param tableName
     * @return
     */
    List<String> getTableInfoList(@Param("tableName") String tableName);

    /**
     * 新增列
     * @param column
     * @return
     */
    int alterTableColumn(ColumnInfo column);

    /**
     * 获取所有索引
     * @param tableName
     * @return
     */
    List<String> getAllIndexNameFromTableName(@Param("tableName") String tableName);

    /**
     *
     * @param tableName
     * @param idxName
     * @return
     */
    List<String> getAllIndexFromTableName(@Param("tableName") String tableName, @Param("idxName") String idxName);

    /**
     * 获取索引
     * @param tableName
     * @param idxName
     * @return
     */
    List<String> findIndexFromTableName(@Param("tableName") String tableName, @Param("idxName") String idxName);

    /**
     * 创建索引
     * @param tableName
     * @param idxName
     * @param indexList
     * @return
     */
    int commonCreatIndex(@Param("tableName") String tableName, @Param("idxName") String idxName, @Param("list")List<String> indexList);



}
