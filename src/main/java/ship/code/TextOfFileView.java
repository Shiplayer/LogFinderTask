package ship.code;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextOfFileView extends JTextArea {
    private List<Long> findTextIndex;
    private long totalChars = 0;
    private int position = 0;
    private boolean isFileLarge = false;
    private File srcFile;
    private int countLines = 20;
    private List<String[]> findLines;
    private boolean continueFind;
    private String txt;

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
        int index;
        if (srcFile.length() < Integer.MAX_VALUE) {
            while ((line = bf.readLine()) != null) {
                if ((index = line.indexOf(txt)) != -1) {
                    findTextIndex.add(totalChars + index);
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

    public void showIndexes() {
        for (long value : findTextIndex) {
            System.out.println(value);
        }
    }

    public long getNextCaretPosition() {
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
                        totalChars = totalText.indexOf(txt, (int) totalChars + 1);
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
            long pos = -1;
            for (int i = lines.length - 1; i >= 0; i--) {
                String l = lines[i];
                append(l);
                append("\n");
                if (pos == -1) {
                    if ((index = l.indexOf(txt)) != -1 && i == lines.length / 2 - 1) {
                        totalChars += index;
                        pos = totalChars;
                    } else
                        totalChars += l.length() + 1;
                }
            }

            return pos;
        }
    }
}
