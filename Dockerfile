FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew \
    && ./gradlew bootJar --no-daemon \
    && cp "$(find build/libs -name '*.jar' ! -name '*-plain.jar' -print -quit)" /app/app.jar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/app.jar app.jar

ENV TZ="Asia/Seoul"
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:+UseContainerSupport -Duser.timezone=Asia/Seoul"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
