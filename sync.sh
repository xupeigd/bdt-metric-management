#!/bin/bash
targetHost='page.quicksand.com'
targetDir='/usr/local/dev/sync-wsp/bdt-metric-management'
cleanCommand="sh ${targetDir}/sync-clean.sh"
#deployCommand="sh ${targetDir}/sync-docker-redeploy.sh"
redeployCommand="sudo docker rm -f metric-management && sudo docker rmi -f metric-management:1.0 && sudo docker compose -f ${targetDir}/docker-compose.yml up metric-management"
mvn clean && mvn package -U -DskipTests=true && echo "package done !" && ssh $targetHost -t $cleanCommand && sh ./sync-transport.sh && ssh $targetHost -t $redeployCommand