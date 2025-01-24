FROM openjdk:17.0.2-jdk-slim AS builder
WORKDIR /app

COPY target/ride-0.0.1-SNAPSHOT.jar /app/ride.jar

RUN java -Djarmode=layertools -jar ride.jar extract

FROM openjdk:17.0.2-jdk-slim
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

EXPOSE 8083

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
