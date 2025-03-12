# !/bin/bash
docker compose -f compile.yml up && docker compose -f compile.yml down && docker rm -f metric-management && docker rmi -f metric-management:1.0 && docker compose up metric-management &