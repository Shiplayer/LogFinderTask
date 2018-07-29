package ship.code;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextOfFileView extends JTextArea {
    private List<Long> findTextIndex;
    private long totalChars = 0;
    private int position = 0;

    public TextOfFileView(){
        findTextIndex = new ArrayList<>();
    }

    public void readAndFind(File file, String txt) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        int index;
        while((line = bf.readLine()) != null){
            if ((index = line.indexOf(txt)) != -1) {
                findTextIndex.add(totalChars + index);
            }
            append(line);
            append("\n");
            totalChars += line.length() + 1;
        }
    }

    public void showIndexes(){
        for(long value : findTextIndex){
            System.out.println(value);
        }
    }

    public long getNextCaretPosition(){
        long pos = findTextIndex.get(position);
        if(++position >= findTextIndex.size()){
            position = position % findTextIndex.size();
        }
        return pos;
    }
}
