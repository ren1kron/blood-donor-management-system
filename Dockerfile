FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./
COPY src/ src/

RUN chmod +x gradlew
RUN ./gradlew clean bootWar --no-daemon -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.war app.war

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.war"]
