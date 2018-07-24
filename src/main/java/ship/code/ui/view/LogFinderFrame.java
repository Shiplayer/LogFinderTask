package ship.code.ui.view;

import javax.swing.*;

public class LogFinderFrame extends JFrame{
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 800;
    private JTabbedPane fileShowPanel;
    private JTree TreeFiles;
    private JTextField pathOfFileView;
    private JButton chooseDirButton;
    private JButton findBtn;
    private JTextField searchTextField;
    private JTextField extOfFileField;
    private JPanel jPanel;

    public LogFinderFrame(){
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(jPanel);
        setLocationRelativeTo(null);
    }

    public JButton getFindBtn() {
        return findBtn;
    }

    public JTabbedPane getFileShowPanel() {
        return fileShowPanel;
    }

    public JTree getTreeFiles() {
        return TreeFiles;
    }

    public JTextField getPathOfFileView() {
        return pathOfFileView;
    }

    public JButton getChooseDirButton() {
        return chooseDirButton;
    }

    public JTextField getSearchTextField() {
        return searchTextField;
    }

    public JTextField getExtOfFileField() {
        return extOfFileField;
    }

    public JPanel getjPanel() {
        return jPanel;
    }
}
