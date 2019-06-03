https://travis-ci.com/awaniak/space-agency-data-hub.svg?branch=master

**(Instar)Space Agency Data Hub**

Example REST api

**How to run**

`mvn clean install`

`java -jar target/space-agency-data-hub-0.0.1-SNAPSHOT.jar`

Application works on localhost:8080

Under address: http://localhost:8080/swagger-ui.html#/ is available api documentation, to get access it is needed to log in with manager credentials

Manager credentials:
manager:password

Customer credentials: 
customer:password


Example queries:

curl -u manager:password -X GET "http://localhost:8080/missions"

curl -u customer:password -X GET "http://localhost:8080/products?missionName=Mission 1 - TEST"
