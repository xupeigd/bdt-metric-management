-- ----------------------------
-- Table structure for mf_demo_customers
-- ----------------------------
DROP TABLE IF EXISTS `postgresdb`.`mf_demo_customers`;
CREATE TABLE `postgresdb`.`mf_demo_customers`
(
    `id_customer` text,
    `country`     text,
    `ds`          timestamp(6)
)
;
-- ----------------------------
-- Records of mf_demo_customers
-- ----------------------------
BEGIN;
INSERT INTO `postgresdb`.`mf_demo_customers` (`id_customer`, `country`, `ds`)
VALUES ('c500001', 'FR', '2022-03-07 00:00:00');
INSERT INTO `postgresdb`.`mf_demo_customers` (`id_customer`, `country`, `ds`)
VALUES ('c500003', 'MX', '2022-03-08 00:00:00');
INSERT INTO `postgresdb`.`mf_demo_customers` (`id_customer`, `country`, `ds`)
VALUES ('c500000', 'US', '2022-03-10 00:00:00');
INSERT INTO `postgresdb`.`mf_demo_customers` (`id_customer`, `country`, `ds`)
VALUES ('c500002', 'CA', '2022-03-07 00:00:00');
COMMIT;
