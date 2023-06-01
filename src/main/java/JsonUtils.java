import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class JsonUtils {

    public static final Long SOLVED_ID = 4L;
    public static final Long CLOSED_ID = 5L;
    static final PropertyAccessor properties = PropertyAccessor.getInstance();

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Parse content from json
     *
     * @param json
     * @return ticket keys as {@link KeysDto}
     */
    public static KeysDto parseContent(String json) {
        final String SEPARATOR = properties.getProperty(PropertyEnums.SEPARATOR.getValue());
        final StringBuilder sb = new StringBuilder();
        final JSONObject jsonObject = new JSONObject(json);
        final JSONArray contentArray = jsonObject.getJSONArray("content");

        // check if there is next page
        long totalPages = jsonObject.getLong("totalPages");
        long pageNumber = jsonObject.getLong("pageNumber");
        boolean hasNextPage = pageNumber < totalPages;

        contentArray.forEach(item -> {
            JSONObject itemObject = (JSONObject) item;
            long statusId = itemObject.getJSONObject("status").getLong("id");

            if (statusId == SOLVED_ID || statusId == CLOSED_ID) {
                return;
            }

            String key = itemObject.getString("key");
            sb.append(key);
            sb.append(SEPARATOR);
        });

        return new KeysDto(sb.toString(), hasNextPage);
    }

    public static String parseToken(String json) {
        final JSONObject jsonObject = new JSONObject(json);
        return jsonObject.getString("access_token");
    }
}
