package ship.code.utils;

import java.io.File;
import java.io.IOException;

public class OpenDefaultEditor {
    private enum EnumOS {windows, macos, solaris, linux, unknown}


    public static boolean open(File file) {

        if (openSystemSpecific(file.getPath())) return true;

        return false;
    }

    private static boolean openSystemSpecific(String what) {

        EnumOS os = getOs();

        if (os == EnumOS.linux) {
            if (runCommand("kde-open", what)) return true;
            if (runCommand("gnome-open", what)) return true;
            if (runCommand("xdg-open", what)) return true;
        }

        if (os == EnumOS.macos) {
            if (runCommand("open", what)) return true;
        }

        if (os == EnumOS.windows) {
            if (runCommand("explorer", what)) return true;
        }

        return false;
    }


    private static boolean runCommand(String command, String file) {
        String[] parts = new String[]{command, file};
        try {
            Process p = Runtime.getRuntime().exec(parts);
            return p != null;

        } catch (IOException e) {
            System.err.println("Error running command." + " " + e.getMessage());
            return false;
        }
    }

    public static EnumOS getOs() {

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
