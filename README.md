# Campspot Gap Rule Take-home Programming Exercise

## Overview
I decided to implement the solution to the problem provided with a Web Service. This service is a small component of a 
larger system. The assumption would be that we want features such as the gap rule to be "plug and play" and extendable. 
The application can be deployed and scaled as a containerized microservice as well as run as a standalone process.

The API exposes two methods currently. The `POST` is the one I am officially submitting, with the `GET` being what I 
would do given this be a "real" task. The `GET` is not finished but shows my thought process of best accessing the campsites 
with their reservations from a database and not from the request. If given more time, I would implement features such 
as create campsites, create reservations, and fetch either by Id. In a microservice world, we would differentiate 
between a reservation-service and a campsite-service. We would also have a separation of concerns and different scaling 
requirements, each managing its own data source (can be different tables in the same database).

The project structure is designed for scaling and future design considerations. For example, I have a package called 
`dao`, which represents the data access objects. In a future iteration, I would implement a database for this project 
and then use the API endpoint I exposed, which is a `GET` with `startDate`, `endDate`, and `gapSize` URL parameters. This 
way, the backend stores the state of campsites instead of sending it with the request. Implementing this will be simple 
and pluggable as the interfaces are already defined, and the methods stubbed out. Since we have interfaces for these `DAO` 
objects, we can easily switch between various implementations of the database layer, i.e., maybe we start with MySQL 
and change to Mongo.

You can send requests with a larger gap size by setting the `gapSize` request parameter with a default value of `1`. This
 implementation assumes that if you have a gap size greater than `1`, the gap cannot be less than `gapSize`, i.e., with a 
 gapSize set to 3, gaps of 1 or 2 days will also be rejected.

### Approach
I approached this problem by first drawing out the reservations on a calendar from the test file. I then walked through 
the provided search dates and wrote down what the algorithm would be as I talked through it out loud, mapping directly 
with how I was thinking about it in my head.

Here are the steps to the algorithm:

    1. Pre-process the input by mapping the sorted reservations by campsiteId
        a) sorted by startDate
    2. Check the reservations for each campsite
        a) If the campsite has no reservations, add the name to the result
    3. For each reservation, check if the start and end date for that reservation passes the gap rule filter (the big `if` statement) in ReservationServiceImpl)
        a) see inline comments for specific descriptions on the rules applied
        b) stop iterating over the reservations if we are outside of reservation dates by more than the gap size
        c) add the campsite to the result list if no overlaps are found and meets the minimum gap requirements
        
The runtime for this algorithm will be `O(NM)`, where N=number of campsites and M=number of reservations, with the 
worst-case runtime being `O(N^2)` (same number of campsites as reservations). We eliminate the need for extra 
computations by sorting the reservations by date and mapping by `campsiteId`. In order to build the sorted set, the 
runtime is `O(NlogN)`. Overall the runtime is `O(NlogN + NM)`, which can be simplified to `O(NM)` since the worst case
is the condition mentioned above.

Ideally, the reservations would be cached (sorted), eliminating the need for any db lookups and processing, ultimately
reducing runtime.

Another assumption made is that the "gap rule" means that if you have a `gapSize` of `1`, there either has to be 0 or 2 
days between the reservations and the search range. If it's `2`, there either has to be 0 or 3 days in between
reservations.
 
## Build Instructions

### Maven Build
Requires Maven with this approach

This is a Spring Boot Maven project that can be built by running the command
`mvn clean install`
in the project root directory

It can be run with the command `mvn spring-boot:run` and will expose a web service on port 8080 by default. The project
doesn't need to be built explicitly prior to being run. Running `mvn spring-boot:run` will build and deploy the service
out of the box. However, to performing a build verifies that the tests pass.  

### Docker Build
This service can also be built and run via Docker/Podman. Run the following commands:
```
docker build . -t reservation-system:latest
docker run reservation-system:latest
```

Then, find the IP address of the running Docker host with the following command:`docker inspect --format '{{.NetworkSettings.IPAddress}}' <container-id>`

### Run Simple Jar file
A jar file has been provided so that no build is required for an immediate run of the application. To run the application
with the jar, run the command `java -jar reservation-system.jar` 

## How to run/test
This service exposes a swagger-ui endpoint for a user-friendly REST interface. Once the application is running, swagger 
can be accessed with `http://localhost:8080/swagger-ui.html`

Copy and paste the contents of `src/main/resources/test-case.json` into the `POST /campspot/reservations/reserve` request body 
and hit execute.

Alternatively, you can use the following curl command:
```
curl -X POST "http://localhost:8080/campspot/reservations/reserve?gapSize=1" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"search\": { \"startDate\": \"2018-06-04\", \"endDate\": \"2018-06-06\" }, \"campsites\": [ { \"id\": 1, \"name\": \"Cozy Cabin\" }, { \"id\": 2, \"name\": \"Comfy Cabin\" }, { \"id\": 3, \"name\": \"Rustic Cabin\" }, { \"id\": 4, \"name\": \"Rickety Cabin\" }, { \"id\": 5, \"name\": \"Cabin in the Woods\" } ], \"reservations\": [ {\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"}, {\"campsiteId\": 1, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-10\"}, {\"campsiteId\": 2, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-01\"}, {\"campsiteId\": 2, \"startDate\": \"2018-06-02\", \"endDate\": \"2018-06-03\"}, {\"campsiteId\": 2, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-09\"}, {\"campsiteId\": 3, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-02\"}, {\"campsiteId\": 3, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-09\"}, {\"campsiteId\": 4, \"startDate\": \"2018-06-07\", \"endDate\": \"2018-06-10\"} ]}"
```

Test cases will be executed if you run `mvn test` in the project root directory
