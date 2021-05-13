FROM java:8

WORKDIR app

# Add jar file & configuration files
ADD ./build/libs/*.jar application.jar
ADD ./src/main/resources/ conf/

# Startup with application-docker.yaml configuration file
ENTRYPOINT ["java", "-jar", "application.jar", "--spring.config.location=./conf/application-docker.yaml"]
