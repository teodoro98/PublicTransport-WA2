# Web Application 2

## Spring 2022

 Group composed by: Andrea Colli-Vignarelli, Lorenzo Cravero, Roberto Torta, Teodoro Corbo
 
### Instructions
#### Requirements
- Docker Compose
    ```bash
    cd docker
    docker compose up
    ```
  
User username can be retrived from DB, while password is set to ```Pwd123456&```
#### APIs' documentation
Each microservice expose their API documentation at /swagger-ui.html
Default local scenario URLs example:
- http://localhost:8080/swagger-ui.html [Login Service]
- http://localhost:8081/swagger-ui.html [Travelers Service]
- http://localhost:8082/swagger-ui.html [Ticket Catalogue Service]
- http://localhost:8083/swagger-ui.html [Payment Service]
- http://localhost:8084/swagger-ui.html [Turnstile Service]