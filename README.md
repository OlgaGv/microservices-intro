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
- **StatefulSets**:
    - Used for database components
- **Persistent Storage**:
    - PersistentVolume (manual)
    - PersistentVolumeClaim

## Project Structure
```
microservices-intro-master/
├── deployment/
│   ├── app/ 
│   │   ├── eureka-deployment.yml
│   │   ├── resources-ms-deployment.yml
│   │   └── songs-ms-deployment.yml
│   ├── db/
│   │   ├── resources-db-statefulset.yml
│   │   └── songs-db-statefulset.yml
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
