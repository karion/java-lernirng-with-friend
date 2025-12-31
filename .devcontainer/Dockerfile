FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

# Maven jest zwykle już w obrazie; jakby nie było:
RUN apt-get update && apt-get install -y maven

# (opcjonalnie) narzędzia:
RUN apt-get update && apt-get install -y curl git && rm -rf /var/lib/apt/lists/*
