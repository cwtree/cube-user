FROM hub.komect.com:10443/hybase/jdk:1.8.0_241

RUN mkdir -p /apprun/
COPY ./cube-project-1.0.zip /apprun/
RUN cd /apprun/ && tar xvf cube-user-1.0.zip
WORKDIR /apprun/cube-user/
# ENTRYPOINT ["top"]
EXPOSE 9288
ENTRYPOINT ["java","-jar","-Xms2000m","-Xmx2000m","/apprun/cube-user/cube-user.jar"]
