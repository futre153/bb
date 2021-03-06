CREATE DATABASE IF NOT EXISTS `ofkpudb` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE ofkpudb;
CREATE TABLE IF NOT EXISTS `ofkpudb`.`users` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '',
  `username` VARCHAR(15) NOT NULL COMMENT '',
  `user_pass` VARCHAR(40) NOT NULL COMMENT '',
  `fullname` VARCHAR(128) NULL COMMENT '',
  `c_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `e_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '',
  `d_time` TIMESTAMP NULL COMMENT '',
  `creator` INT UNSIGNED NOT NULL COMMENT '',
  `modifier` INT UNSIGNED ZEROFILL NOT NULL COMMENT '',
  `verifier` INT UNSIGNED ZEROFILL NOT NULL COMMENT '',
  `disabled` CHAR(1) NOT NULL DEFAULT 'U' COMMENT '',
  `mfd` CHAR(1) NOT NULL DEFAULT 'N' COMMENT '',
  PRIMARY KEY (`id`, `username`)  COMMENT '',
  INDEX `username_users_UNIQUE` (`users` ASC) COMMENT '');
CREATE TABLE IF NOT EXISTS `ofkpudb`.`ip_addresses` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '',
  `user_id` INT UNSIGNED NOT NULL COMMENT '',
  `ip_address` VARCHAR(23) NOT NULL COMMENT '',
  PRIMARY KEY (`id`, `user_id`)  COMMENT '',
  INDEX `FK_ip_addresses_user_id_idx` (`user_id` ASC)  COMMENT '',
  CONSTRAINT `FK_ip_addresses_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `ofkpudb`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
CREATE TABLE IF NOT EXISTS `ofkpudb`.`applications` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '',
  `app_name` VARCHAR(64) NOT NULL COMMENT '',
  `app_url` VARCHAR(256) NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '');
CREATE TABLE IF NOT EXISTS `ofkpudb`.`user_roles` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '',
  `role_name` VARCHAR(15) NOT NULL COMMENT '',
  `app_id` INT UNSIGNED NOT NULL COMMENT '',
  `top_id` INT ZEROFILL NOT NULL COMMENT '',
  `role_desc` VARCHAR(128) NULL COMMENT '',
  PRIMARY KEY (`id`, `role_name`)  COMMENT '',
  INDEX `FK_app_id_user_roles_idx` (`app_id` ASC)  COMMENT '',
  CONSTRAINT `FK_app_id_user_roles`
    FOREIGN KEY (`app_id`)
    REFERENCES `ofkpudb`.`applications` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
CREATE TABLE IF NOT EXISTS `ofkpudb`.`user_roles_affinity` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '',
  `user_id` INT UNSIGNED NOT NULL COMMENT '',
  `role_id` INT UNSIGNED NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  INDEX `FK_user_roles_affinity_user_id_idx` (`user_id` ASC)  COMMENT '',
  INDEX `FK_user_roles_affinity_role_id_idx` (`role_id` ASC)  COMMENT '',
  CONSTRAINT `FK_user_roles_affinity_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `ofkpudb`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_user_roles_affinity_role_id`
    FOREIGN KEY (`role_id`)
    REFERENCES `ofkpudb`.`user_roles` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
CREATE TABLE IF NOT EXISTS `ofkpudb`.`profiles` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '',
  `p_name` VARCHAR(16) NOT NULL COMMENT '',
  `p_desc` VARCHAR(64) NOT NULL COMMENT '',
  PRIMARY KEY (`id`, `p_name`)  COMMENT '');
