FROM amazoncorretto:17
WORKDIR /app
COPY ./target/*-jar-with-dependencies.jar /app/seMethods.jar
ENTRYPOINT ["java", "-jar", "/app/seMethods.jar"]