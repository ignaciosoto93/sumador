This project is a REST API built with Spring Boot that allows users to sum two numbers and get the result. In addition, the API keeps a history of all the operations that have been performed.

To run the application using Docker, follow these steps:

Clone the repository:
```
git clone https://github.com/your-username/sumador-api.git
```
Build the Docker image:
```
docker build -t sumador-api .
```
Run the Docker container:
```
docker-compose up
```
Database setup:
The API uses a PostgreSQL database to store the call history. To create the 'History' table, follow these steps:
```
docker exec -it my-db psql -U postgres -c "CREATE TABLE history (id SERIAL PRIMARY KEY, timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, first_value INT NOT NULL, second_value INT NOT NULL, result DOUBLE PRECISION NOT NULL, percentage DOUBLE PRECISION NOT NULL);"
```
Once the container is up and running, you can access the API at http://localhost:8080/sum.

That's it! You can now use the Sumador API and keep track of your call history.
