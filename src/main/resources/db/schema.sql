DROP TABLE IF EXISTS `phoenix_user`;
CREATE TABLE `phoenix_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `name` VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(32) NOT NULL COMMENT '加密后的密码',
  `salt` VARCHAR(32) NOT NULL COMMENT '加密使用的盐',
  `email` VARCHAR(32) NOT NULL UNIQUE COMMENT '邮箱',
  `phone_number` VARCHAR(16) NOT NULL UNIQUE COMMENT '手机号码',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态，-1：逻辑删除，0：禁用，1：启用',
  `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '上次登录时间',
  `last_update_time` DATETIME DEFAULT NOW() COMMENT '上次更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='phoenix用户示例表';


DROP TABLE IF EXISTS `voucher_publish`;
CREATE TABLE `voucher_publish` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `user_d` BIGINT NOT NULL COMMENT '哪个用户发布的代金券信息',
  `merchant` VARCHAR(32) NOT NULL COMMENT '商家',
  `voucher_amount` BIGINT NOT NULL COMMENT '商家发布的代金券总金额',
  `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NOW() COMMENT '上次更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家发布的代金券';
