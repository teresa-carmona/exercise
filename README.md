# Exercise for dbCLM

This solution is a **microservice** that exposes three endpoints to **read and persist NACE data** using a local database and Excel file input.

Since this is a simple exercise:
- **Eureka has not been implemented** for service autodiscovery.
- In a real microservice architecture, this service should **integrate with Eureka** for registration and discovery.

The solution can be run locally, and the **REST API is documented with Swagger**:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Provided Endpoints

### `PUT` `api/nace/save`
- Accepts an **Excel file** and persists its NACE data into a **local database**.
- Being a `PUT`:
  - **Creates** new resources if they don’t exist.
  - **Updates** existing rows if data already exists.
- **Designed to run asynchronously** since files can be large and there can be multiple requests at the same time.
- **Transactional database methods** ensure consistency under concurrent requests.

### `GET` `/nace/{id}`
- Accepts an **integer**.
- Returns the NACE record whose `"order"` field matches the input.
- Response is in **JSON** format.

### `GET` `/nace`
- Extra endpoint implemented to provide a performant solution in high load scenarios.
- Supports **pagination** using Spring Data.
- Useful for:
  - GUI display of large datasets.
  - Handling **high request loads** efficiently.

---

## Tests Included

- **Unit tests** for the controller and service layers.
- **Integration test** validating the repository's database operations.

---

## Implementation Details

- Upon startup, a **local H2 database** is created with a `NACE` table preloaded with **two sample rows**.
- An initial implementation used **OpenCSV**, but issues with the sample `.xlsx` file (line breaks in cells, no quoting) led to switching to **Apache POI** and accepting **only Excel files**.

---

## TDD Approach

- Tests were written **before implementing the main logic**.
- Early commits show failing tests expecting valid results from methods still returning `null`.  
  Example commit:  
  [Commit 15303e7](https://github.com/teresa-carmona/exercise/commit/15303e70a3e4097146574af2dddc7293c336d2b6#diff-6e8d4fac335a95474c7fdf0a6e6b22a4674f21e1f2297e74244c26085903415d)

---

## Future Improvements

- Integration with an actual **external database** instead of local H2.
- **SonarQube** integration to validate test coverage and detect potential code smells.
- **Custom exception handler** to manage different error cases appropriately.
- **More robust concurrency handling**, e.g.:
  - Replacing `@Async` with **message queues** like Kafka.
  - Use of **Spring Retry** for failed insertions.
  - **Circuit breakers** (e.g. with Resilience4j) to protect the database under high load.
- Notification system (e.g. Kafka) to inform users when the asynchronous load finishes.
- **Audit table** to track uploaded files and processed requests.
- **Load testing** to assess scalability and resilience.
