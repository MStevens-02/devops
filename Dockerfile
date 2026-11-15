FROM amazoncorretto:17
WORKDIR /app
COPY ./target/seMethods.jar /app/seMethods.jar
ENTRYPOINT ["java", "-jar", "/app/seMethods.jar"]