package GUI;

import javax.swing.*;
import java.awt.event.*;

public class newExtensionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonAdd;
    private JButton buttonBack;
    private JTextField extensionTextField;

    public newExtensionDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonAdd);

        buttonAdd.addActionListener(e -> onAdd());

        buttonBack.addActionListener(e -> onBack());

        // call onBack() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onBack();
            }
        });

        // call onBack() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onBack();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public String showDialog() {
        this.pack();
        this.setVisible(true);
        return extensionTextField.getText();
    }

    private void onAdd() {
        // add your code here
        this.setVisible(false);
        dispose();
    }

    private void onBack() {
        // add your code here if necessary
        extensionTextField.setText("");
        this.setVisible(false);
        dispose();
    }

    public static void main(String[] args) {
        newExtensionDialog dialog = new newExtensionDialog();
        String result = dialog.showDialog();
        if (result.equals("")) {
            System.out.println("no result");
        } else if (!result.contains(".")) {
            System.out.println("extensionTextField = ." + result);
        } else if (result.lastIndexOf(".") == 0) {
            System.out.println("result = " + result);
        }
        System.exit(0);
    }
}
