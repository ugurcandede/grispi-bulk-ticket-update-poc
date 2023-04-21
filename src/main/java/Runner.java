import org.tinylog.Logger;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class Runner {

    private static final PropertyAccessor properties = PropertyAccessor.getInstance();
    private static final Long SLEEP_INDEX = Long.parseLong(properties.getProperty("SLEEP_INDEX"));
    private static final Long SLEEP_TIME = Long.parseLong(properties.getProperty("SLEEP_TIME"));

    private static final String FILTER_ID = "1";
    private static final String PAGE_SIZE = "50";
    private static final String REQUEST_BODY = "{\"channel\":\"INTEGRATION\",\"fields\":[{\"key\":\"ts.status\",\"value\":\"4\"}]}";

    public void run() {
        try {
            getAllTicketKeys();
            solveTickets();
        } catch (Exception e) {
            Logger.error(e);
            e.printStackTrace();
        }
    }

    /**
     * Get all ticket keys from api and store them in a file
     */
    private void getAllTicketKeys() {
        // get all ticket keys
        int pageIndex = 0;
        boolean hasNextPage = true;

        while (hasNextPage) {
            try {
                final KeysDto keysDto = HttpRequestUtils.getTicketsRequest(FILTER_ID, PAGE_SIZE, pageIndex);
                assert keysDto != null;
                final String keys = keysDto.keys();
                hasNextPage = keysDto.hasNextPage();

                // store ticket keys
                FileUtils.storeTicketKeys(keys);

                // sleep thread
                if (pageIndex % SLEEP_INDEX == 0) {
                    Logger.info("[FETCH] Thread is sleeping for {} ms", SLEEP_TIME);
                    Thread.sleep(SLEEP_TIME);
                }

                Logger.info("[FETCH] Page index: {}", pageIndex);

                pageIndex++;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Logger.info("[INFO] Tickets are fetched");
    }

    /**
     * Solve tickets from stored file with given request body
     */
    private void solveTickets() {
        int index = 0;
        final String[] ticketKeys = FileUtils.getTicketKeys();

        for (String ticketKey : ticketKeys) {
            try {
                if (ticketKey.isBlank()) {
                    Logger.info("There is no ticket found");
                    continue;
                }
                HttpRequestUtils.sendTicketRequest(ticketKey, REQUEST_BODY);

                // sleep thread
                if (index % SLEEP_INDEX == 0) {
                    Logger.info("[SOLVE] Thread is sleeping for {} ms", SLEEP_TIME);
                    Thread.sleep(SLEEP_TIME);
                }

                Logger.info("[SOLVE] Solved ticket wih key: {}", ticketKey);

                index++;
            } catch (Exception e) {
                Logger.warn(e);
            }
        }
        Logger.info("[INFO] All tickets are solved");
    }
}
