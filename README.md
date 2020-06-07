#Pool Game
## Requirements
### Client
In order to build the client application you'll need:  

* JDK11  

To run the client you can simply run the JAR after building it. For this you'll need Java 11+.  

The client attempts to connect to the API, which by default is located at 'https://sem.timanema.net/'.
You can override this location by setting the environment variable 'SERVER\_LOCATION'

### Server
In order to build and/or test the server you'll need:

* Docker 19.03+ (older versions may work, but not tested)
* JDK11

To run the server you can simply start a container with the server image. For this you'll also need Docker.

The server has several customization options, which can be found under the running instructions of the readme.

Even though the server can be run stand-alone, it is meant to be behind a load balancer and/or reverse proxy.
Directly publicly exposing the server is not recommended.

## Instructions
*Note: All these command expect that you are in the root dir*
### General instructions
*Note: The server relies on docker for its tests and builds,
so executing the below commands will not work if you do not have docker installed.
You can either install docker, or choose to not run server-related commands*  


Pre-push commands:
```bash
# You should do this for every module you've worked on to ensure all your
# code isn't breaking any rules.
# Replace <module> by whatever module you worked on (client/shared/server)

$ ./gradlew clean :<module>:shadowJar :<module>:check
$ <commit & push iff succesfull>
```

Generating aggregated test report:
```bash
# The report will be generated under /build/reports/tests
$ ./gradlew clean testReport
```

Generating aggregated test coverage report:
```bash
# The report will be generated under /build/jacoco/html
$ ./gradlew clean coverageReport
```

Generating mutation testing report:
```bash
# Unfortunately these can't be easily aggregated so you'll have to
# manually check the report for each module.  

# The report will be generated under /<module>/build/reports/pitest
$ ./gradlew clean pitest
```

---

### Build instructions
Server:
```bash
$ docker build -t sem-pool-server .
```


Client:
```bash
$ ./gradlew clean :client:shadowJar
```

---

### Run instructions
*Warning: For the first prototype there should be an envvar for client where the server can be found: SERVER\_LOCATION  
In most cases localhost suffices, but for some things (like toolbox) this should be something else.*  

Server:
```bash
$ docker run --env MYSQL_PASS=<password> --env JWT_SECRET=<secret> [--env VAR=val] -p 8080:8080 sem-pool-server
```

Possible environment variables:  

| Variable | Description | Optional |  
| --- | --- |  --- |   
| MYSQL\_PASS | Password to use when connecting with the MySQL database |  false |  
| MYSQL\_USER | User to use when connection with the MySQL database | true |  
| MYSQL\_URL | Location of the database | true |  
| MYSQL\_DATABASE | Database to use | true |  
| JWT\_SECRET | Secret used for signing tokens | false |  
| DATABASE | Select the database to use (SQL) | true
| CACHE | Select the database to use (MEMORY) | true

Client:

You can also run the runClient.sh if you are on mac
```bash
$ cd client/build/libs
$ java -jar client-*.jar
```
