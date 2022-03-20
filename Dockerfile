FROM openjdk:15
COPY ./target/server-0.0.1.jar server.jar
ENTRYPOINT ["java","-jar","server.jar"]
