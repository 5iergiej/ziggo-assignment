# ziggo-assignment

## Database Setup
Run following command in terminal to create local (Postgres) database instance with docker:

```
docker run --name my-container -p 5432:5432 -d -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=postgres-db postgres:latest
```

Add the following to src/main/resources/application.properties file:

```
# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=update

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL15Dialect

spring.datasource.url= jdbc:postgresql://localhost:5432/postgres-db
spring.datasource.username=admin
spring.datasource.password=admin

# Enable the following to show actual error messages in the API response JSON
# server.error.include-binding-errors=always
# server.error.include-message=always
```

Make sure host, port, username and password are the same in docker and your application.properties file!

## Running the application
Requirements: 
- Java 17 (java 17.0.6-tem)
- Maven 3 (3.8.6)

```
$ mvn clean install
$ mvn spring-boot:run 
```

## Manual testing 
The following files are available for manual testing of the API
- [swagger.json](docs/openapi.json)
- [swagger.yaml](docs/openapi.yaml)
- [postman collection](docs/postman_collection.json)

Inside the project [following directory](src/main/resources/api/v1) contains files for manual testing of http calls as well





