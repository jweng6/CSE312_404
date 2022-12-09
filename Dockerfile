FROM openjdk:8-jre
RUN apt-get install -y tzdata && ln -sf /usr/share/zoneinfo/America/New_York /etc/localtime
COPY svc /svc
EXPOSE 9000 9443
CMD /svc/bin/start -Dhttps.port=9443 -Dplay.crypto.secret=secret
