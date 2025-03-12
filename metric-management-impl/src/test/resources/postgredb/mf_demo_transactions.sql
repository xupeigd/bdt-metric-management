/*
 Navicat Premium Data Transfer

 Source Server         : postgre@lm
 Source Server Type    : PostgreSQL
 Source Server Version : 140004
 Source Host           : page.quicksand.com:15432
 Source Catalog        : postgresdb
 Source Schema         : postgresdb

 Target Server Type    : PostgreSQL
 Target Server Version : 140004
 File Encoding         : 65001

 Date: 17/08/2022 17:14:44
*/


-- ----------------------------
-- Table structure for mf_demo_transactions
-- ----------------------------
DROP TABLE IF EXISTS "postgresdb"."mf_demo_transactions";
CREATE TABLE "postgresdb"."mf_demo_transactions"
(
    "id_transaction"         text COLLATE "pg_catalog"."default",
    "id_order"               text COLLATE "pg_catalog"."default",
    "id_customer"            text COLLATE "pg_catalog"."default",
    "transaction_amount_usd" float8,
    "transaction_type_name"  text COLLATE "pg_catalog"."default",
    "ds"                     timestamp(6)
)
;
ALTER TABLE "postgresdb"."mf_demo_transactions" OWNER TO "postgresdb";

-- ----------------------------
-- Records of mf_demo_transactions
-- ----------------------------
BEGIN;
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936437', 'o1007', 'c500001', 321.03, 'quick-buy', '2022-03-22 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936438', 'o1009', 'c500003', 444.89, 'cancellation', '2022-04-04 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936439', 'o1002', 'c500000', 129.68, 'cancellation', '2022-03-15 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936440', 'o1003', 'c500000', 133.86, 'buy', '2022-03-27 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936441', 'o1003', 'c500001', 296.73, 'cancellation', '2022-03-16 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936442', 'o1010', 'c500000', 148.48, 'cancellation', '2022-03-31 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936443', 'o1003', 'c500000', 183.12, 'alteration', '2022-03-27 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936444', 'o1008', 'c500003', 353.81, 'quick-buy', '2022-04-03 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936445', 'o1005', 'c500003', 116.76, 'cancellation', '2022-03-31 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936446', 'o1000', 'c500000', 179.34, 'cancellation', '2022-03-14 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936447', 'o1004', 'c500002', 273.16, 'quick-buy', '2022-03-16 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936448', 'o1004', 'c500001', 499.59, 'buy', '2022-03-15 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936449', 'o1000', 'c500002', 74.55, 'buy', '2022-03-22 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936450', 'o1006', 'c500002', 460.83, 'alteration', '2022-03-17 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936451', 'o1001', 'c500003', 147.92, 'alteration', '2022-03-30 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936452', 'o1008', 'c500001', 259.58, 'buy', '2022-03-25 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936453', 'o1006', 'c500003', 248.45, 'alteration', '2022-04-02 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936454', 'o1006', 'c500002', 215.14, 'cancellation', '2022-03-23 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936455', 'o1004', 'c500002', 353.66, 'buy', '2022-03-08 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936456', 'o1009', 'c500002', 309.65, 'alteration', '2022-03-31 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936457', 'o1008', 'c500002', 66.6, 'quick-buy', '2022-03-25 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936458', 'o1007', 'c500000', 87.42, 'buy', '2022-03-13 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936459', 'o1003', 'c500000', 100.4, 'cancellation', '2022-04-01 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936460', 'o1003', 'c500003', 161.03, 'alteration', '2022-03-30 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936461', 'o1002', 'c500002', 321.54, 'quick-buy', '2022-04-02 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936462', 'o1007', 'c500000', 450.62, 'buy', '2022-03-21 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936463', 'o1009', 'c500002', 266.9, 'buy', '2022-03-28 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936464', 'o1008', 'c500000', 79.88, 'quick-buy', '2022-03-10 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936465', 'o1004', 'c500001', 368.65, 'buy', '2022-03-26 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936466', 'o1002', 'c500002', 7.66, 'buy', '2022-03-09 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936467', 'o1003', 'c500000', 137.07, 'quick-buy', '2022-03-15 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936468', 'o1007', 'c500000', 179.91, 'buy', '2022-03-26 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936469', 'o1002', 'c500000', 208.37, 'cancellation', '2022-03-11 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936470', 'o1008', 'c500003', 448.13, 'cancellation', '2022-03-22 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936471', 'o1010', 'c500001', 426.25, 'alteration', '2022-03-25 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936472', 'o1003', 'c500003', 223.34, 'quick-buy', '2022-03-08 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936473', 'o1001', 'c500003', 225.86, 'cancellation', '2022-03-18 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936474', 'o1008', 'c500001', 192.98, 'buy', '2022-03-07 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936475', 'o1008', 'c500000', 130.81, 'cancellation', '2022-03-12 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936476', 'o1004', 'c500002', 379.29, 'alteration', '2022-03-31 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936477', 'o1000', 'c500001', 438.43, 'quick-buy', '2022-03-29 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936478', 'o1006', 'c500002', 295.03, 'quick-buy', '2022-03-07 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936479', 'o1007', 'c500003', 216.78, 'cancellation', '2022-03-31 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936480', 'o1005', 'c500001', 40.82, 'cancellation', '2022-03-23 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936481', 'o1005', 'c500002', 46.19, 'buy', '2022-03-30 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936482', 'o1009', 'c500001', 347.62, 'cancellation', '2022-03-29 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936483', 'o1000', 'c500002', 189.25, 'cancellation', '2022-03-21 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936484', 'o1001', 'c500000', 179.11, 'alteration', '2022-03-30 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936485', 'o1010', 'c500001', 258.72, 'alteration', '2022-03-21 00:00:00');
INSERT INTO "postgresdb"."mf_demo_transactions" ("id_transaction", "id_order", "id_customer", "transaction_amount_usd",
                                                 "transaction_type_name", "ds")
VALUES ('s59936486', 'o1001', 'c500002', 91.55, 'cancellation', '2022-03-10 00:00:00');
COMMIT;
