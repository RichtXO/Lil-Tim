FROM gradle:jdk15 AS build
WORKDIR /home/gradle/src
ADD --chown=gradle . .
RUN gradle build --no-daemon

FROM openjdk:15.0.2
COPY --from=build /home/gradle/src/build/libs/*all.jar /tmp/Lil-Tim.jar
WORKDIR /tmp
RUN ls -a
ENTRYPOINT ["java","-jar","Lil-Tim.jar"]