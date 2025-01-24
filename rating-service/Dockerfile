FROM openjdk:17.0.2-jdk-slim AS builder
WORKDIR /app

COPY target/rating-0.0.1-SNAPSHOT.jar /app/rating.jar

RUN java -Djarmode=layertools -jar rating.jar extract

FROM openjdk:17.0.2-jdk-slim
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

EXPOSE 8084

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
