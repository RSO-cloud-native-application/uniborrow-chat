# Uniborrow - Chat service

## Description

This service handles the chat operations between users.

## Prerequisites

Postgres database is needed. For local development use:

```
docker run --name uniborrow-chat-db \
    -e POSTGRES_DB=chat \
    -e POSTGRES_PASSWORD=postgres \ 
    -e POSTGRES_USER=dbuser \ 
    -p 5431:5432 \
    postgres:13
```

## Services

The service uses Consul for configuration and service discovery.

Keys are of form: `environments/dev/services/uniborrow-chat-service/1.0.0/config`.