FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update -y \
 && apt-get install -y curl wget tar git unzip ca-certificates gnupg \
 && apt-get clean && rm -rf /var/lib/apt/lists/*

ENV GRADLE_USER_HOME=/home/gradle
RUN useradd -ms /bin/bash dev \
 && mkdir -p ${GRADLE_USER_HOME} \
 && chown -R dev:dev ${GRADLE_USER_HOME}

USER dev
WORKDIR /workspace
