FROM openjdk:17.0.2-jdk-slim AS builder
WORKDIR /app

COPY target/storage-service-0.0.1-SNAPSHOT.jar /app/storage-service.jar

RUN java -Djarmode=layertools -jar storage-service.jar extract

FROM openjdk:17.0.2-jdk-slim
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

EXPOSE 8086

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
