FROM openjdk:8-jre
RUN apt update
RUN apt install mysql-server
RUN systemctl start mysql.service
COPY svc /svc
EXPOSE 9000 9443
CMD /svc/bin/start -Dhttps.port=9443 -Dplay.crypto.secret=secret
