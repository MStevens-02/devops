FROM amazoncorretto:17
WORKDIR /app
COPY ./target/seMethods-1.0-SNAPSHOT-jar-with-dependencies.jar /app/seMethods.jar
ENTRYPOINT ["java", "-jar", "/app/seMethods.jar"]