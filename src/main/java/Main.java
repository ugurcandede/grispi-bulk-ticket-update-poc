import org.tinylog.Logger;

import java.util.List;
import java.util.Scanner;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class Main {

    public static void main(String[] args) {
        Logger.info("Application started");
        final Scanner scanner = new Scanner(System.in);

        // clear console
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("\nEnter filter ids:");

        final String line = scanner.nextLine();
        final List<String> filterIds = FileUtils.splitTicketKeys(line);

        final Runner runner = new Runner();
        runner.run(filterIds);
    }
}
