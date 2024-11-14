FROM openjdk:17-jdk-slim as openjdk-jdk

RUN apt-get update && apt-get install -y --no-install-recommends binutils && rm -rf /var/lib/apt/lists/*

RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.security.jgss,java.security.sasl,java.sql,jdk.httpserver,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /customjre

FROM debian:bullseye-slim
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=openjdk-jdk /customjre $JAVA_HOME

RUN apt-get update
RUN apt-get install -y curl

WORKDIR /app

COPY target/eureka-0.0.1-SNAPSHOT.jar /app/eureka.jar
COPY src/main/resources/application.properties /app/config/application.properties

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/eureka.jar", "--spring.config.location=/app/config/application.properties"]
