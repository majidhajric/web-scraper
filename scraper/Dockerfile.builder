FROM maven:3.6.3-adoptopenjdk-11 AS MAVEN_BUILDER
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package -DskipTests=true

FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=/opt/spring/app.jar
ARG PORT=8080
RUN apk add --no-cache bash
COPY --from=MAVEN_BUILDER /tmp/target/*.jar ${JAR_FILE}
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
