FROM gradle:7.6.1-jdk17-alpine AS TEMP_BUILD_IMAGE

ENV APP_HOME=/app/backend
WORKDIR $APP_HOME
COPY build.gradle settings.gradle $APP_HOME

COPY . $APP_HOME

USER root                
RUN chown -R gradle $APP_HOME
USER gradle              

RUN gradle --no-daemon build


FROM eclipse-temurin:17-jdk-alpine
ENV APP_HOME=/app/backend
WORKDIR $APP_HOME

COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/*.jar app.jar

ADD /images/players/NonPhoto.png $APP_HOME/images/players/NonPhoto.png
ADD /images/teams_logo/NoLogo.svg $APP_HOME/images/teams_logo/NoLogo.svg

ADD src/main/resources/config.properties $APP_HOME/src/main/resources/config.properties

EXPOSE 8080

ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://db:27017/kast", "-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]