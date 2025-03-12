#!/bin/bash
targetHost=page.quicksand.com
targetDir=/usr/local/dev/sync-wsp/bdt-metric-management
scpCommand="scp ./target/bdt-metric-management.jar $targetHost:$targetDir/target/"
$scpCommand && echo "$scpCommand execute done !"