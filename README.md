# Getting Started
A microservice which serves the contents of player.csv through REST API.
The service exposes two REST endpoints:

- GET /api/players - returns the list of all players.
- GET /api/players/{playerID} - returns a single player by ID.

## Design Options:
1) Read CSV File on Each Request.
2) Read CSV Once at Application Startup and Keep in Memory.
3) Use H2 Database to Store Data Once.

### 1) Read CSV File on Each Request:
In this approach, the CSV file is read and processed each time a request is made. This involves opening and parsing the CSV file from the file system for every request.
##### Pros:
- Simplicity: The implementation is straightforward as it does not require additional setup.
##### Cons
- Performance Overhead: Reading and parsing the CSV file for each request can introduce significant performance overhead
- Concurrency Issues: If the file is large, concurrent requests might result in performance bottlenecks or potential file locking issues.
- Scalability: This approach does not scale well for high-traffic applications, as the time to read and parse the file increases with each request.

### 2) Read CSV Once at Application Startup and Keep in Memory:
In this approach, the CSV file is read and processed once when the application starts. The data is then stored in memory using data structures such as List and Map.
-   List: Used to store all players and is returned by the findAll method. This allows for efficient retrieval of a paginated subset of players.
-   Map: Used to store players indexed by their player ID. This allows for quick lookup of individual players by their ID via the findById method.
##### Pros:
- Simplicity: The implementation is straightforward as it does not require additional setup.
- Performance: Requests are fast as they access data from in-memory structures rather than reading from the file system.
##### Cons:
- Memory Usage: Requires sufficient memory to hold the entire dataset, which might be problematic for very large CSV files.
- Persistence of Changes: If handling WRITE requests, the modified data will not be persisted and will be lost after a restart. This means any changes made during runtime are not saved, and the application will revert to the data loaded at startup.
### 3) Use H2 Database to Store Data Once
In this approach, the CSV file is read once and the data is stored in an H2 in-memory and in file-based database. Requests interact with the cache and database to retrieve data.
##### Pros:
- Performance: Databases like H2 are optimized for data retrieval and can handle queries efficiently, providing faster response times compared to file-based storage.
- Data Management: Supports complex queries and transactions, which can be beneficial for more advanced use cases.
- Persistence: If using a file-based H2 database, data persists between application restarts, ensuring consistency and avoiding the need for reloading.
- Concurrency Handling: H2 handles concurrent data access and transactions efficiently, reducing potential issues with data consistency.
##### Cons:
- Additional Complexity: Requires setup and management of a database, which introduces additional complexity compared to file-based storage.

### My measurements
 - Read directly from csv on each request:
   - 19370 records:
     - get all(non parallel): 1 attempt 1113 ms, 2 attempt 879 ms, .... ~830 ms
     - get by id: 1 attempt (when get all used before) 757, 1 attempt (when get all not used before) 1028, 2 758, 3 774 ... ~754ms
   - 500K records:
       - get all(non parallel): reached max response (need to paginate)
       - get  by id: 1 attempt 24 s, 2 attempt 24 s... ~24s
   - 1M records:
        -  get all(non parallel): reached max response (need to paginate) 
        -  get  by id: 1 attempt 50 s, 2 attempt 47 s, 3 attempt ~51.4 s
    
  - Store in memory (List and Map) when app starts:
    - 19370 records:
      - get all ~60 ms
      - get by id ~5 ms, 
    - 1M records:
        - memory usage 
          - before loading:
            Total Memory: 88 MB
            Free Memory: 68 MB
            Used Memory: 19 MB
            after loading:
            Total Memory: 2854 MB
            Free Memory: 1850 MB
            Used Memory: 1003 MB
    - 500K records (paginated 20K each page):
      - get all: ~60ms (20K records)
      - get by id ~5 ms,
      - memory usage:
               Before: Total Memory: 74 MB
               Free Memory: 54 MB
               Used Memory: 19 MB
               After:
               Total Memory: 1546 MB
               Free Memory: 1010 MB
               Used Memory: 535 MB

    
  - Persistent (H2):
    - 19370 records:
      - get all ??
      - get by id ??, 
    - 1M records:
      - get all (20 k pagination) 1 attempt 425ms, 2 attempt  ~286ms ,
      - get by id: 18ms, ~5 ms
      - memory usage:
        Total Memory: 88 MB
        Free Memory: 63 MB
        Used Memory: 24 MB
        After reading csv:
        Total Memory: 2754 MB
        Free Memory: 1712 MB
        Used Memory: 1041 MB
        After Storing to db:
        After Storing to db:
        Total Memory: 3560 MB
        Free Memory: 2505 MB
        Used Memory: 1054 MB
      
    - 500K records (paginated 20K each page):
      - get all: ~60ms (20K records)
      - get by id ~5 ms,
      - memory usage:
               Before: Total Memory: 74 MB
               Free Memory: 54 MB
               Used Memory: 19 MB
               After:
               Total Memory: 1546 MB
               Free Memory: 1010 MB
               Used Memory: 535 MB

  - Store in memory (H2) when app starts:
    - 19370 records:
        - get all ~60 ms
        - get by id ~5 ms,
    - 500K records (paginated 20K each page):
        - get all: ~60ms (20K records)
        - get by id ~5 ms,
        - memory usage:
          Before: Total Memory: 74 MB
          Free Memory: 54 MB
          Used Memory: 19 MB
          After:
          Total Memory: 1546 MB
          Free Memory: 1010 MB
          Used Memory: 535 MB
    - 1M records:
      - memory usage
        Total Memory: 80 MB
        Free Memory: 57 MB
        Used Memory: 22 MB
        After reading csv:
        Total Memory: 3056 MB
        Free Memory: 2016 MB
        Used Memory: 1039 MB
        After Storing to db:
        Total Memory: 3760 MB
        Free Memory: 2306 MB
        Used Memory: 1453 MB
### Implementation Summary:
- Initially Implemented Option 2: Read CSV Once at Application Startup and Keep in Memory. This approach was chosen for its performance benefits with smaller datasets and simplicity in handling read requests.
- Subsequently Implemented Option 3: Use H2 Database. This approach was introduced to handle larger datasets more efficiently and to ensure data persistence and scalability. Additionally, the H2 database supports add and update operations, providing greater flexibility in managing data.

Current Implementation:
The application supports both design options, allowing you to choose the appropriate method via configuration.
For datasets up to 1 million records, the in-memory approach offers fast access and simplicity. For larger datasets, the H2 database provides better scalability, persistence, and support for data modifications.
- If playerHandler.isDataBase is set to true: Option 3 (Use H2 Database) is used. Also if playerHandler.reloadDatabaseOnStart is set to true, Each startup will reload data from the CSV file to the database.
- If playerHandler.isDataBase is set to false or not specified: Option 2 (Read CSV Once at Application Startup and Keep in Memory) is used.

## Next Steps!:

- Advanced Monitoring and Observability: Implement detailed metrics for request performance, error rates, and system health using tools like Prometheus, Grafana, etc.
- Add functionality to allow for data addition and updates without restarting the service. This would improve flexibility and data management capabilities.
- Testing: Develop comprehensive integration, e2e and lnp tests.
- Authorization and Roles: Implement role-based access control and authorization mechanisms to secure endpoints and manage user permissions effectively.
- Record Modification: Enable functionality for updating existing records, such as adding a death date or other modifications, to support more dynamic data management.
- Performance Optimizations: Continuously analyze and optimize performance for handling larger datasets and higher traffic volumes.
- CSV Pagination: Implement pagination for reading from CSV files. Currently, pagination is supported only at the API level.

### Reference Documentation

For further reference, please consider the following sections:
 
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.1/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.1/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

