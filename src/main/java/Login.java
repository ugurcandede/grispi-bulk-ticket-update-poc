import org.tinylog.Logger;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created on May, 2023
 *
 * @author ugurcandede
 */
public class Login {

    private static final PropertyAccessor properties = PropertyAccessor.getInstance();

    private Login() {
    }

    public static void login() {
        try {
            final String AUTH_SEPARATOR = properties.getProperty(PropertyEnums.AUTH_SEPARATOR.getValue());

            final Scanner scanner = new Scanner(System.in);
            Logger.info("Enter credentials (tenantId$email$password):");
            final String credentials = scanner.next();

            final String[] credentialsArray = credentials.split(AUTH_SEPARATOR);
            final String tenantId = credentialsArray[0];
            final String username = credentialsArray[1];
            final String password = credentialsArray[2];

            Logger.info("You entered\nTenantId: {}\nUsername: {}\nPassword: {}", tenantId, username, password);

            Logger.info("Are you sure? (y/n)");
            final String confirmation = scanner.next();

            if (!confirmation.equals("y")) {
                Logger.info("Application finished");
                System.exit(0);
            }

            Logger.info("Logging in...");
            // send login request and get token
            final String token = HttpRequestUtils.login(tenantId, username, password);

            if (token == null) {
                Logger.error("Login failed");
                System.exit(1);
            }

            Logger.info("Login successful");

            properties.setProperties(PropertyEnums.ACCESS_TOKEN.getValue(), token);
            properties.setProperties(PropertyEnums.TENANT_ID.getValue(), tenantId);
        } catch (NoSuchElementException e) {
            Logger.warn("Application interrupted");
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
