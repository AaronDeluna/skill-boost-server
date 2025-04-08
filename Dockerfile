FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . /app

RUN mvn clean install -DskipTests

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/target/SkillBoostService-1.0.0.jar /app/SkillBoostService.jar

CMD ["java", "-jar", "SkillBoostService.jar"]