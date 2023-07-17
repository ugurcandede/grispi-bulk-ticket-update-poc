# Grispi Bulk Ticket Update - PoC

This PoC is a Java application that stores all ticket keys in the specified filter in a text file and sends an update request to the ticket keys it stores.

## How to Run
1. Clone the repository
2. Open the project in your IDE
3. Update the environment variables in `app.properties` file
4. Update filter id and request body in `Runner.java` file
5. Run the `Main.java`

## Environment Variables
`app.properties` located in `resources` folder, and it contains the following environment variables:

```
# Authentication
API_URL=xxxx
AUTH_SEPARATOR=\\$

# File
STORED_TICKETS_WITH_ASSIGNEE_FILE_NAME=stored tickets w-assignee.txt
STORED_TICKETS_WITHOUT_ASSIGNEE_FILE_NAME=stored tickets wo-assignee.txt
STORED_TICKETS_COUNT_FILE_NAME=%s - ticket counts.txt

# Others
SEPARATOR=,
SLEEP_DURATION=2000
SLEEP_ENABLED=true
REQUEST_BODY_WITH_ASSIGNEE={\"channel\":\"INTEGRATION\",\"fields\":[{\"key\":\"ts.status\",\"value\":\"4\"}]}
REQUEST_BODY_WITHOUT_ASSIGNEE={\"channel\":\"INTEGRATION\",\"fields\":[{\"key\":\"ts.status\",\"value\":\"4\"},{\"key\":\"ts.assignee\",\"value\":\"1:null\"}]}
```
