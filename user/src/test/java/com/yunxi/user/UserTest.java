package com.yunxi.user;

import com.alibaba.druid.util.StringUtils;
import com.yunxi.user.constants.UserConstants;
import com.yunxi.user.mapper.p.PConfigDBMapper;
import com.yunxi.user.mapper.s.SRobRedPackageLogMapper;
import com.yunxi.user.mapper.s.SUserMapper;
import com.yunxi.user.model.base.ColumnInfo;
import com.yunxi.user.model.base.ConfigDB;
import com.yunxi.user.model.po.TbRobRedPackageLog;
import com.yunxi.user.model.po.TbUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserTest {

//    @Resource
//    PConfigDBMapper pConfigDBMapper;
//
//    @Autowired
//    SUserMapper tbUserMapper;
//    @Resource
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    SRobRedPackageLogMapper sRobRedPackageLogMapper;
//
//
//    @Test
//    public void querysRobRedPackageLog(){
//        //select t.id,t.red_package_id,t.user_id,t.activity_id,t.part_red_package t from tb_red_package_log t where  create_time between ? and ? and user_id = ?  or activity_id = ?
//        List<TbRobRedPackageLog> tbRobRedPackageLogs = sRobRedPackageLogMapper.selectRobRedPackageLog(8830015L);
//        for (TbRobRedPackageLog tbRobRedPackageLog : tbRobRedPackageLogs) {
//            System.out.println(tbRobRedPackageLog.toString());
//        }
//    }
//
//    @Test
//    public void testPassword(){
//        String password = passwordEncoder.encode("1233456" + UserConstants.USER_SECRET);
//        System.out.println(password);
//        System.out.println(verifyPassword( "1233456" + UserConstants.USER_SECRET,password));
//    }
//
//    public boolean verifyPassword(String rawPassword, String encodedPassword) {
//        // 使用PasswordEncoder的matches方法来比较密码
//        return passwordEncoder.matches(rawPassword, encodedPassword);
//    }
//
//    @Test
//    public void createUser(){
//        ScheduledExecutorService poolExecutor = new ScheduledThreadPoolExecutor(
//                Runtime.getRuntime().availableProcessors() * 2);
//        poolExecutor.scheduleAtFixedRate(() -> {
//                    List<TbUser> userList = new ArrayList<>(10000);
//                    for (int i = 0; i < 10000; i++) {
//                        TbUser tbUser = new TbUser();
//                        tbUser.setIsDelete(0);
//                        tbUser.setUserStatus(1);
//                        String uid =  UUID.randomUUID().toString();
//                        tbUser.setNickename("liaozhiwei" + uid);
//                        tbUser.setUsername("liaozhiwei" + uid);
//                        tbUser.setPassword("liao" + uid);
//                        userList.add(tbUser);
//                    }
////                    Integer usersave = tbUserMapper.batchInsert(userList);
////                    log.info("批量保存用户数量：" + usersave);
//            log.info("测试");
//
//                },
//                0, //这是首次执行任务前的延迟时间。它表示从当前时间开始到首次执行任务所需的等待时间。配置为0，表示无需等待
//                1, TimeUnit.SECONDS);//这是任务执行的周期时间。每次任务执行完毕后，都会等待这个时间段后再次执行任务。
//    }
//
//    /**
//     * 创建子表
//     * 注意：在使用ShardingSphere进行数据库分片时，需要提前生成好表规则，否则无法找到与逻辑表对应的表规则和默认数据源,先单库批量生成子表结构，然后再把子表结构的sql导入到分库后的多个库中
//     */
//    @Test
//    public void createSubtable(){
//        List<String> list = new ArrayList<>();
//        List<String> years = new ArrayList<>();
//        years.add("2023");
//        years.add("2024");
//        years.add("2025");
//        years.add("2026");
//        years.add("2027");//后缀
//        List<String> months = new ArrayList<>();
//        for (int i = 1; i < 13; i++) {
//            months.add(String.valueOf(i));
//        }
//        List<String> LabCodes = new ArrayList<>();
//        LabCodes.add("1");
//        LabCodes.add("2");
//        for (String year : years) {
//            for (String month : months) {
//                for (String labCode : LabCodes) {
//                    list.add(year + "_" + month + "_" + labCode);
//                }
//            }
//        }
//        ConfigDB configDB = new ConfigDB();
//        configDB.setTableName("tb_red_package_log");//表名称
//        configDB.setDatabaseName("user");//数据库名称
//        configDB.setLabCodes(list);
//        configDB.setSourceType(0);
//        //创建子表结构
//        createTable(configDB);
//        //同步子表结构
//        syncAlterTableColumn(configDB.getTableName(),configDB.getDatabaseName());
//        //同步子表索引
//        syncAlterConfigIndex(configDB.getTableName());
//    }
//    /**
//     * 创建子表结构
//     * {
//     *     "tableName": "tb_user",
//     *     "labCodes": [
//     *         "sh",//上海
//     *         "gz",//广州
//     *         "bj"//北京
//     *     ]
//     * }
//     */
//    public Boolean createTable(ConfigDB reqObject) {
//        if (CollectionUtils.isEmpty(reqObject.getLabCodes())) {
//            return false;
//        }
//        List<String> labCodes = reqObject.getLabCodes();
//        //主表表名
//        String tableName = reqObject.getTableName();
//        //数据库名称
//        String databaseName = reqObject.getDatabaseName();
//        for (String labCode: labCodes){
//            //子表后表名
//            String newTable = String.format("%s_%s", tableName, labCode);
//            //校验子表是否存在
//            Integer checkMatrix = pConfigDBMapper.checkTable(newTable, databaseName);
//            if(checkMatrix == null || checkMatrix.intValue() < 0){
//                //创建子表结构
//                pConfigDBMapper.createConfigTable(tableName, newTable);
//            }
//        }
//        return true;
//    }
//    /**
//     * 主表字段同步到子表
//     * @param masterTable 主表
//     * @return
//     */
//    private Boolean syncAlterTableColumn(String masterTable,String database) {
//        String table = masterTable + "%";
//        //获取子表名
//        List<String> tables = pConfigDBMapper.getTableInfoList(table);
//        if(CollectionUtils.isEmpty(tables)){
//            return false;
//        }
//        //获取主表结构列信息
//        List<ColumnInfo> masterColumns = pConfigDBMapper.getColumnInfoList(masterTable,database);
//        if (masterColumns.isEmpty()){
//            return false;
//        }
//        String alterName = null;
//        for (ColumnInfo column: masterColumns) {
//            column.setAlterName(alterName);
//            alterName = column.getColumnName();
//        }
//        for(String tableName : tables){
//            if(StringUtils.equalsIgnoreCase(tableName, masterTable)){
//                continue;
//            }
//            //获取子表结构列信息
//            List<ColumnInfo> columns = pConfigDBMapper.getColumnInfoList(tableName,database);
//            if(CollectionUtils.isEmpty(columns)){
//                continue;
//            }
//            for (ColumnInfo masterColumn : masterColumns) {
//                ColumnInfo column = columns.stream().filter(c -> StringUtils.equalsIgnoreCase(c.getColumnName(),
//                        masterColumn.getColumnName())).findFirst().orElse(null);
//                if (column == null){
//                    column = new ColumnInfo();
//                    column.setColumnName(masterColumn.getColumnName());//列名
//                    column.setAddColumn(true);//是否修改
//                }
//                if (column.hashCode() == masterColumn.hashCode()){
//                    continue;
//                }
//                column.setTableName(tableName);//表名
//                column.setColumnDef(masterColumn.getColumnDef());//是否默认值
//                column.setIsNull(masterColumn.getIsNull());//是否允许为空（NO：不能为空、YES：允许为空）
//                column.setColumnType(masterColumn.getColumnType());//字段类型（如：varchar(512)、text、bigint(20)、datetime）
//                column.setComment(masterColumn.getComment());//字段备注（如：备注）
//                column.setAlterName(masterColumn.getAlterName());//修改的列名
//                //创建子表字段
//                pConfigDBMapper.alterTableColumn(column);
//            }
//        }
//        return true;
//    }
//    /**
//     * 主表索引同步子表
//     * @param masterTableName 主表名
//     * @return
//     */
//    private Boolean syncAlterConfigIndex(String masterTableName) {
//        String table = masterTableName + "%";
//        //获取子表名
//        List<String> tableInfoList = pConfigDBMapper.getTableInfoList(table);
//        if (tableInfoList.isEmpty()){
//            return false;
//        }
//        // 获取所有索引
//        List<String> allIndexFromTableName = pConfigDBMapper.getAllIndexNameFromTableName(masterTableName);
//        if (CollectionUtils.isEmpty(allIndexFromTableName)) {
//            return false;
//        }
//        for (String indexName : allIndexFromTableName) {
//            //获取拥有索引的列名
//            List<String> indexFromIndexName = pConfigDBMapper.getAllIndexFromTableName(masterTableName, indexName);
//            for (String tableName : tableInfoList) {
//                if (!tableName.startsWith(masterTableName)) {
//                    continue;
//                }
//                //获取索引名称
//                List<String> addIndex = pConfigDBMapper.findIndexFromTableName(tableName, indexName);
//                if (CollectionUtils.isEmpty(addIndex)) {
//                    //创建子表索引
//                    pConfigDBMapper.commonCreatIndex(tableName, indexName, indexFromIndexName);
//                }
//            }
//        }
//        return true;
//    }
    @Test
    public void test() throws Exception{
        String text = "廖志伟的微信号：SeniorRD";
        System.out.println(replaceSensitive(text, "*", 3, text.length()));
        System.out.println(encryptSensitive(text, "md5"));
        System.out.println(randomSensitive(text,1,text.length()));
        System.out.println(deleteSensitive(text,7,text.length()));
    }
    /**
     * 将字符串中的敏感信息替换为固定字符
     * @param text       原始文本
     * @param replaceStr 替换字符
     * @param startIndex 替换起始位置（从0开始）
     * @param endIndex   替换终止位置
     * @return 替换敏感信息后的字符串
     */
    public String replaceSensitive(String text, String replaceStr, int startIndex, int endIndex) {
        char[] array = text.toCharArray();
        for (int i = startIndex; i < endIndex; i++) {
            if (Character.isLetterOrDigit(array[i])) {
                array[i] = replaceStr.charAt(0);
            }
        }
        return new String(array);
    }
    /**
     * 对指定字符串进行加密处理
     * @param text      待加密文本
     * @param algorithm 加密算法
     * @return 加密后的文本
     */
    public String encryptSensitive(String text, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(text.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }
    /**
     * 将指定字符串中的敏感信息替换为随机数
     * @param text       原始文本
     * @param startIndex 替换起始位置（从0开始）
     * @param endIndex   替换终止位置
     * @return 替换敏感信息后的字符串
     */
    public String randomSensitive(String text, int startIndex, int endIndex) {
        char[] array = text.toCharArray();
        Random random = new Random();
        for (int i = startIndex; i < endIndex; i++) {
            if (Character.isLetterOrDigit(array[i])) {
                array[i] = (char) ('0' + random.nextInt(10));
            }
        }
        return new String(array);
    }
    /**
     * 删除指定字符串中的敏感信息
     * @param text       原始文本
     * @param startIndex 删除起始位置（从0开始）
     * @param endIndex   删除终止位置
     * @return 删除敏感信息后的字符串
     */
    public String deleteSensitive(String text, int startIndex, int endIndex) {
        StringBuilder sb = new StringBuilder(text);
        sb.delete(startIndex, endIndex);
        return sb.toString();
    }
}
