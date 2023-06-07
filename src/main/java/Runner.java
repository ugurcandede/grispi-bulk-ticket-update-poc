import org.tinylog.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class Runner {

    private static final PropertyAccessor properties = PropertyAccessor.getInstance();

    private static final String PAGE_SIZE = "50";

    public void run(List<String> filterIds) {
        final long SLEEP_DURATION = Long.parseLong(properties.getProperty(PropertyEnums.SLEEP_DURATION.getValue()));
        try {
            // add filter ids to list
            filterIds.parallelStream().forEach(this::getAllTicketKeys);
            Thread.sleep(SLEEP_DURATION * 3);
            solveTickets();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    /**
     * Get all ticket keys from api and store them in a file
     */
    private void getAllTicketKeys(String filterId) {
        // get all ticket keys
        int pageIndex = 0;
        boolean hasNextPage = true;
        final AtomicInteger ticketCounter = new AtomicInteger(0);

        while (hasNextPage) {
            final KeysDto keysDto;

            try {
                keysDto = HttpRequestUtils.getTicketsRequest(filterId, PAGE_SIZE, pageIndex);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            assert keysDto != null;
            final String keysWithAssignee = keysDto.keysWithAssignee();
            final String keysWithoutAssignee = keysDto.keysWithoutAssignee();
            hasNextPage = keysDto.hasNextPage();

            // store ticket keys
            try {
                FileUtils.storeTicketKeys(keysWithAssignee, true);
                FileUtils.storeTicketKeys(keysWithoutAssignee, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            final List<String> ticketKeys = FileUtils.splitTicketKeys(keysWithAssignee, keysWithoutAssignee);
            ticketCounter.addAndGet(ticketKeys.size());

            Logger.info("[FETCH] Page index: {} and filterId is '{}'", pageIndex, filterId);
            if (hasNextPage) pageIndex++;
        }

        // store ticket count with filterId
        FileUtils.storeTicketsCount(filterId, ticketCounter.get());
        Logger.info("[INFO] Tickets are fetched for filterId: '{}'", filterId);
    }

    /**
     * Solve tickets from stored file with given request body
     */
    private void solveTickets() {
        final String REQUEST_BODY_WITH_ASSIGNEE = properties.getProperty(PropertyEnums.REQUEST_BODY_WITH_ASSIGNEE.getValue());
        final String REQUEST_BODY_WITHOUT_ASSIGNEE = properties.getProperty(PropertyEnums.REQUEST_BODY_WITHOUT_ASSIGNEE.getValue());

        // get all ticket keys from stored file
        closeTicketAssignee(true, REQUEST_BODY_WITH_ASSIGNEE);
        sleeper();
        closeTicketAssignee(false, REQUEST_BODY_WITHOUT_ASSIGNEE);

        // delete stored files
        FileUtils.deleteStoredFiles();
        Logger.info("[INFO] All tickets are solved");
    }

    private void closeTicketAssignee(boolean isWithAssignee, String requestBody) {

        final List<String> ticketKeys = Collections.synchronizedList(new ArrayList<>(FileUtils.getTicketKeys(isWithAssignee)));
        Logger.info("[INFO] Total ticket {} size: {}", isWithAssignee ? "with assignee" : "without assignee", ticketKeys.size());

        // divide ticket keys into blocks
        final List<List<String>> blocks = new ArrayList<>();
        int blockSize = 20;
        for (int i = 0; i < ticketKeys.size(); i += blockSize) {
            int end = Math.min(ticketKeys.size(), i + blockSize);
            blocks.add(ticketKeys.subList(i, end));
        }

        // each block is solved in parallel
        blocks.stream().parallel().forEach(block -> {
            final CopyOnWriteArrayList<String> writeArrayList = new CopyOnWriteArrayList<>(block);

            sleeper();

            // solve each ticket in block
            writeArrayList.forEach(ticketKey -> {
                try {
                    if (ticketKey.isBlank()) {
                        Logger.info("There is no ticket found");
                        return;
                    }

                    HttpRequestUtils.sendTicketRequest(ticketKey, requestBody);
                    writeArrayList.remove(ticketKey);

                    Logger.info("[SOLVE] Solved ticket with key: {}", ticketKey);
                } catch (Exception e) {
                    Logger.warn(e);
                }
            });
        });
    }

    private void sleeper() {
        final long SLEEP_DURATION = Long.parseLong(properties.getProperty(PropertyEnums.SLEEP_DURATION.getValue()));
        final boolean SLEEP_ENABLED = Boolean.parseBoolean(properties.getProperty(PropertyEnums.SLEEP_ENABLED.getValue()));
        if (SLEEP_ENABLED) {
            try {
                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
