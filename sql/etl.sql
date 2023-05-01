/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.2.199_3307_root
 Source Server Type    : MySQL
 Source Server Version : 50740
 Source Host           : 192.168.2.199:3307
 Source Schema         : etl

 Target Server Type    : MySQL
 Target Server Version : 50740
 File Encoding         : 65001

 Date: 29/04/2023 16:55:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for etl_log_keyword
-- ----------------------------
DROP TABLE IF EXISTS `etl_log_keyword`;
CREATE TABLE `etl_log_keyword`  (
  `KEY_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关键字ID',
  `KEY_WORDS` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关键字匹配内容',
  `KEY_DESC` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关键字业务描述',
  `ERROR_TYPE` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误类型',
  `ERROR_MSG` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
  `HOST_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务主机ip',
  `PORT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务主机端口',
  PRIMARY KEY (`KEY_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件归档数据，已经根据关键字智能归档' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of etl_log_keyword
-- ----------------------------

-- ----------------------------
-- Table structure for etl_log_keyword_match
-- ----------------------------
DROP TABLE IF EXISTS `etl_log_keyword_match`;
CREATE TABLE `etl_log_keyword_match`  (
  `KEY_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关键字ID',
  `MATCH_TYPE` int(2) NULL DEFAULT NULL COMMENT '匹配模式：1关键字匹配；',
  `KEY_WORDS` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关键字匹配内容',
  `KEY_DESC` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关键字业务描述',
  `KEY_TYPE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '关键字类型：1-历史日志关键字，2-shell组件错误关键字，3-shell组件忽略错误关键字，4-sql组件错误关键字',
  `SYSTEM_ID` int(11) NULL DEFAULT NULL COMMENT '系统id',
  PRIMARY KEY (`KEY_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '关键字匹配，逐行读取文件，分析每一行文本的依据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of etl_log_keyword_match
-- ----------------------------
INSERT INTO `etl_log_keyword_match` VALUES (1, NULL, '获取连接异常', '原因：数据库连接异常，解决：请解决数据库不能连接问题', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (2, NULL, 'Error 10001', '原因：表不存在，解决：请创建该表', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (3, NULL, 'Error 10004', '原因：字段不存在，解决：请检查并修改SQL语句', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (4, NULL, 'AlreadyExistsException', '原因：表已存在，解决：请检查', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (5, NULL, 'Insufficient privileges to execute delete', '原因：没有删除权限', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (6, NULL, 'Found in more than One Tables', '原因：有一列在字段在多个表中出现,但是没加表名前缀', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (7, NULL, 'ParseException', '原因：语法解析错误', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (8, NULL, '语句异常', '原因：SQL语法错误', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (9, NULL, 'ORA-00900', '原因：无效 SQL 语句，解决：请重新检查并改动该SQL语句', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (10, NULL, 'ORA-00942', '原因：表或视图不存在，解决：请检查表是否创建或者表名是否使用正确', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (11, NULL, 'java.net.SocketTimeoutException: Read timed out', '原因：大数据集群返回异常，解决：请联系管理员处理', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (12, NULL, 'Lock wait timeout exceeded; try restarting transaction', '原因：事务没有提交导致锁等待，解决：请提交该事务', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (13, NULL, 'doesn\'t exist', '原因：表不存在，解决：请创建该表', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (14, NULL, 'Unknown column', '原因：字段不存在，解决：请修改SQL语句', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (15, NULL, 'Couldn\'t get row from result set', '原因：不能获取结果，解决：请检查表中是否有数据', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (17, NULL, 'ERROR000', '原因：日志报错', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (18, NULL, 'already exists', '原因：表已存在，解决：表名重命名', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (19, NULL, 'Error connecting to database', '原因：数据库连接异常，解决：检查数据库连接', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (20, NULL, 'Connect to database fail, trying to connect again...', '原因：数据库连接异常', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (21, NULL, 'Caused by:', '错误原因：测试用', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (22, NULL, '123,abc', 'HIVE_zm', '2', 169);
INSERT INTO `etl_log_keyword_match` VALUES (23, NULL, '123,ERROR', 'HBASE', '2', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (24, NULL, '111,222,333', 'SPARK', '2', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (25, NULL, 'Unknown column', 'hive', '2', 244);
INSERT INTO `etl_log_keyword_match` VALUES (26, NULL, 'already exists', 'HIVE', '3', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (27, NULL, 'ignore,123', 'HIVE_zm', '3', 244);
INSERT INTO `etl_log_keyword_match` VALUES (28, NULL, '数据质量规则实例接口异常', '原因：数据质量规则实例接口异常，解决：联系相关维护人员检查该接口', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (29, NULL, '无法准备执行任务', '原因：任务加载失败，解决：请查看后台日志分析原因', '1', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (30, NULL, '123;ERROR', 'HBASE', '4', NULL);
INSERT INTO `etl_log_keyword_match` VALUES (31, NULL, 'doesn\'t exist;error', 'MYSQL', '4', NULL);

-- ----------------------------
-- Table structure for r_slave
-- ----------------------------
DROP TABLE IF EXISTS `r_slave`;
CREATE TABLE `r_slave`  (
  `ID_SLAVE` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `HOST_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `PORT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `USERNAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `PASSWORD` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `PROXY_HOST_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `PROXY_PORT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `NON_PROXY_HOSTS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MASTER` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ID_SYSTEM_DEF` int(11) NOT NULL,
  `HOST_USER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `HOST_PASSWORD` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `HOST_FTP_PORT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `HOST_FILE_PATH` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `HOST_START_SCRIPT` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ID_CLASS` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_SLAVE`, `ID_SYSTEM_DEF`) USING BTREE,
  INDEX `INDEX_NAME`(`NAME`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_slave
-- ----------------------------' ||

INSERT INTO `r_slave` VALUES (24, '192.168.2.199_2345', '192.168.2.199', '8002', 'root', '5541855', NULL, NULL, NULL, NULL, 23, 'root', '5541855', NULL, '/opt', NULL, NULL, '2023-04-07 21:16:06');

SET FOREIGN_KEY_CHECKS = 1;
