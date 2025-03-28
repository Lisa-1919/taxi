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

WORKDIR /app

COPY target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar

COPY src/main/resources/application.yaml /app/config/application.yaml

EXPOSE 8765

ENTRYPOINT ["java", "-jar", "/app/gateway.jar", "--spring.config.location=/app/config/application.yaml"]