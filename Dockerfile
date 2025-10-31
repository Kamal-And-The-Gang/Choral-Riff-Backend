# Etape 1 : compilation du projet
FROM maven:3.9.5-eclipse-temurin-21-alpine as builder
WORKDIR /opt/choral-riff-build
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install -DskipTests

# Etape 2 : conteneur en production
FROM eclipse-temurin:21-jre-alpine
EXPOSE 8000/tcp
WORKDIR /opt/choral-riff-api

# Copie du JAR
COPY --from=builder /opt/choral-riff-build/target/choral-riff-0.0.1-SNAPSHOT.jar choral-riff-0.0.1-SNAPSHOT.jar

# Commande de démarrage
ENTRYPOINT ["java","-jar","/opt/choral-riff-api/choral-riff-0.0.1-SNAPSHOT.jar"]