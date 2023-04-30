# Grispi Bulk Ticket Update - PoC

This PoC is a Java application that stores all ticket keys in the specified filter in a text file and sends an update request to the ticket keys it stores.

## How to Run
1. Clone the repository
2. Open the project in your IDE
3. Update the environment variables in `app.properties` file
4. Update filter id and request body in `Runner.java` file
5. Run the `Main.java`

## Environment Variables
`app.properties` located in `resources` folder contains the following environment variables:

```
# Authentication
TENANT_ID=xxx
API_URL=xxx
ACCESS_TOKEN=xxx

# Others
SEPARATOR=,
SLEEP_DURATION=2000
SLEEP_ENABLED=true
REQUEST_BODY={\"channel\":\"INTEGRATION\",\"fields\":[{\"key\":\"ts.status\",\"value\":\"4\"}]}
  ```
