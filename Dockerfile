FROM gradle:jre15 AS build
WORKDIR /home/gradle/src
ADD --chown=gradle . .
RUN gradle build --no-daemon

FROM openjdk:15.0.2-slim
COPY --from=build /home/gradle/src/build/libs/*all.jar /tmp/Lil-Tim.jar
WORKDIR /tmp
ENTRYPOINT ["java","-jar","Lil-Tim.jar"]