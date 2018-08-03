package ship.code.utils;

import java.io.File;
import java.io.IOException;

public class OpenDefaultEditor {
    private enum EnumOS {windows, macos, solaris, linux, unknown}


    public static void open(File file) {

        openSystemSpecific(file.getPath());

    }

    private static void openSystemSpecific(String what) {

        EnumOS os = getOs();

        switch (os) {
            case linux:
                runCommand("kde-open", what);
                runCommand("gnome-open", what);
                runCommand("xdg-open", what);
                break;

            case macos:
                runCommand("open", what);
                break;

            case windows:
                runCommand("explorer", what);
                break;
        }

    }


    private static void runCommand(String command, String file) {
        String[] parts = new String[]{command, file};
        try {
            Runtime.getRuntime().exec(parts);

        } catch (IOException e) {
            System.err.println("Error running command." + " " + e.getMessage());
        }
    }

    private static EnumOS getOs() {

        String s = System.getProperty("os.name").toLowerCase();

        if (s.contains("win")) {
            return EnumOS.windows;
        }

        if (s.contains("mac")) {
            return EnumOS.macos;
        }

        if (s.contains("solaris")) {
            return EnumOS.solaris;
        }

        if (s.contains("sunos")) {
            return EnumOS.solaris;
        }

        if (s.contains("linux")) {
            return EnumOS.linux;
        }

        if (s.contains("unix")) {
            return EnumOS.linux;
        } else {
            return EnumOS.unknown;
        }
    }
}
