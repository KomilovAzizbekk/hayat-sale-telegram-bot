package uz.mediasolutions.saleservicebot.utills;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class UTF8Control extends ResourceBundle.Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        try (InputStream is = loader.getResourceAsStream(resourceName)) {
            assert is != null;
            return new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
        }
    }
}
