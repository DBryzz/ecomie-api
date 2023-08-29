FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /build
COPY . /build
#COPY ./src /build/src
#COPY ./pom.xml /build
#COPY ./.env /build

RUN mvn clean install -DskipTests

FROM openjdk:17-alpine AS deploy

COPY --from=build /build/target/ecomie.jar /ecomie-service/ecomie.jar
WORKDIR /ecomie-service
ENTRYPOINT ["java", "-jar", "ecomie.jar"]
EXPOSE 8081