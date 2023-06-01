import org.tinylog.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class HttpRequestUtils {

    private static final PropertyAccessor properties = PropertyAccessor.getInstance();

    private HttpRequestUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get ticket keys with given parameters
     *
     * @param filterId  remote filter id
     * @param pageSize  page size
     * @param pageIndex page index
     * @return {@link KeysDto}
     */
    public static KeysDto getTicketsRequest(String filterId, String pageSize, int pageIndex) throws URISyntaxException {
        final String API_URL = properties.getProperty(PropertyEnums.API_URL.getValue());
        final String TENANT_ID = properties.getProperty(PropertyEnums.TENANT_ID.getValue());
        final String ACCESS_TOKEN = properties.getProperty(PropertyEnums.ACCESS_TOKEN.getValue());

        URI uri;
        try {
            uri = new URI("%s/filters/%s?size=%s&page=%s".formatted(API_URL, filterId, pageSize, pageIndex));
        } catch (URISyntaxException e) {
            Logger.error("Error when parsing url {} {}", e.getMessage(), e);
            throw new URISyntaxException("[ERR]", e.getReason());
        }

        var client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .GET()
                .uri(uri)
                .header("tenantId", TENANT_ID)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                Logger.error("Error when ticket fetching: {} {}", response.statusCode(), response.body());
                throw new IOException("Error when ticket fetching");
            }

            return JsonUtils.parseContent(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send ticket request with given parameters. i.e. update ticket status
     *
     * @param ticketKey   ticket key
     * @param requestBody request body
     */
    public static void sendTicketRequest(String ticketKey, String requestBody) {
        final String API_URL = properties.getProperty(PropertyEnums.API_URL.getValue());
        final String TENANT_ID = properties.getProperty(PropertyEnums.TENANT_ID.getValue());
        final String ACCESS_TOKEN = properties.getProperty(PropertyEnums.ACCESS_TOKEN.getValue());

        URI uri;
        try {
            uri = new URI("%s/tickets/%s".formatted(API_URL, ticketKey));
        } catch (URISyntaxException e) {
            Logger.error("Error when parsing url {} {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(requestBody);
        final HttpRequest request = HttpRequest
                .newBuilder()
                .method("PATCH", bodyPublisher)
                .uri(uri)
                .header("tenantId", TENANT_ID)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                Logger.error("Error: {} {} key is {}", response.statusCode(), response.body(), ticketKey);
            }

//            FileUtils.removeSuccessTickets(ticketKey);

        } catch (IOException | InterruptedException e) {
            Logger.error("Error: {} {} key is {}", e.getMessage(), e, ticketKey);
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public static String login(String tenantId, String username, String password) throws URISyntaxException {
        final String API_URL = properties.getProperty(PropertyEnums.API_URL.getValue());

        URI uri;
        try {
            uri = new URI("%s/login".formatted(API_URL));
        } catch (URISyntaxException e) {
            Logger.error("Error when parsing url {} {}", e.getMessage(), e);
            throw new URISyntaxException("[ERR]", e.getReason());
        }

        final HttpClient client = HttpClient.newHttpClient();
        final String requestBody = "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password);
        var request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .header("tenantId", tenantId)
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(10))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                Logger.error("Error when logging {} {}", response.statusCode(), response.body());
            }

            return JsonUtils.parseToken(response.body());

        } catch (IOException | InterruptedException e) {
            Logger.error("Error when logging {} {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return null;
    }
}
