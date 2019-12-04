FROM gradle:6.0.1-jdk8 AS build

WORKDIR /app

COPY . /app

RUN gradle bootJar -Dorg.gradle.daemon=false


FROM openjdk:8-jre-alpine

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
