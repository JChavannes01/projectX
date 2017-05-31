package GUI;

import main.DocumentProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Janko on 5/29/2017.
 */
public class GUI extends Component {
    public static final boolean DEBUG = true;
    private JFileChooser searchFC;
    private JFileChooser targetFC;

    private JPanel mainPanel;
    private JButton selectSearchDirectoryButton;
    private JCheckBox mp4CheckBox;
    private JPanel OptionsPanel;
    private JSpinner fileSizeSpinner;
    private JLabel fileSizeLabel;
    private JLabel searchDirLabel;
    private JButton targetDirButton;
    private JButton moveButton;
    private JLabel targetDirLabel;

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
        SpinnerNumberModel fileSizeModel = new SpinnerNumberModel(0, 0, null, 150);
        fileSizeSpinner.setModel(fileSizeModel);

        //Create new file chooser for search directory
        searchFC = new JFileChooser();
        searchFC.setCurrentDirectory(new File(".\\resources\\")); //TODO REMOVE
        searchFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        searchFC.setAcceptAllFileFilterUsed(false);
        searchDirLabel.setText(searchFC.getCurrentDirectory().getPath());

        //Create new file chooser for  target directory
        targetFC = new JFileChooser();
        targetFC.setCurrentDirectory(new File(".\\resources"));
        targetFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        targetFC.setAcceptAllFileFilterUsed(false);
        targetDirLabel.setText(targetFC.getCurrentDirectory().getPath());

        // Set up document Processor
        documentProcessor = new DocumentProcessor(0, targetFC.getCurrentDirectory().toPath());
        documentProcessor.addExtension(".txt"); //TODO REMOVE


        selectSearchDirectoryButton.addActionListener(e -> {
            int returnVal = this.searchFC.showOpenDialog(GUI.this);
            if (DEBUG) {
                System.out.println("this.getSize() = " + this.getSize());
                System.out.println(mainPanel.getSize());
            }

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Update UI
                File searchDir = this.searchFC.getSelectedFile();
                searchFC.setCurrentDirectory(searchDir);
                searchDirLabel.setText(searchDir.getPath());

                if(DEBUG)System.out.println("Opening: " + searchDir.getName() + ".");
                if(DEBUG)System.out.println("Opening: " + searchFC.getCurrentDirectory().getName() + ".");

                //Make sure filesize is consisent with GUI
                documentProcessor.setMinFileSize(Integer.parseInt(fileSizeSpinner.getValue().toString()));
                documentProcessor.getAllFiles(searchFC.getCurrentDirectory().getPath());

            } else {
                if(DEBUG)System.out.println("Open command cancelled by user.");
            }
        });
        targetDirButton.addActionListener(e -> {
            int returnVal = this.targetFC.showOpenDialog(GUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Update UI
                File targetDir = this.targetFC.getSelectedFile();
                targetFC.setCurrentDirectory(targetDir);
                targetDirLabel.setText(targetDir.getPath());

                // Set dp target folder.
                documentProcessor.setTargetDir(targetDir.toPath());
            }
        });
        moveButton.addActionListener(e -> {
            if (DEBUG)System.out.println("[GUI] Movebutton pressed!");
            documentProcessor.moveFiles();
        });
    }
}
