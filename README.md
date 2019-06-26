## Docker Builder 

Create docker image builder 

docker build -t chabir/web-api-builder -f Dockerfile_build .

docker build -t chabir/web-api-builder:latest --cache-from chabir/web-api-builder:latest -f Dockerfile_build .

docker run -it --rm -v $PWD/target:/build/web-api-jersey/target chabir/web-api-builder mvn test

## Docker builder multistage

docker build --build-arg=framework_select=jersey --no-cache -t chabir/web-api-jersey:latest -f Dockerfile .

docker run --rm -p 8008:8008 -d chabir/web-api-jersey:latest 

## Call Api

Invoke Options

````
curl -X OPTIONS http://localhost:8008/api/v1/persons/ -v
````

Invoke Get

````
curl -X GET http://localhost:8008/api/v1/persons/1 -v
````

Invoke Post create person

````
curl -X POST -d '{"personId":"1"}' -H "Content-Type: application/json"  http://localhost:8008/api/v1/persons/ -v
````

Invoke Patch update person


```
curl -X PATCH -d '{"name":"chabir"}' -H "Content-Type: application/json"  http://localhost:8008/api/v1/persons/1 -v
```


Invoke Post error create person

````
curl -X POST -d '{"name":"chabir", "surname":"atrahouch"}' -H "Content-Type: application/json"  http://localhost:8008/api/v1/persons/ -v
````