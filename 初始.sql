drop database if exists picshare;
create database picshare;
use picshare;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `real_name` varchar(10) NOT NULL,
  `password` varchar(6) NOT NULL DEFAULT '123456',
  `flag` int(11) NOT NULL DEFAULT '0' COMMENT '0学生 1领导 2管理员',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pic`;
CREATE TABLE `pic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_user` varchar(50) NOT NULL,
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `source_location` varchar(255) NOT NULL COMMENT '原图物理位置',
  `zipped_location` varchar(255) DEFAULT NULL COMMENT '压缩图片物理位置',
  `description` varchar(255) DEFAULT NULL,
  `source_link` varchar(255) DEFAULT NULL COMMENT '原图链接',
  `zipped_link` varchar(255) DEFAULT NULL COMMENT '压缩图片链接',
  `flag` int(1) DEFAULT '0' COMMENT '是否有压缩文件 0 没有 1 有',
  `read_num` int(11) NOT NULL DEFAULT '0' COMMENT '浏览数',
  `like_num` int(11) NOT NULL DEFAULT '0' COMMENT '喜欢数',
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pic__user` (`create_user`),
  KEY `pic__zippedLink` (`zipped_link`),
  CONSTRAINT `pic_user_username_fk` FOREIGN KEY (`create_user`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `picId` int(11) NOT NULL,
  `detail` varchar(100) DEFAULT NULL COMMENT '评论内容',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `comment__picId` (`picId`),
  KEY `comment__username` (`username`),
  CONSTRAINT `comment___picId` FOREIGN KEY (`picId`) REFERENCES `pic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment___username` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record` (
  `username` varchar(50) NOT NULL,
  `picId` int(11) NOT NULL,
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `like_record___picId` (`picId`),
  KEY `like_record___username` (`username`),
  CONSTRAINT `like_record___picId` FOREIGN KEY (`picId`) REFERENCES `pic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `like_record___username` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `read_record`;
CREATE TABLE `read_record` (
  `username` varchar(50) NOT NULL,
  `picId` int(11) NOT NULL,
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `read_record___picId` (`picId`),
  KEY `read_record___username` (`username`),
  CONSTRAINT `read_record___picId` FOREIGN KEY (`picId`) REFERENCES `pic` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `read_record___username` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `top_pic`;
CREATE TABLE `top_pic` (
  `id` int(11) NOT NULL,
  KEY `top_pic_pic_id_fk` (`id`),
  CONSTRAINT `top_pic_pic_id_fk` FOREIGN KEY (`id`) REFERENCES `pic` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `wish`;
CREATE TABLE `wish` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `detail` varchar(100) DEFAULT NULL,
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `wish_user_username_fk` (`username`),
  CONSTRAINT `wish_user_username_fk` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
