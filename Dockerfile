FROM maven:3 as build

COPY ./ /build

RUN cd /build && mvn clean install

RUN find /build -name "*.jar"

FROM openjdk

COPY --from=build /build/target/*.jar /app/app.jar

CMD java -jar /app/app.jar
