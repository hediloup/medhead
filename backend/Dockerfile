# Étape 1: Build avec Maven
FROM maven:3.8.4-openjdk-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Télécharger les dépendances (cache layer)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src src

# Compiler l'application
RUN mvn clean package -DskipTests

# Étape 2: Runtime avec OpenJDK
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Variables d'environnement pour la base de données
ENV SPRING_PROFILES_ACTIVE=test
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/medhead_db
ENV SPRING_DATASOURCE_USERNAME=medhead_user
ENV SPRING_DATASOURCE_PASSWORD=medhead_password

# Variables d'environnement pour les performances et éviter les problèmes de métriques
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=test -Dmanagement.endpoints.enabled-by-default=false -Dmanagement.endpoint.health.enabled=true -Dmanagement.metrics.system.enabled=false -Dmanagement.metrics.process.enabled=false"

# Commande pour démarrer l'application avec optimisations
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
