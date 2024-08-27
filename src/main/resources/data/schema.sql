SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE ='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddy
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `paymybuddy` DEFAULT CHARACTER SET utf8;
USE `paymybuddy`;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Table `paymybuddy`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`users` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(30) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE
);

-- -----------------------------------------------------
-- Table `paymybuddy`.`accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`accounts` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `balance` DOUBLE NOT NULL,
    `user_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_account_user_idx` (`user_id` ASC) VISIBLE,
    UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_account_user`
        FOREIGN KEY (`user_id`)
        REFERENCES `paymybuddy`.`users` (`id`)
);


-- -----------------------------------------------------
-- Table `paymybuddy`.`transactions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`transactions` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `amount` DOUBLE NOT NULL,
    `description` VARCHAR(140) CHARACTER SET 'latin1' COLLATE 'latin1_general_ci' NULL COMMENT 'Excerpt from ISO 20022 (RemittanceInformation)',
    `sender` INT NOT NULL,
    `receiver` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_transaction_sender_idx` (`sender` ASC) VISIBLE,
    INDEX `fk_transaction_receiver_idx` (`receiver` ASC) VISIBLE,
    CONSTRAINT `fk_transaction_sender`
        FOREIGN KEY (`sender`)
        REFERENCES `paymybuddy`.`users` (`id`),
    CONSTRAINT `fk_transaction_receiver`
        FOREIGN KEY (`receiver`)
        REFERENCES `paymybuddy`.`users` (`id`)
);


-- -----------------------------------------------------
-- Table `paymybuddy`.`user_relationships`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddy`.`user_relationships` (
    `user_id` INT NOT NULL,
    `relation_user_id` INT NOT NULL,
    PRIMARY KEY (`user_id`, `relation_user_id`),
    INDEX `fk_relation_user_id_idx` (`relation_user_id` ASC) VISIBLE,
    CONSTRAINT `fk_user_id`
        FOREIGN KEY (`user_id`)
        REFERENCES `paymybuddy`.`users` (`id`)
        ON DELETE CASCADE,
    CONSTRAINT `fk_relation_user_id`
        FOREIGN KEY (`relation_user_id`)
        REFERENCES `paymybuddy`.`users` (`id`)
        ON DELETE CASCADE
);