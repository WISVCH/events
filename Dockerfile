FROM wisvch/alpine-java:8_server-jre_unlimited
ADD build/libs/events.jar /srv/events.jar
WORKDIR /srv
CMD "/srv/events.jar"