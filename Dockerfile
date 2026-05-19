# Stage 1: build jar
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copy pom và tải dependency trước (tăng tốc build)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# copy source code
COPY src ./src

# build jar (finalName trong pom.xml = ecommerce-springboot)
RUN mvn -q -B package -DskipTests

# Stage 2: image chạy ứng dụng
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/ecommerce-springboot.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
