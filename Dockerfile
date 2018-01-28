FROM wisvch/spring-boot-base:1
COPY ./build/libs/events.jar /srv/events.jar
USER spring-boot
CMD ["/srv/events.jar"]
