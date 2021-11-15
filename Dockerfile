FROM ghcr.io/wisvch/spring-boot-base:2.5.5
COPY ./build/libs/events.jar /srv/events.jar
CMD ["/srv/events.jar"]
