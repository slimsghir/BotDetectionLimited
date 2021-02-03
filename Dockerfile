FROM adoptopenjdk/openjdk11:latest
RUN mkdir /opt/app
COPY ./target/BotDetectionLimited-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/app/
COPY access.log.txt /opt/app/
WORKDIR /opt/app
ENTRYPOINT ["java", "-jar", "BotDetectionLimited-1.0-SNAPSHOT-jar-with-dependencies.jar","access.log.txt"]

