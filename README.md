# spring-data-rest-microservices

This project demonstrates how to use spring data rest across multiple microservices with relationships using Eureka.

## Table of Contents

  * [Modules](#modules)
  * [Local Setup](#local-setup)
    * [Local API Gateway with Eureka setup](#local-api-gateway-with-eureka-setup)
  * [Cloud Setup](#cloud-setup)
    * [Eureka Service](#eureka-service)

## Modules

|Module|Description|Local URL|
|---|---|---|
|jpa-library|Shared JPA model library|N/A|
|project-microservice|Microservice that exposes the project object|http://localhost:8051/|
|ticket-microservice|Microservice that exposes the ticket object|http://localhost:8050/|
|eureka-server|Discovery service|http://localhost:8761/|

## Local Setup

For running the apps locally you do not need the eureka-server. Simply start up the project and ticket microservices using `mvn spring-boot:run` and they will find each other.

## Local API Gateway with Eureka Setup

To simulate how it will work in the cloud locally, start up the eureka-server using `mvn spring-boot:run`. Then start up the project and ticket microservices, but this time designate the cloud profile as active (this will automatically happen when pushed to the cloud) using `mvn spring-boot:run -Dspring.profiles.active=cloud`. The two microservices will now use the eureka-server to find each other.

|Module|URL|
|---|---|---|
|api-gateway|http://localhost:8052/|
|project-microservice|http://localhost:8052/projects|
|ticket-microservice|http://localhost:8052/tickets|
|eureka-server|http://localhost:8761/|

## Pivotal Cloud Foundry Setup

The applications can be deployed directly to any cloud environment. The Eureka server can be pushed as is without further configuration, but the project and ticket test microservices require a service be created for Eureka (see [below](#eureka-service)), then have that server bound to them.

Once fully configured the apps will automatically link up.

### Eureka Service

A `User Provided` service needs to be created, and it its `Credential Parameters` a variable `url` needs to be defined with the URL to the eureka server's registration point.

Both project and ticket microservices are currently looking for service called `Eureka Test`. You can change this expectation by editing their `application-cloud.yml` property files, changing the defaultZone's value.

```yml
client:
  service-url:
    defaultZone: ${vcap.services.eureka-test.credentials.url}
```
