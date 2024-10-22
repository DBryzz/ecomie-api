FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /build
#COPY . /build

#

COPY ./pom.xml /build
RUN mvn dependency:go-offline

#RUN rm -rf /build/pom.xml

COPY ./src /build/src

COPY ./.env /build/.env
#COPY ./.env /build/.env
#RUN mvn clean install -DskipTests

#RUN rm -rf /build/target

RUN mvn clean package -DskipTests



FROM openjdk:17-alpine AS deploy

COPY --from=build /build/target/ecomie.jar /ecomie-service/ecomie.jar
WORKDIR /ecomie-service
ENTRYPOINT ["java", "-jar", "ecomie.jar"]
EXPOSE 8081