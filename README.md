### Run
./mvnw spring-boot:run

### Usage 

curl http://localhost:8080/app/customer/12345678/balance

curl http://localhost:8080/app/customer/88888888/balance

curl -X POST "http://localhost:8080/app/customer/12345678/transfer" \
-H "Content-Type: application/json" \
-d '{"amount": 100.0, "recipientId": "88888888"}'

http://localhost:8080/h2-console/

### Design Choices

H2 database is persistent in prod and dev as required, but I kept it as
embedded, temporary database in the test profile. This is useful so that
unit tests never affect the persistent data (test transactions are rolled
back anyway, but this is another protection)

I assumed that the smallest amount a customer can transfer is 0.01 HKD.

### Comments for Implementation

data.sql contains a hack to prevent duplicate inserts
`SET MODE MySQL; INSERT IGNORE INTO [...]`
This allows H2 data to be prepopulated every time spring launches without
errors if the data already exists from a previous launch.