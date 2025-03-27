//package com.yunxi.search.model.entity;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.Field;
//
///**
// * @author zhiweiLiao
// * @Description MongoDB的文档，@Id表示主键，@Indexed是索引字段，@Field代表字段
// * @Date create in 2022/9/12 0012 21:12
// */
//
//
//@Document("mongodb_demo")
//public class MongodbDemo {
//
//    @Id
//    @Indexed
//    private String id;
//
//    @Field("name")
//    private String name;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//}
