package ds.util;

import java.util.Random;

public class RandUtil {
    private static Random random = new Random();

    public RandUtil() {
    }

    public static boolean trueOrFalse() {
        return random.nextBoolean();
    }

    public static int getInt(int max) {
        return random.nextInt(max) + 1;
    }

    public static int getFromRange(int min, int max) {
        return random.nextInt(max) + 1;
    }

}
