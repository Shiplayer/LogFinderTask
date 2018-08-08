package ship.code.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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
        try (FileChannel channel = new RandomAccessFile(file, "r").getChannel()){
            Charset charset = Charset.forName("UTF-8");
            ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
            String line;
            long position = word.length();
            long readCount;
            while((readCount = channel.read(byteBuffer, position - word.length())) != -1){
                position += readCount;
                line = charset.decode((ByteBuffer)byteBuffer.flip()).toString();
                if(line.contains(word)){
                    observable.find();
                    return;
                }
                byteBuffer.clear();
            }
        } catch (IOException e) {
            System.err.println(file.getName() + " " + file.getPath());
            e.printStackTrace();
        }
    }
}
