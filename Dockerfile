FROM openjdk:17
WORKDIR /app
COPY target/client-management-1.0-SNAPSHOT.jar app.jar
EXPOSE 8282
CMD ["java", "-jar", "app.jar"]

FROM mysql:latest
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=clientdb
ENV MYSQL_PASSWORD=root
