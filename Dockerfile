FROM amazoncorretto:17
WORKDIR /app
COPY ./target/seMethods-0.1.0.2-SNAPSHOT-jar-with-dependencies.jar /app/seMethods.jar
ENTRYPOINT ["java", "-jar", "/app/seMethods.jar"]