package ds.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceUtil {
    public ResourceUtil() {
    }

    public static InputStream loadResource(String name) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return loadResource(name, cl);
    }

    public static InputStream loadResource(String name, ClassLoader cl) throws IOException {
        InputStream is = cl.getResourceAsStream(name);
        if (is == null) {
            throw new IOException("Unable to load '" + name + "' using " + cl.toString());
        } else {
            return is;
        }
    }

    public static BufferedReader loadResourceAsReader(String name) throws IOException {
        return new BufferedReader(new InputStreamReader(loadResource(name)));
    }

    public static BufferedReader loadResourceAsReader(String name, ClassLoader cl) throws IOException {
        return new BufferedReader(new InputStreamReader(loadResource(name, cl)));
    }
}
