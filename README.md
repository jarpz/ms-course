

https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin

https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/spring.md

https://openapi-generator.tech/docs/generators/spring

- Create docker network
```
docker network create -d bridge demo-network
docker network create -d bridge --scope swarm demo-network --attachable
```

Correr mysql docker instancia
```
docker run -d --name mysql --network-alias mysql-server -p 3306:3306 --network demo-network -e MYSQL_ROOT_PASSWORD=111222 -e MYSQL_DATABASE=microservice mysql:5.7
```

Compilar fuentes:
```
cd ms-course
mvn clean install 
```

- Build image con jib
```
mvn jib:dockerBuild
```

- Build image con docker
```
cd microservice
mvn package
docker build . -t microservice:1.0-SNAPSHOT

cd config-server
mvn package
docker build . -t config-server:1.0-SNAPSHOT
```

- run config-server
```
docker run -d -p 8001:8080 --name config-server -e 'config-repo=/config-repo' -v /home/jarp/Projects/config-repo:/config-repo --network-alias config-server --network=demo-network  config-server:1.0-SNAPSHOT
```

- run microservice 
``` 
docker run -d -p 8080:8080 --name microservice -e 'spring.cloud.config.uri=http://config-server:8080' --network=demo-network  microservice:1.0-SNAPSHOT 
```
    
```
docker service create --name ms --publish published=8080,target=8080 --network demo-network -e 'spring.cloud.config.uri=http://config-server:8080' microservice:1.0-SNAPSHOT 
```

