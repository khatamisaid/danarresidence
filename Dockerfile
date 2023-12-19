FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
WORKDIR /usr
COPY application.danarresidence.yml /usr/yml/application.danarresidence.yml
WORKDIR /src
COPY /target/danarresidence-0.0.1.jar /src/danarresidence.jar
RUN apk add tzdata

CMD ["java" , "-jar", "/src/danarresidence.jar"]
RUN cp /usr/share/zoneinfo/Asia/Jakarta /etc/localtime
