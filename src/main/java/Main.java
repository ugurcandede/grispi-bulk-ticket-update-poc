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
        System.out.flush();

        Logger.info("Application started\n");

        // Login stuff
        try {
            final Scanner scanner = new Scanner(System.in);
            Logger.info("Choose login method:\n1) Login with credentials\n2) Login with token");
            final String loginMethod = scanner.next();

            if (loginMethod.equals("1")) {
                Login.customLogin();
            } else if (loginMethod.equals("2")) {
                Login.tokenLogin();
            } else {
                Logger.error("Invalid input");
                System.exit(1);
            }
        } catch (NoSuchElementException e) {
            Logger.error("Application interrupted");
        }

        // Runner stuff
        try (final Scanner scnFilter = new Scanner(System.in)) {
            System.out.flush();
            Logger.info("Enter filter ids:");

            final String line = scnFilter.next();
            final List<String> filterIds = FileUtils.splitFilterKeys(line);

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
