## Overview

This project demonstrates deployment of a microservices-based Java application in Kubernetes.

The system includes:
- **Eureka Server** for service discovery
- **Songs Microservice**
- **Resources Microservice**
- **Databases** deployed using StatefulSets

## Architecture

The application includes:

- **Namespace**: `k8s-program`
- **Services**:
    - NodePort services for external access to applications
- **Deployments**:
    - 2 replicas for each application service
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
├── eureka/
├── resources-service/
├── songs-service/
└──README.md```
