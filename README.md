## Overview

This project demonstrates deployment of a microservices-based Java application in Kubernetes.

The system includes:
- **Eureka Server** for service discovery
- **Songs Microservice**
- **Resources Microservice**
- **Databases** deployed using StatefulSets
- **NGINX Ingress Controller** for routing external traffic

Additionally, the project introduces Helm for templating and managing Kubernetes deployments across different environments.

## Architecture

The application includes:

- **Namespace**:
    - Default: `k8s-program`
    - Configurable via Helm values
- **Ingress**
  - NGINX Ingress Controller installed via Helm
  - Routes external traffic to microservices
  - Uses path-based routing and rewrite-target annotations
- **Services**:
    - NodePort services for external access to applications
- **Deployments**:
    - Configurable replica count via Helm (replicaCount)
    - Environment variables loaded from ConfigMaps
- **StatefulSets**:
    - Used for database components
    - Use Secrets for credentials
  - **Configuration Management**:
    - Secrets – store database usernames and passwords
    - ConfigMaps – store:
      - Application environment variables
      - SQL initialization scripts
- **Persistent Storage**:
    - PersistentVolume (manual)
    - PersistentVolumeClaim
- **Helm Integration**:
    - Helm chart located in ms-intro-helm/
    - Supports configurable:
      - namespace
      - replicaCount
    - Uses _helpers.tpl to generate reusable labels:
      - date (generated dynamically)
      - version (from Chart.yaml → appVersion)
    - Enables deployment into multiple isolated environments

## Helm Usage
Install with default values
```helm install msintro ./ms-intro-helm```
- Namespace: k8s-program
- Replicas: 2

## Ingress Routing
Ingress routes requests to internal services using path rules and rewrite annotations.

Examples:
```text
http://localhost/api/v1/songs      -> songs-ms
http://localhost/api/v1/resources  -> resources-ms
```

## Project Structure
```
microservices-intro-master/
├── deployment/
│   ├── app/ 
│   │   ├── eureka-deployment.yaml
│   │   ├── resources-ms-configmap.yaml
│   │   ├── resources-ms-deployment.yaml
│   │   ├── songs-ms-configmap.yaml
│   │   └── songs-ms-deployment.yaml
│   ├── db/
│   │   ├── db-secret.yaml
│   │   ├── resources-db-configmap.yaml
│   │   ├── resources-db-statefulset.yaml
│   │   ├── songs-db-configmap.yaml
│   │   ├── songs-db-job.yaml
│   │   └── songs-db-statefulset.yaml
│   └── namespace.yml
├── manual_pv/
│   ├── resources-db.yml
│   ├── resources-pv.yml
│   ├── resources-pvc.yml
│   ├── songs-db.yml
│   ├── songs-pv.yml
│   └── songs-pvc.yml
├── ms-intro-helm/
│   ├── templates/
│   │   ├── app/
│   │   │   ├── eureka-deployment.yaml
│   │   │   ├── eureka-service.yaml
│   │   │   ├── resources-ms-configmap.yaml
│   │   │   ├── resources-ms-deployment.yaml
│   │   │   ├── resources-ms-service.yaml
│   │   │   ├── songs-ms-configmap.yaml
│   │   │   ├── songs-ms-deployment.yaml
│   │   │   └── songs-ms-service.yaml
│   │   ├── db/
│   │   │   ├── db-secret.yaml
│   │   │   ├── resources-db-configmap.yaml
│   │   │   ├── resources-db-service.yaml
│   │   │   ├── resources-db-statefulset.yaml
│   │   │   ├── songs-db-configmap.yaml
│   │   │   ├── songs-db-job.yaml
│   │   │   ├── songs-db-service.yaml
│   │   │   └── songs-db-statefulset.yaml
│   │   ├── _helpers.tpl
│   │   └── ingress.yaml
│   ├── Chart.yaml
│   └── values.yaml
├── eureka/
├── resources-service/
├── songs-service/
└──README.md```
