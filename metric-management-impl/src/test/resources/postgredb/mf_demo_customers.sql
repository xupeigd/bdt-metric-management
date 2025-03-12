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

 Date: 17/08/2022 17:13:57
*/


-- ----------------------------
-- Table structure for mf_demo_customers
-- ----------------------------
DROP TABLE IF EXISTS "postgresdb"."mf_demo_customers";
CREATE TABLE "postgresdb"."mf_demo_customers"
(
    "id_customer" text COLLATE "pg_catalog"."default",
    "country"     text COLLATE "pg_catalog"."default",
    "ds"          timestamp(6)
)
;
ALTER TABLE "postgresdb"."mf_demo_customers" OWNER TO "postgresdb";

-- ----------------------------
-- Records of mf_demo_customers
-- ----------------------------
BEGIN;
INSERT INTO "postgresdb"."mf_demo_customers" ("id_customer", "country", "ds")
VALUES ('c500001', 'FR', '2022-03-07 00:00:00');
INSERT INTO "postgresdb"."mf_demo_customers" ("id_customer", "country", "ds")
VALUES ('c500003', 'MX', '2022-03-08 00:00:00');
INSERT INTO "postgresdb"."mf_demo_customers" ("id_customer", "country", "ds")
VALUES ('c500000', 'US', '2022-03-10 00:00:00');
INSERT INTO "postgresdb"."mf_demo_customers" ("id_customer", "country", "ds")
VALUES ('c500002', 'CA', '2022-03-07 00:00:00');
COMMIT;
