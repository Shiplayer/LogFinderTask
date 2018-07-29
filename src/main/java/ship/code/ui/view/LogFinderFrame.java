package ship.code.ui.view;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
    private JScrollPane scrollTreeFiles;
    private JMenuBar menuBar;

    public LogFinderFrame(){
        JScrollBar scrollBar = scrollTreeFiles.createVerticalScrollBar();
        System.out.println(scrollBar.getVisibleAmount());
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(jPanel);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Find");
        JMenuItem selectAll = new JMenuItem("Select All");
        menu.add(selectAll);
        selectAll.setAccelerator(KeyStroke.getKeyStroke("alt A"));
        selectAll.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
        JMenuItem next = new JMenuItem("Next");
        menu.add(next);
        next.setAccelerator(KeyStroke.getKeyStroke("control F"));
        next.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
        menuBar.add(menu);
        this.setJMenuBar(menuBar);
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

    public JScrollPane getScrollTreeFiles() {
        return scrollTreeFiles;
    }

    private void createUIComponents() {

    }
}
