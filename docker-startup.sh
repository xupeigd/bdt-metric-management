# !/bin/bash
mkdir -p /mnt/d/Dev/maven/repo && docker compose -f compile.yml up && docker compose -f compile.yml down && docker compose up &