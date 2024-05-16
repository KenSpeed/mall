FROM adoptopenjdk/openjdk21:alpine
LABEL authors="kenspeedit"
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
