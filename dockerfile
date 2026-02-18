# Construir utilizando el comando "docker build -t carvillf/homework-1:javashell"

FROM eclipse-temurin:21-alpine-3.23

RUN apk update && \
    apk add --no-cache iproute2

WORKDIR /app
COPY ./jsh/src /app

RUN javac *.java

RUN mkdir -p /folder1/folder2/folder3/folder4/folder5/folder6/folder7
RUN touch /folder1/folder1.txt && \
    touch /folder1/folder2/folder2.txt && \
    touch /folder1/folder2/folder3/folder3.txt && \
    touch /folder1/folder2/folder3/folder4/folder4.txt && \
    touch /folder1/folder2/folder3/folder4/folder5/folder5.txt && \
    touch /folder1/folder2/folder3/folder4/folder5/folder6/folder6.txt && \
    touch /folder1/folder2/folder3/folder4/folder5/folder6/folder7/folder7.txt

CMD ["java", "JSH"]