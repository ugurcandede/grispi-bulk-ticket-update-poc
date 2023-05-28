/**
 * Created on May, 2023
 *
 * @author ugurcandede
 */
public enum PropertyEnums {
    API_URL("API_URL"),
    ACCESS_TOKEN("ACCESS_TOKEN"),
    TENANT_ID("TENANT_ID"),
    AUTH_SEPARATOR("AUTH_SEPARATOR"),
    STORED_TICKETS_FILE_NAME("STORED_TICKETS_FILE_NAME"),
    STORED_TICKETS_COUNT_FILE_NAME("STORED_TICKETS_COUNT_FILE_NAME"),
    SEPARATOR("SEPARATOR"),
    SLEEP_DURATION("SLEEP_DURATION"),
    SLEEP_ENABLED("SLEEP_ENABLED"),
    REQUEST_BODY("REQUEST_BODY");

    private final String value;

    PropertyEnums(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
