package ship.code.utils;

import java.io.*;
import java.nio.file.Path;

public class TextFinder implements Runnable {
    private File file;
    private String word;
    private Observable observable;

    public TextFinder(Path path, String word, Observable observable) {
        this.file = path.toFile();
        this.word = word;
        this.observable = observable;
    }

    @Override
    public void run() {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String line;
            while((line = bf.readLine()) != null){
                if(line.contains(word)){
                    observable.find();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
