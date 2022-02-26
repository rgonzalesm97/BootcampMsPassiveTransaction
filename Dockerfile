FROM openjdk:11

COPY ["./target/passiveTransaction-0.0.1-SNAPSHOT.jar", "/usr/app/"]

CMD ["java", "-jar", "/usr/app/passiveTransaction-0.0.1-SNAPSHOT.jar"]

EXPOSE 8083