FROM wisvch/spring-boot-base:2.1
COPY ./build/libs/events.jar /srv/events.jar
CMD ["/srv/events.jar"]
