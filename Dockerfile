# Stage 1: build jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# copy pom và tải dependency trước (tăng tốc build)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# copy source code
COPY src ./src

# build jar
RUN mvn -q -B package -DskipTests

# Stage 2: image chạy ứng dụng
FROM eclipse-temurin:21-jre
WORKDIR /app

# CHÚ Ý: sửa tên jar cho đúng với target/
# Ví dụ: ecommerce-springboot-0.0.1-SNAPSHOT.jar hoặc ecommerce-springboot.jar
COPY --from=build /app/target/ecommerce-springboot.jar app.jar

EXPOSE 8080

# bật profile docker
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java","-jar","/app/app.jar"]
