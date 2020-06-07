FROM gradle:6.0-jdk11 as compile-stage

WORKDIR /build
COPY . .
RUN gradle :server:shadowJar -Dorg.gradle.daemon=false

FROM openjdk:11-jre-slim
WORKDIR /run
COPY --from=compile-stage /build/server/build/libs/server.jar .

# Default database settings
ENV MYSQL_USER pu_SEM-pool86
ENV MYSQL_URL projects-db.ewi.tudelft.nl
ENV MYSQL_DATABASE projects_SEM-pool86
ENV DATABASE SQL
ENV CACHE MEMORY

EXPOSE 8080
CMD exec java -jar server.jar