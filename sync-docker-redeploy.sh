#!/bin/bash
targetDir=/usr/local/dev/sync-wsp/bdt-metric-management
upCommond="sudo docker compose -f $targetDir/docker-compose.yml up metric-management"
sudo docker rm -f metric-management && sudo docker rmi -f metric-management:1.0 && echo 'Call Deplayed !' && $upCommond &