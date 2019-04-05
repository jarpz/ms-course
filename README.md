

- Create docker network
```
docker network create -d bridge demo-network
docker network create -d bridge --scope swarm demo-network --attachable #Swarm
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
cd microservice
mvn jib:dockerBuild

cd config-server
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
- tag image
```
docker tag microservice:1.0-SNAPSHOT jarpz/microservice:1.0
docker tag config-server:1.0-SNAPSHOT jarpz/config-server:1.0
```

- run config-server
```
docker run -d -p 8001:8080 --name config-server -e 'config-repo=/config-repo' -v $PWD/config-repo:/config-repo --network-alias config-server --network=demo-network jarpz/config-server:1.0
```

- run microservice 
``` 
docker run -d -p 8080:8080 --name microservice -e 'spring.cloud.config.uri=http://config-server:8080' --network=demo-network  jarpz/microservice:1.0
```

