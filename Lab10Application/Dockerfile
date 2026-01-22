FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /data
COPY --from=builder /build/target/lab10-0.0.1-SNAPSHOT.jar /opt/app/app.jar
EXPOSE 8082
ENV DB_URL=jdbc:sqlite:lab10.db
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]

