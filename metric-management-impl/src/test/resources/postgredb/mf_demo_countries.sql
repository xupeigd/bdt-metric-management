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

 Date: 17/08/2022 16:35:12
*/


-- ----------------------------
-- Table structure for mf_demo_countries
-- ----------------------------
DROP TABLE IF EXISTS "postgresdb"."mf_demo_countries";
CREATE TABLE "postgresdb"."mf_demo_countries"
(
    "country" text COLLATE "pg_catalog"."default",
    "region"  text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "postgresdb"."mf_demo_countries" OWNER TO "postgresdb";

-- ----------------------------
-- Records of mf_demo_countries
-- ----------------------------
BEGIN;
INSERT INTO "postgresdb"."mf_demo_countries" ("country", "region")
VALUES ('US', 'NA');
INSERT INTO "postgresdb"."mf_demo_countries" ("country", "region")
VALUES ('MX', 'NA');
INSERT INTO "postgresdb"."mf_demo_countries" ("country", "region")
VALUES ('CA', 'NA');
INSERT INTO "postgresdb"."mf_demo_countries" ("country", "region")
VALUES ('BR', 'SA');
INSERT INTO "postgresdb"."mf_demo_countries" ("country", "region")
VALUES ('GR', 'EU');
INSERT INTO "postgresdb"."mf_demo_countries" ("country", "region")
VALUES ('FR', 'EU');
COMMIT;
