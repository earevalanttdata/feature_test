# Usa una imagen base de Java 21 LTS, que es lo que indica tu pom.xml
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

COPY target/assets-proxy-0.0.1-SNAPSHOT.jar assets-proxy.jar

# Expone el puerto 8080 (que es el puerto que usa tu application.yml)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "assets-proxy.jar"]