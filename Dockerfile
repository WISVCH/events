FROM ghcr.io/wisvch/spring-boot-base:2.5.5@sha256:7a9a757da92f8cbd6310bffa5f5ba28937ac02b2efc61fdfc3fcc3c0ad46c4c7
COPY ./build/libs/events.jar /srv/events.jar
CMD ["/srv/events.jar"]
