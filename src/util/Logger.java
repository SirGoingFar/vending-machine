package util;


public class Logger {

    public static void info(final Object object) {
        info(null, 0, object);
    }

    public static void info(final int numOfVSpace, final Object object) {
        info(null, numOfVSpace, object);
    }

    public static void info(final Class<?> klass, final Object object) {
        info(klass, 0, object);
    }

    public static void info(final Class<?> klass, final int numOfVSpace, final Object object) {
        StringBuilder sb = new StringBuilder();
        if (klass != null) {
            sb.append(klass.getSimpleName()).append("/I ---> ");
        }
        sb.append(object);
        System.out.println(sb);
        for (int i = 0; i < numOfVSpace; i++) {
            System.out.println();
        }
    }

}
