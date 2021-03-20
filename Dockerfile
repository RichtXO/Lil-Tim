FROM openjdk:15.0.2-slim-buster
COPY ./build/libs/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Lil-Tim"]