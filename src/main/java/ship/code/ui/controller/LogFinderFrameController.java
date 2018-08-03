package ship.code.ui.controller;

import ship.code.FormatFileNotFoundException;
import ship.code.TextOfFileView;
import ship.code.ui.view.LogFinderFrame;
import ship.code.utils.FilesMutableTreeNode;
import ship.code.utils.OpenDefaultEditor;
import ship.code.utils.TextFinder;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class LogFinderFrameController {
    private LogFinderFrame logFinderFrame;
    private JButton chooseDir;
    private JButton findBtn;
    private File dir = null;
    private JTree tree;
    private DefaultTreeModel root;
    private ExecutorService executor;
    private FilesMutableTreeNode filesRoot;

    public LogFinderFrameController() {
        initComponents();
        initListeners();
    }

    private void initListeners() {
        JMenu menu = logFinderFrame.getMenu();
        JMenuItem selectAll = new JMenuItem("Select All");
        menu.add(selectAll);
        selectAll.setAccelerator(KeyStroke.getKeyStroke("alt A"));
        selectAll.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTextOfFileViewFromPanel((JPanel) logFinderFrame.getFileShowPanel().getSelectedComponent()).selectAll();
            }
        });
        JMenuItem next = new JMenuItem("Next");
        menu.add(next);
        next.setAccelerator(KeyStroke.getKeyStroke("control F"));
        next.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextOfFileView view = getTextOfFileViewFromPanel((JPanel) logFinderFrame.getFileShowPanel().getSelectedComponent());
                int pos = (int) view.getNextCaretPosition();
                view.select(pos, pos + logFinderFrame.getSearchTextField().getText().length());
            }
        });
        JMenuItem closeTab = new JMenuItem("Close Tab");
        menu.add(closeTab);
        closeTab.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
        closeTab.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logFinderFrame.getFileShowPanel().remove(logFinderFrame.getFileShowPanel().getSelectedComponent());
            }
        });

        JMenuItem openInEditor = new JMenuItem("Open in editor");
        menu.add(openInEditor);
        openInEditor.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextOfFileView view = getTextOfFileViewFromPanel((JPanel) logFinderFrame.getFileShowPanel().getSelectedComponent());
                try {
                    if(Desktop.isDesktopSupported()) {
                        if(Desktop.getDesktop().isSupported(Desktop.Action.EDIT))
                            Desktop.getDesktop().edit(view.getSrcFile());
                        else
                            OpenDefaultEditor.open(view.getSrcFile());
                            /*if(!System.getProperty("os.name").equalsIgnoreCase("windows")) {
                                Runtime.getRuntime().exec("nano " + view.getSrcFile().getPath());
                                System.out.println("nano");
                            }*/
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        logFinderFrame.getFileShowPanel().addChangeListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
            if (tabbedPane.getTabCount() > 0)
                logFinderFrame.showMenuTab();
            else
                logFinderFrame.hiddenMenuTab();
            System.out.println(tabbedPane.getName());

            JPanel panel = (JPanel) tabbedPane.getSelectedComponent();

            TextOfFileView view = getTextOfFileViewFromPanel(panel);
            if(view != null) {
                int index = (int) view.getNextCaretPosition();
                view.requestFocus();
                view.setSelectedTextColor(Color.BLUE);
                view.select(index, index + logFinderFrame.getSearchTextField().getText().length());
            }
            else
                System.err.println("view is null");
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
                super.mouseClicked(e);
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

    private TextOfFileView getTextOfFileViewFromPanel(JPanel panel){
        if(panel != null) {
            for (Component component : panel.getComponents()) {
                if (component.getClass() == JScrollPane.class) {
                    return  (TextOfFileView) ((JScrollPane) component).getViewport().getView();
                }
            }
        }
        return null;
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
        tree = logFinderFrame.getTreeFiles();
        tree.clearSelection();
        filesRoot = new FilesMutableTreeNode("filesRoot");
        this.root = new DefaultTreeModel(filesRoot);
        tree.setModel(this.root);
        executor = Executors.newFixedThreadPool(20);
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
        try {
            TreeNode n;
            if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                n = treeNode.addNode(path.replace(dir.toPath().toString(), "").split("\\\\"), 1, null);
            } else
                n = treeNode.addNode(path.replace(dir.toPath().toString(), "").split(File.separator), 1, null);
            //tree.setVisibleRowCount(((DefaultMutableTreeNode)finalRoot.getRoot()).getChildCount());
            if (n != null) {
                SwingUtilities.invokeLater(finalRoot::reload);
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.err.println(e.getMessage());
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

                        Pattern pattern = Pattern.compile("("+logFinderFrame.getExtOfFileField().getText()+")$");
                        return pattern.matcher(extension).find();
                    } catch (FormatFileNotFoundException exp) {
                        //System.err.println(exp.getMessage());
                        return false;
                    }
                })).forEach(path -> {
                    if(path.toFile().isFile())
                        executor.execute(new TextFinder(
                                path,
                                logFinderFrame.getSearchTextField().getText(),
                                () -> addNode(treeNode, finalRoot, path.toString())
                        ));
                });
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
