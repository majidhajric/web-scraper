FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=/opt/spring/app.jar
ARG PORT=8080
RUN apk add --no-cache bash
COPY /target/*.jar ${JAR_FILE}
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
