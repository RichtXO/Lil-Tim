FROM arm32v7/opendjk
COPY ./build/libs/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Lil-Tim"]