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
        log(klass, "/INFO", numOfVSpace, object);
    }

    public static void error(final Class<?> klass, final Object object) {
        error(klass, 0, object);
    }

    public static void error(final Class<?> klass, final int numOfVSpace, final Object object) {
        log(klass, "/ERROR", numOfVSpace, object);
    }

    private static void log(final Class<?> klass, final String logLevel, final int numOfVSpace, final Object object) {
        StringBuilder sb = new StringBuilder();
        if (klass != null) {
            sb.append(klass.getSimpleName());
            sb.append(logLevel);
            sb.append(" ---> ");
        }
        sb.append(object);
        System.out.println(sb);
        for (int i = 0; i < numOfVSpace; i++) {
            System.out.println();
        }
    }

}
