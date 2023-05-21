import org.tinylog.Logger;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class Main {

    public static void main(String[] args) {
        // clear console
        System.out.print("\033[H\033[2J");
        System.out.flush();

        Logger.info("Application started\n");
        try (final Scanner scanner = new Scanner(System.in)) {
            Logger.info("\nEnter filter ids:");

            final String line = scanner.nextLine();
            final List<String> filterIds = FileUtils.splitTicketKeys(line);

            final Runner runner = new Runner();
            runner.run(filterIds);
        } catch (NoSuchElementException e) {
            Logger.warn("Application interrupted");
        } catch (Exception e) {
            Logger.error(e);
        }

        Logger.info("Application finished");
    }
}
