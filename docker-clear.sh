#!/bin/bash
mvn clean && docker rmi -f metric-management:1.0 && docker compose down