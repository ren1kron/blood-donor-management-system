FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
COPY src/ src/

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
