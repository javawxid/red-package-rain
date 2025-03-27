/*
SQLyog Ultimate v10.00 Beta1
MySQL - 8.0.20 : Database - user
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`user` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `user`;

/*Table structure for table `tb_choose_spouse` */

CREATE TABLE `tb_choose_spouse` (
  `Id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `sex` bigint DEFAULT NULL COMMENT '性别 0女 1男',
  `age` bigint DEFAULT NULL COMMENT '年龄 年龄',
  `height` bigint DEFAULT NULL COMMENT '身高 单位',
  `weight` bigint DEFAULT NULL COMMENT '体重 单位kg',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址 地址',
  `education` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最高学历 程序需要校验学历是否合法 最高学历 程序需要校验学历是否合法',
  `industry` bigint DEFAULT NULL COMMENT '行业 需要程序枚举定义每个数值代表的行业 行业 需要程序枚举定义每个数值代表的行业',
  `marital_status` bigint DEFAULT NULL COMMENT '婚姻状况 0未婚 1已婚 2离异 婚姻状况 0未婚 1已婚 2离异',
  `departuretarget` bigint DEFAULT NULL COMMENT '脱单目标 0：1年内结婚 1:3年内结婚 2：不强求 3：只想要纯纯的恋爱 4：先交友后恋爱 脱单目标 0：1年内结婚 1:3年内结婚 2：不强求 3：只想要纯纯的恋爱 4：先交友后恋爱',
  `nativeplace` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '籍贯',
  `income` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收入 月薪为k，年薪为w 收入 月薪为k，年薪为w',
  `Create_By` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Create_Date` datetime DEFAULT NULL,
  `Update_By` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Update_Date` datetime DEFAULT NULL,
  `Is_Delete` bigint DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='择偶标准';

/*Table structure for table `tb_red_package_log` */

CREATE TABLE `tb_red_package_log` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `red_package_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `activity_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `part_red_package` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `message` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `messageId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `tb_talk` */

CREATE TABLE `tb_talk` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `talk` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '话题',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='话题';

/*Table structure for table `tb_user` */

CREATE TABLE `tb_user` (
  `Id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `photo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '照片',
  `sex` bigint DEFAULT NULL COMMENT '性别 0女 1男',
  `age` bigint DEFAULT NULL COMMENT '年龄',
  `nickename` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `height` bigint DEFAULT NULL COMMENT '身高 单位cm',
  `weight` bigint DEFAULT NULL COMMENT '体重 单位kg',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址',
  `education` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最高学历 程序需要校验学历是否合法',
  `industry` bigint DEFAULT NULL COMMENT '行业 需要程序枚举定义每个数值代表的行业',
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职业',
  `marital_status` bigint DEFAULT NULL COMMENT '婚姻状况 0未婚 1已婚 2离异',
  `departuretarget` bigint DEFAULT NULL COMMENT '脱单目标 0：1年内结婚 1:3年内结婚 2：不强求 3：只想要纯纯的恋爱 4：先交友后恋爱',
  `income` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收入 月薪为k，年薪为w',
  `mobile` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `user_status` bigint DEFAULT NULL COMMENT '用户状态 1正常 2冻结 3注销',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

/*Table structure for table `tb_user_comment` */

CREATE TABLE `tb_user_comment` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '评论',
  `type` bigint DEFAULT NULL COMMENT '类型 0针对动态进行评论 1针对评论进行评论',
  `association_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联id',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户评论';

/*Table structure for table `tb_user_follow` */

CREATE TABLE `tb_user_follow` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `user_id` bigint DEFAULT NULL COMMENT '用户id 当前用户',
  `follow_id` bigint DEFAULT NULL COMMENT '关注用户id 当前用户关注的用户',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户关注表';

/*Table structure for table `tb_user_hobby` */

CREATE TABLE `tb_user_hobby` (
  `Id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `hobby` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '兴趣爱好',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户兴趣爱好';

/*Table structure for table `tb_user_likes` */

CREATE TABLE `tb_user_likes` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `type` bigint DEFAULT NULL COMMENT '类型 0针对动态进行点赞 1针对评论进行点赞',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户点赞';

/*Table structure for table `tb_user_photo` */

CREATE TABLE `tb_user_photo` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `Photo_address` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '照片地址',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户照片';

/*Table structure for table `tb_user_reports` */

CREATE TABLE `tb_user_reports` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `report` bigint DEFAULT NULL COMMENT '举报 0资料作假 1垃圾广告 2低俗色情 3诽谤谩骂 4诈骗托儿 5线下行为不端 6其他违规行为',
  `report_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '举报内容 仅当report=6其他违规行为，说明具体行为',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户举报';

/*Table structure for table `tb_user_talk` */

CREATE TABLE `tb_user_talk` (
  `Id` bigint NOT NULL COMMENT '编号',
  `Create_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人 通常使用用户id存储',
  `Create_Date` datetime DEFAULT NULL COMMENT '创建时间',
  `Update_By` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人 通常使用用户id存储',
  `Update_Date` datetime DEFAULT NULL COMMENT '修改时间',
  `Is_Delete` bigint DEFAULT NULL COMMENT '是否删除 0正常 1已删除',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `talk_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '话题id',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户话题';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
