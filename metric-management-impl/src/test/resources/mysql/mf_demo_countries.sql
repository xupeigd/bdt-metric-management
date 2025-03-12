-- ----------------------------
-- Table structure for mf_demo_countries
-- ----------------------------
DROP TABLE IF EXISTS `postgresdb`.`mf_demo_countries`;
CREATE TABLE `postgresdb`.`mf_demo_countries`
(
    `country` text,
    `region`  text
)
;

-- ----------------------------
-- Records of mf_demo_countries
-- ----------------------------
BEGIN;
INSERT INTO `postgresdb`.`mf_demo_countries` (`country`, `region`)
VALUES ('US', 'NA');
INSERT INTO `postgresdb`.`mf_demo_countries` (`country`, `region`)
VALUES ('MX', 'NA');
INSERT INTO `postgresdb`.`mf_demo_countries` (`country`, `region`)
VALUES ('CA', 'NA');
INSERT INTO `postgresdb`.`mf_demo_countries` (`country`, `region`)
VALUES ('BR', 'SA');
INSERT INTO `postgresdb`.`mf_demo_countries` (`country`, `region`)
VALUES ('GR', 'EU');
INSERT INTO `postgresdb`.`mf_demo_countries` (`country`, `region`)
VALUES ('FR', 'EU');
COMMIT;
