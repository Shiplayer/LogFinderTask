package ship.code.utils;

import java.io.*;
import java.nio.file.Path;

public class TextFinder implements Runnable {
    private File file;
    private String word;

    public TextFinder(Path path, String word) {
        this.file = path.toFile();
        this.word = word;
    }

    @Override
    public void run() {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String line;
            while((line = bf.readLine()) != null){
                if(line.contains(word)){
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
