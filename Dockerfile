FROM m3base:1.111

MAINTAINER Page <page.quicksand.com>

ADD ./target/bdt-metric-management.jar /bdt-metric-management.jar

ENTRYPOINT ["nohup","java","-jar","-agentlib:jdwp=transport=dt_socket,address=8989,server=y,suspend=n","/bdt-metric-management.jar","&"]