package GUI;

import main.DocumentProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

/**
 * Created by Janko on 5/29/2017.
 */
public class GUI extends Component {
    private static final boolean DEBUG = true;

    private JFileChooser fileChooser;
    private JPanel mainPanel;
    private JButton selectSearchDirectoryButton;
    private JCheckBox mp4CheckBox;
    private JPanel OptionsPanel;
    private JSpinner fileSizeSpinner;
    private JLabel fileSizeLabel;
    private JLabel searchDirLabel;

    private DocumentProcessor documentProcessor;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Intelligent Learner");
        frame.setContentPane(new GUI().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public GUI() {
        // Set spinner characteristics
        SpinnerNumberModel fileSizeModel = new SpinnerNumberModel(100, 0, null, 150);
        fileSizeSpinner.setModel(fileSizeModel);

        //Create new file chooser for search directory
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".\\resources\\")); //TODO REMOVE
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        searchDirLabel.setText(fileChooser.getCurrentDirectory().getPath());
        documentProcessor = new DocumentProcessor(Paths.get(fileChooser.getCurrentDirectory().getPath()));


        selectSearchDirectoryButton.addActionListener(e -> {
            int returnVal = this.fileChooser.showOpenDialog(GUI.this);
            if (DEBUG) {
                System.out.println("this.getSize() = " + this.getSize());
                System.out.println(mainPanel.getSize());
            }

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File searchDir = this.fileChooser.getSelectedFile();
                fileChooser.setCurrentDirectory(searchDir);
                searchDirLabel.setText(searchDir.getPath());
                if(DEBUG)System.out.println("Opening: " + searchDir.getName() + ".");
                if(DEBUG)System.out.println("Opening: " + fileChooser.getCurrentDirectory().getName() + ".");
                documentProcessor.getAllFiles(fileChooser.getCurrentDirectory().getPath());

            } else {
                if(DEBUG)System.out.println("Open command cancelled by user.");
            }
        });
    }
}
