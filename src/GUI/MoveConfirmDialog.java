package GUI;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MoveConfirmDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<String> filesList;
    private JLabel proceedLabel;
    private boolean accept;

    public MoveConfirmDialog(List<String> paths, String targetDir) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        //Fill filesList
        DefaultListModel<String> listModel = new DefaultListModel<>();
        filesList.setModel(listModel);
        for (String path : paths) {
            listModel.addElement(path);
        }
        accept = false;
        proceedLabel.setText("<html>The following files have been found and will be moved to:<br><i>" +
                targetDir + "</i><br> Do you wish to proceed?");

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /** Dispose dialog window */
    private void onOK() {
        // add your code here
        accept = true;
        dispose();
    }

    /** Dispose dialog window*/
    private void onCancel() {
        accept = false;
        dispose();
    }

    public boolean showDialog() {
        this.pack();
        this.setVisible(true);
        return accept;
    }

    /** Dispose dialog window*/
    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        files.add("path 1");
        files.add("path 2");
        files.add("path 3");
        files.add("C:\\Users\\Janko\\IdeaProjects\\projectX\\resources\\Testfolder\\Nested folder\\text2.txt");
        MoveConfirmDialog dialog = new MoveConfirmDialog(files, "C:\\Users\\Janko\\IdeaProjects\\projectX\\resources");
        System.out.println("BOOLEAN ACCEPT: " + dialog.showDialog());
        System.exit(0);
    }
}
