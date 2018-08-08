package ship.code;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextOfFileView extends JTextArea {
    private List<Integer> findTextIndex;
    private int totalChars = 0;
    private int position = 0;
    private boolean isFileLarge = false;
    private File srcFile;
    private int countLines = 20;
    private List<String[]> findLines;
    private boolean continueFind;
    private String txt;
    private String selectedLine;
    private int currPosInLine = -1;

    public TextOfFileView() {
        findTextIndex = new ArrayList<>();
        findLines = new LinkedList<>();
        txt = null;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public void readAndFind(File file, String txt) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        if (this.txt == null) {
            this.txt = txt;
        }
        if (srcFile == null)
            srcFile = file;
        String line;
        int index = 0;
        int caret;
        if (srcFile.length() < 1e7) {
            while ((line = bf.readLine()) != null) {
                caret = 0;
                while (line.indexOf(txt, caret) != -1) {
                    caret = line.indexOf(txt, caret) + 1;
                    findTextIndex.add(totalChars + caret - 1);
                }
                append(line);
                append("\n");
                totalChars += line.length() + 1;
            }
            bf.close();
        } else {
            isFileLarge = true;
            int lineIndex = 0;
            LinkedList<String> lines = new LinkedList<>();
            LinkedList<Integer> counter = new LinkedList<>();
            while ((line = bf.readLine()) != null) {
                lineIndex++;
                lines.push(line);
                if (line.contains(txt)) {
                    counter.add(lineIndex - 1 + countLines);
                }
                if (!counter.isEmpty() && counter.peek() == lineIndex) {
                    findLines.add(lines.toArray(new String[lines.size()]));
                    counter.poll();
                }
                if (lines.size() == countLines * 2) {
                    lines.pollLast();
                }
            }
            if (!counter.isEmpty())
                findLines.add(lines.toArray(new String[lines.size()]));
        }
    }

    public long getNextCaretPosition() {
        if(selectedLine != null && !selectedLine.isEmpty()){
            int newIndex = selectedLine.indexOf(txt, currPosInLine + 1);
            if(selectedLine.lastIndexOf(txt) == newIndex)
                selectedLine = null;
            int distance = newIndex - currPosInLine;
            currPosInLine = newIndex;
            return totalChars + distance;
        }
        if (!isFileLarge) {
            totalChars = findTextIndex.get(position++);
            if(position == findTextIndex.size())
                position = 0;
            return totalChars;
        } else {
            selectAll();
            replaceSelection("");
            if (position == findLines.size() - 1) {
                String totalText = String.join("\n", findLines.get(position));
                setText(totalText);
                if (totalText.indexOf(txt) == totalText.lastIndexOf(txt)) {
                    position = 0;
                    return totalText.indexOf(txt);
                } else {
                    if (!continueFind) {
                        continueFind = true;
                        totalChars = totalText.indexOf(txt);
                    } else {
                        totalChars = totalText.indexOf(txt, totalChars + 1);
                        if (totalChars == totalText.lastIndexOf(txt)) {
                            continueFind = false;
                            position = 0;
                        }
                    }
                    return totalChars;
                }
            }
            totalChars = 0;
            String[] lines = findLines.get(position++);
            if (position == findLines.size())
                position = 0;
            int index;
            currPosInLine = -1;
            for (int i = lines.length - 1; i >= 0; i--) {
                String l = lines[i];
                append(l);
                append("\n");
                if (currPosInLine == -1) {
                    if( i == countLines - 1) {
                        if ((index = l.indexOf(txt)) != l.lastIndexOf(txt) && index != -1) {
                            selectedLine = l;
                            currPosInLine = l.indexOf(txt);
                            totalChars += currPosInLine;
                        } else if(index != -1){
                            selectedLine = null;
                            totalChars += index;
                            currPosInLine = index;
                        }
                    }else
                        totalChars += l.length() + 1;
                }
            }

            return totalChars;
        }
    }
}
