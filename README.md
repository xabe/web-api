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