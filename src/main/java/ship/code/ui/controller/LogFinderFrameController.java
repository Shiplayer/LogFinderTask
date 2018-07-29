package ship.code.ui.controller;

import ship.code.FormatFileNotFoundException;
import ship.code.TextOfFileView;
import ship.code.ui.view.LogFinderFrame;
import ship.code.utils.FilesMutableTreeNode;
import ship.code.utils.Observable;
import ship.code.utils.TextFinder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class LogFinderFrameController {
    private LogFinderFrame logFinderFrame;
    private JButton chooseDir;
    private JButton findBtn;
    private File dir = null;
    private List<Path> listFiles;
    private JTree tree;
    private DefaultTreeModel root;
    private ExecutorService executor;
    private List<Path> pathList;
    private FilesMutableTreeNode filesRoot;

    public LogFinderFrameController() {
        initComponents();
        initListeners();
    }

    private void initListeners() {
        logFinderFrame.getFileShowPanel().addChangeListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
            if(tabbedPane.getTabCount() > 0)
                logFinderFrame.showMenuTab();
            else
                logFinderFrame.hiddenMenuTab();
            System.out.println(tabbedPane.getName());

            JPanel panel = (JPanel) tabbedPane.getSelectedComponent();
            for(Component component : panel.getComponents()){
                System.out.println(component.getClass());
                if(component.getClass() == JScrollPane.class){
                    TextOfFileView view = (TextOfFileView) ((JScrollPane) component).getViewport().getView();
                    int index = (int) view.getNextCaretPosition();
                    view.requestFocus();
                    view.setSelectedTextColor(Color.BLUE);
                    view.select(index , index + logFinderFrame.getSearchTextField().getText().length());
                    System.out.println(view.getSelectedText());
                }
            }
        });

        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    openFile();
                }
                else System.out.println(e.getKeyCode() + " vs " + KeyEvent.VK_UNDEFINED);
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    openFile();
                }
            }
        });

        chooseDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                openFileChooser();
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
                if(dir != null && !logFinderFrame.getExtOfFileField().getText().isEmpty()) {
                    new SwingWorkerFinder(logFinderFrame.getProgressFinder()).execute();
                }
            }
        });
    }

    private void openFileChooser(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose directory for searching text");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.showDialog(logFinderFrame, "Choose");
        if(fileChooser.getSelectedFile() != null) {
            dir = fileChooser.getSelectedFile();
            logFinderFrame.getPathOfFileView().setText(fileChooser.getSelectedFile().getPath());
            filesRoot.removeAllChildren();
            ((DefaultTreeModel)tree.getModel()).reload();
        }
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
        tree.clearSelection();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        filesRoot = new FilesMutableTreeNode("filesRoot");
        this.root = new DefaultTreeModel(filesRoot);
        tree.setModel(this.root);
        logFinderFrame.getScrollTreeFiles().getViewport().add(tree);
        executor = Executors.newFixedThreadPool(20);
        pathList = Collections.synchronizedList(new ArrayList<>());
    }

    public void showLogFinderFrameController(){
        logFinderFrame.setVisible(true);
    }

    private void openFile(){
        FilesMutableTreeNode files = (FilesMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        if(files.isRoot())
            return;
        if(files.isLeaf()) {
            File file = new File(files.getPathFile().replace("filesRoot", logFinderFrame.getPathOfFileView().getText()));
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            TextOfFileView textArea = new TextOfFileView();
            textArea.setEditable(false);
            JScrollPane jScrollPane = new JScrollPane(textArea);
            jPanel.add(jScrollPane, BorderLayout.CENTER);
            try {
                textArea.readAndFind(file, logFinderFrame.getSearchTextField().getText());
                textArea.showIndexes();
                System.out.println(textArea.getCaret().isVisible());

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            logFinderFrame.getFileShowPanel().addTab(file.getName(), jPanel);
            logFinderFrame.getFileShowPanel().setSelectedComponent(jPanel);
        } else{
            tree.expandPath(tree.getSelectionPath());
        }
    }

    private synchronized void addNode(final FilesMutableTreeNode treeNode, final DefaultTreeModel finalRoot, String path) {
        TreeNode n = treeNode.addNode(path.replace(dir.toPath().toString(), "").split(File.separator), 1, null);
        //tree.setVisibleRowCount(((DefaultMutableTreeNode)finalRoot.getRoot()).getChildCount());
        if (n != null) {
            finalRoot.reload();
        }

    }

    class SwingWorkerFinder extends SwingWorker<Void, Void>{
        private JProgressBar progressBar;

        SwingWorkerFinder(JProgressBar progressBar){
            this.progressBar = progressBar;
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
        }

        @Override
        protected Void doInBackground() throws Exception {
            final DefaultTreeModel finalRoot = root;
            final FilesMutableTreeNode treeNode = (FilesMutableTreeNode) root.getRoot();
            try {
                Files.find(dir.toPath(), Integer.MAX_VALUE, ((path, basicFileAttributes) -> {
                    try {
                        String extension = getExtension(path.toFile());

                        Pattern pattern = Pattern.compile(logFinderFrame.getExtOfFileField().getText());
                        return pattern.matcher(extension).find();
                    } catch (FormatFileNotFoundException exp) {
                        //System.err.println(exp.getMessage());
                        return false;
                    }
                })).forEach(path -> executor.execute(new TextFinder(path, logFinderFrame.getSearchTextField().getText(),
                        (Observable) () -> {
                            addNode(treeNode, finalRoot, path.toString());
                        })));
            } catch (IOException | ArrayIndexOutOfBoundsException e1) {
                System.err.println("holy error");
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
            progressBar.setIndeterminate(false);
            progressBar.setVisible(false);
        }
    }
}
