package ship.code;

import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class TextGenerator {
    private PrintWriter printWriter;
    private long totalSize;
    private Random random;

    public static void main(String[] args) throws FileNotFoundException {
        new TextGenerator().run();
    }

    private void run() throws FileNotFoundException {
        File file = new File("dummy3.txt");
        printWriter = new PrintWriter(file);
        random = new Random();
        String line;
        while(totalSize < 5e8) {
            if(totalSize % 1000 == 0){
                System.out.println((totalSize / 5e8) * 100);
            }
            line = generateString("find it");
            totalSize += line.length();
            printWriter.write(line);
            printWriter.flush();
        }
        printWriter.close();
        System.out.println(file.length() + " " + totalSize + " " + Integer.MAX_VALUE);
    }

    private String generateString(String text){
        int length = random.nextInt(2000) + 1000;
        return RandomStringUtils.randomAscii(length / 2) + " " + text + " "
                + RandomStringUtils.randomAscii(length / 2) + "\n";
    }
}
