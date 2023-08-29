# ECOMIE API 

This repo contains the api for the ecomie application.

## SETUP

### DOCKER
- Create a copy of the .env.example file and rename it to .env
- Fill the required environmental variables
- Position yourself into the root directory
- Run ___docker-compose up___ or ___docker compose up___. You can prepend sudo to the command if need be
- Open the browser and enter ___localhost:8081/swagger-ui/index.html___

### Without Docker
- Create a copy of the .env.example file and rename it to .env
- Comment docker postgres variables
- Uncomment either mysql or postgres variable depending on what you want to use
- Fill the required environmental variables
- Position yourself into the root directory
- Run ___mvn clean install -DskipTests___ to compile code and ___java -jar target/ecomie.jar___ to run compiled code


## Branches
- ___staging:___ recent branch
- ___master:___ authentication