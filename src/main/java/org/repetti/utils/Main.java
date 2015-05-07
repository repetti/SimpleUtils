package org.repetti.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;

/**
 * @author repetti
 */
public class Main {
    private static final String HELP = "?";
    private static final String JSON_HELPER = "jh";
    private static final String JH_FORMAT = "f";
    private static final String JH_NORMAL = "p";
    private static final String JH_FORMAT_SORT = "s";

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println(info(null));
        } else if (args.length == 1) {
            System.err.println(info(args[0]));
        } else if (args.length == 2 && HELP.equals(args[0])) {
            System.err.println(info(args[1]));
        } else {
            try {
                if (JSON_HELPER.equals(args[0])) {
                    jsonHelper(args);
                } else {
                    System.err.println(info(args[0]));
                }

            } catch (Exception e) {
//                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }
        System.err.println('\n');
        System.exit(0);
    }

    public static String info(String param) {
        if (param != null && !HELP.equals(param)) {
            if (JSON_HELPER.equals(param)) {
                return "Parameters for JsonHelper:" +
                        "\n\t " + JH_FORMAT + " <filename> - print formatted" +
                        "\n\t " + JH_NORMAL + " <filename> - print normalized" +
                        "\n\t " + JH_FORMAT_SORT + " <filename> - print formatted and sorted";
            } else {
                System.err.println("Command '" + param + "' not defined\n");
            }
        }
        return "Parameters:" +
                "\n\t" + JSON_HELPER + "\tJsonHelper";

    }

    private static void jsonHelper(String[] args) throws UtilsException {
        if (JH_FORMAT.equals(args[1])) {
            if (args.length != 3) {
                System.err.println("wrong number of parameters");
            }
            final File file = file(args[2]);
            if (file == null) {
                return;
            }
            JsonNode j = JsonHelper.parse(file);
            System.out.println(JsonHelper.printFormatted(j));
        } else if (JH_FORMAT_SORT.equals(args[1])) {
            if (args.length != 3) {
                System.err.println("wrong number of parameters");
            }
            final File file = file(args[2]);
            if (file == null) {
                return;
            }
            JsonNode j = JsonHelper.parse(file);
            System.out.println(JsonHelper.printFormattedSorted(j));
        } else if (JH_NORMAL.equals(args[1])) {
            if (args.length != 3) {
                System.err.println("wrong number of parameters");
            }
            final File file = file(args[2]);
            if (file == null) {
                return;
            }
            JsonNode j = JsonHelper.parse(file);
            System.out.println(JsonHelper.toPrintable(j)); //TODO test
        } else {
            System.err.println("Subcommand not found\n");
            System.err.println(info(JSON_HELPER));
        }
    }

    private static File file(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            System.err.println("File doesn't exist");
        } else if (!f.isFile()) {
            System.err.println("Is not a file");
        } else if (!f.canRead()) {
            System.err.println("Cannot read the file");
        } else {
            return f;
        }
        return null;
    }
}
