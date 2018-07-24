package ship.code.ui.controller;

import ship.code.FormatFileNotFoundException;
import ship.code.ui.view.LogFinderFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LogFinderFrameController {
    private LogFinderFrame logFinderFrame;
    private JButton chooseDir;
    private JButton findBtn;
    private File dir = null;
    private List<Path> listFiles;

    public LogFinderFrameController() {
        initComponents();
        initListeners();
    }

    private void initListeners() {

        chooseDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose directory for searching text");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.showDialog(logFinderFrame, "Choose");
                System.out.println(fileChooser.getSelectedFile().getName());
                dir = fileChooser.getSelectedFile();
            }
        });

        findBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); // TODO сделать в многопоточности (лочит UI)
                if(dir != null && !logFinderFrame.getExtOfFileField().getText().isEmpty()){
                    try {
                        Files.find(dir.toPath(), Integer.MAX_VALUE, ((path, basicFileAttributes) -> {
                            try {
                                String extension = getExtension(path.toFile());

                                Pattern pattern = Pattern.compile(logFinderFrame.getExtOfFileField().getText());
                                return pattern.matcher(extension).find();
                            } catch (FormatFileNotFoundException exp){
                                System.err.println(exp.getMessage());
                                return false;
                            }
                        })).forEach(listFiles::add);
                        System.out.println(listFiles.size());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private static String getExtension(File file){
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf(".");
        if(lastIndex != 0 && lastIndex != -1)
            return fileName.substring(lastIndex);
        else
            throw new FormatFileNotFoundException("format file error");
    }

    private void initComponents() {
        logFinderFrame = new LogFinderFrame();
        chooseDir = logFinderFrame.getChooseDirButton();
        findBtn = logFinderFrame.getFindBtn();
        listFiles = new ArrayList<>();
    }

    public void showLogFinderFrameController(){
        logFinderFrame.setVisible(true);
    }
}
