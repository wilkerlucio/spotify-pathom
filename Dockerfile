FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/spotify-pathom-0.0.1-SNAPSHOT-standalone.jar /spotify-pathom/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/spotify-pathom/app.jar"]
