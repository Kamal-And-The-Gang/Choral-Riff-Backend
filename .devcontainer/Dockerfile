FROM mcr.microsoft.com/devcontainers/java:21-bookworm

ARG INSTALL_MAVEN="true"
ARG MAVEN_VERSION=""

ARG INSTALL_GRADLE="false"
ARG GRADLE_VERSION=""

RUN if [ "${INSTALL_MAVEN}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; fi \
    && if [ "${INSTALL_GRADLE}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install gradle \"${GRADLE_VERSION}\""; fi

# [Optional] Uncomment this section to install additional OS packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1




# todo si prod

# FROM mcr.microsoft.com/devcontainers/java:21-bookworm

# # Installer Maven si nécessaire
# ARG INSTALL_MAVEN="true"
# ARG MAVEN_VERSION=""
# RUN if [ "${INSTALL_MAVEN}" = "true" ]; then \
#         su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; \
#     fi

# # Définir le répertoire de travail
# WORKDIR /app

# # Copier le code
# COPY pom.xml .
# COPY src ./src

# # Compiler l'application
# RUN mvn clean package -DskipTests

# # Lancer le JAR
# CMD ["java", "-jar", "target/app.jar"]