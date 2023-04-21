import org.tinylog.Logger;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class Main {

    public static void main(String[] args) {
        Logger.info("Application started");
        Runner runner = new Runner();
        runner.run();
    }
}
