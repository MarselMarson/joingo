FROM gradle:8.5.0-jdk21-alpine as builder
WORKDIR /app
COPY . /app/.
RUN gradle build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/*.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "/app/*.jar"]