package ship.code.ui.controller;

import ship.code.FormatFileNotFoundException;
import ship.code.ui.view.LogFinderFrame;
import ship.code.utils.TextFinder;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

public class LogFinderFrameController {
    private LogFinderFrame logFinderFrame;
    private JButton chooseDir;
    private JButton findBtn;
    private File dir = null;
    private List<Path> listFiles;
    private JTree tree;
    private ExecutorService executor;

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

        logFinderFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
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
                        })).forEach(path -> executor.execute(new TextFinder(path, logFinderFrame.getSearchTextField().getText())));
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
        tree = logFinderFrame.getTreeFiles();
        executor = Executors.newFixedThreadPool(20);
    }

    public void showLogFinderFrameController(){
        logFinderFrame.setVisible(true);
    }
}
