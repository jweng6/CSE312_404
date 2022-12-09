FROM ubuntu
RUN apt-get update
RUN apt-get -y install mysql-server
RUN apt-get -y install openjdk-11-jdk

COPY svc /svc
EXPOSE 9000 9443
CMD /svc/bin/start -Dhttps.port=9443 -Dplay.crypto.secret=secret
