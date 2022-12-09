FROM openjdk:8-jre
RUN apt-get update
RUN apt-get -y install mysql-server
COPY svc /svc
EXPOSE 9000 9443
CMD /svc/bin/start -Dhttps.port=9443 -Dplay.crypto.secret=secret
