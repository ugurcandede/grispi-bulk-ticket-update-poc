import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Created on April, 2023
 *
 * @author ugurcandede
 */
public class PropertyAccessor {

    private final Properties properties = new Properties();

    public PropertyAccessor() {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("app.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PropertyAccessor getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return properties.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    private static class LazyHolder {
        private static final PropertyAccessor INSTANCE = new PropertyAccessor();
    }

}
