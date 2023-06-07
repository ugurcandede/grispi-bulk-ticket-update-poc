import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.*;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class FileUtils {

    private static final PropertyAccessor properties = PropertyAccessor.getInstance();

    private static Path STORED_TICKETS_WITH_ASSIGNEE_PATH;
    private static Path STORED_TICKETS_WITHOUT_ASSIGNEE_PATH;
    private static Path STORED_TICKET_COUNTS_PATH;

    static {
        prepareFiles();
    }

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prepare directory and stored-tickets file
     */
    private static void prepareFiles() {
        final String directory = System.getProperty("user.dir");
        final String STORED_TICKETS_WITH_ASSIGNEE_FILE_NAME = properties.getProperty(PropertyEnums.STORED_TICKETS_WITH_ASSIGNEE_FILE_NAME.getValue());
        final String STORED_TICKETS_WITHOUT_ASSIGNEE_FILE_NAME = properties.getProperty(PropertyEnums.STORED_TICKETS_WITHOUT_ASSIGNEE_FILE_NAME.getValue());
        final String STORED_TICKETS_COUNT_FILE_NAME = properties.getProperty(PropertyEnums.STORED_TICKETS_COUNT_FILE_NAME.getValue());

        try {
            // create files directory if not exists
            final Path filesPath = Path.of(directory, "files");
            if (Files.notExists(filesPath)) {
                Files.createDirectory(filesPath);
            }

            // create stored-tickets file in files directory if not exists
            final Path storedTicketsWithAssigneeFilePath = Path.of(directory, "files", STORED_TICKETS_WITH_ASSIGNEE_FILE_NAME);
            if (Files.notExists(storedTicketsWithAssigneeFilePath)) {
                Files.createFile(storedTicketsWithAssigneeFilePath);
            }

            // create stored-tickets file in files directory if not exists
            final Path storedTicketsWithoutAssigneeFilePath = Path.of(directory, "files", STORED_TICKETS_WITHOUT_ASSIGNEE_FILE_NAME);
            if (Files.notExists(storedTicketsWithoutAssigneeFilePath)) {
                Files.createFile(storedTicketsWithoutAssigneeFilePath);
            }

            // create stored-ticket-counts file in files directory if not exists
            final Path storedTicketCountsFilePath = Path.of(directory, "files", STORED_TICKETS_COUNT_FILE_NAME.formatted(LocalDate.now()));
            if (Files.notExists(storedTicketCountsFilePath)) {
                Files.createFile(storedTicketCountsFilePath);
            }

            STORED_TICKET_COUNTS_PATH = storedTicketCountsFilePath;
            STORED_TICKETS_WITH_ASSIGNEE_PATH = storedTicketsWithAssigneeFilePath;
            STORED_TICKETS_WITHOUT_ASSIGNEE_PATH = storedTicketsWithoutAssigneeFilePath;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store ticket keys to file
     *
     * @param content ticket keys
     */
    public static void storeTicketKeys(String content, boolean isWithAssignee) throws IOException {
        if (content.isBlank()) {
            return;
        }

        try {
            if (isWithAssignee) {
                Files.write(STORED_TICKETS_WITH_ASSIGNEE_PATH, content.getBytes(), StandardOpenOption.APPEND);
            } else {
                Files.write(STORED_TICKETS_WITHOUT_ASSIGNEE_PATH, content.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Store ticket keys to file
     *
     * @param filterId
     * @param totalCount
     */
    public static void storeTicketsCount(String filterId, int totalCount) {

        try {
            Files.write(STORED_TICKET_COUNTS_PATH, ("FilterId: " + filterId + " Count: " + totalCount + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated(forRemoval = true)
    public static void removeSuccessTickets(String ticketKeys) throws IOException {
        final String charset = "UTF-8";
        final File file = new File(STORED_TICKETS_WITH_ASSIGNEE_PATH.toString());

        File temp = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            temp = File.createTempFile("file", ".tmp", file.getParentFile());
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(temp), charset));

            for (String line; (line = reader.readLine()) != null; ) {
                line = line.replace(ticketKeys, "");
                writer.println(line);
            }
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            reader.close();
            writer.close();

            file.delete();
            temp.renameTo(file);
        }
    }

    /**
     * Read stored-tickets file and return all ticket keys
     */
    public static Set<String> getTicketKeys(boolean isWithAssignee) {
        final String SEPARATOR = properties.getProperty(PropertyEnums.SEPARATOR.getValue());
        final Set<String> ticketKeysSet = new HashSet<>();

        try {
            final File myObj;
            if (isWithAssignee) {
                myObj = new File(STORED_TICKETS_WITH_ASSIGNEE_PATH.toString());
            } else {
                myObj = new File(STORED_TICKETS_WITHOUT_ASSIGNEE_PATH.toString());
            }
            final Scanner myReader = new Scanner(myObj);
            while (myReader.hasNext()) {
                String data = myReader.nextLine();
                Collections.addAll(ticketKeysSet, data.split(SEPARATOR));
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            Logger.error("An error occurred while reading tickets file.");
            e.printStackTrace();
        }
        return ticketKeysSet;
    }

    public static void deleteStoredFiles() {
        try {
            Files.deleteIfExists(STORED_TICKETS_WITH_ASSIGNEE_PATH);
            Files.deleteIfExists(STORED_TICKETS_WITHOUT_ASSIGNEE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> splitTicketKeys(String... ticketKeys) {
        final List<String> splitTicketKeys = new ArrayList<>();
        final String SEPARATOR = properties.getProperty(PropertyEnums.SEPARATOR.getValue());
        for (String ticketArg : ticketKeys) {
            String[] values = ticketArg.split(SEPARATOR);
            for (String value : values) {
                if (!value.isBlank()) {
                    splitTicketKeys.add(value);
                }
            }
        }
        return splitTicketKeys;
    }

    public static List<String> splitFilterKeys(String filterKeys) {
        final String SEPARATOR = properties.getProperty(PropertyEnums.SEPARATOR.getValue());
        return Arrays.asList(filterKeys.split(SEPARATOR));
    }
}
