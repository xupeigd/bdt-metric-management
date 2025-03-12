#!/usr/bin/env bash
ls /mnt/d/Dev/maven/repo && mvn clean && cd metric-management-api && mvn install -U -DskipTests=true && cd ../ && mvn package -U -DskipTests