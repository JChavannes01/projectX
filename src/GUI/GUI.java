package GUI;

import main.DocumentProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Janko on 5/29/2017.
 */
public class GUI extends Component {
    public static final boolean DEBUG = true;
    private JFileChooser searchFC;
    private JFileChooser targetFC;
    private DefaultListModel<String> listModel;

    private JMenuBar menuBar;
    private JMenu themes;
    private JPanel mainPanel;
    private JButton selectSearchDirectoryButton;
    private JPanel optionsPanel;
    private JSpinner fileSizeSpinner;
    private JLabel fileSizeLabel;
    private JLabel searchDirLabel;
    private JButton targetDirButton;
    private JButton moveButton;
    private JLabel targetDirLabel;
    private JList extensionList;
    private JButton removeButton;
    private JButton newButton;
    private JButton resetButton;

    private DocumentProcessor documentProcessor;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Project X");
        frame.setContentPane(new GUI().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private GUI() {
        //Setup menubar
//        menuBar = new JMenuBar();
//
//        themes = new JMenu("Themes");
//        menuBar.add(themes);

        // Set spinner characteristics
        SpinnerNumberModel fileSizeModel = new SpinnerNumberModel(0, 0, null, 150);
        fileSizeSpinner.setModel(fileSizeModel);

        //Create new file chooser for search directory
        searchFC = new JFileChooser();
        searchFC.setCurrentDirectory(new File(".\\resources\\")); //TODO REMOVE
        searchFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        searchFC.setAcceptAllFileFilterUsed(false);
        searchDirLabel.setText(searchFC.getCurrentDirectory().getPath());
        searchDirLabel.setToolTipText(searchDirLabel.getText());

        //Create new file chooser for  target directory
        targetFC = new JFileChooser();
        targetFC.setCurrentDirectory(new File(".\\resources"));
        targetFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        targetFC.setAcceptAllFileFilterUsed(false);
        targetDirLabel.setText(targetFC.getCurrentDirectory().getPath());
        targetDirLabel.setToolTipText(targetDirLabel.getText());

        // Set up document Processor
        documentProcessor = new DocumentProcessor(0, targetFC.getCurrentDirectory().toPath());
        documentProcessor.addExtension(".txt"); //TODO REMOVE

        //Fill extensionList
        listModel = new DefaultListModel<>();
        extensionList.setModel(listModel);
        onReset(); // Add default extensions to extensionlist

        resetButton.setToolTipText("Reset the extensionslist to default values.");
        resetButton.addActionListener(e -> onReset());
        newButton.addActionListener(e -> addNewExtension());
        removeButton.addActionListener(e -> onRemove());
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
                searchDirLabel.setToolTipText(searchDirLabel.getText());

                if(DEBUG)System.out.println("Opening: " + searchDir.getName() + ".");
            } else {
                if(DEBUG) System.out.println("FileChooser cancelled by user.");
            }
        });
        targetDirButton.addActionListener(e -> {
            int returnVal = this.targetFC.showOpenDialog(GUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Update UI
                File targetDir = this.targetFC.getSelectedFile();
                targetFC.setCurrentDirectory(targetDir);
                targetDirLabel.setText(targetDir.getPath());
                targetDirLabel.setToolTipText(targetDirLabel.getText());

                // Set dp target folder.
                documentProcessor.setTargetDir(targetDir.toPath());
            }
        });
        moveButton.addActionListener(e -> {
            if (DEBUG)System.out.println("[GUI] Movebutton pressed!");
            //Make sure filesize is consisent with GUI
            documentProcessor.setMinFileSize(Integer.parseInt(fileSizeSpinner.getValue().toString()));
            documentProcessor.getAllFiles(searchFC.getCurrentDirectory().getPath());
//            SelectExtensionsDialog dialog = new SelectExtensionsDialog();
//            dialog.pack();
//            dialog.setVisible(true);
//            documentProcessor.moveFiles();
        });
    }

    private void onReset() {
        if (GUI.DEBUG) System.out.println("ExtensionList reset to default values.");
        listModel.clear();
        listModel.addElement(".txt");
        extensionList.setSelectedIndex(listModel.size()-1);
    }

    private void onRemove() {
        int index = extensionList.getSelectedIndex();
        if (listModel.size() > 0) {
            if (GUI.DEBUG) System.out.println("Removed Extension: " + listModel.get(index));
            listModel.remove(index);
            extensionList.setSelectedIndex(listModel.size()-1);
        } else {
            if (GUI.DEBUG) System.out.println("Could not remove extension: extensionlist empty");
        }
    }

    /**
     * Add a new extension to the extensionList. Accepts either a non-empty String that does not contain any a '.' symbol,
     * or a string starting with a '.' and containing no other '.' symbols.
     * Called by 'New extension button'
     */
    private void addNewExtension() {
        newExtensionDialog dialog = new newExtensionDialog();
        String result = dialog.showDialog();
        if (!result.contains(".") && !result.equals("")) {
            if (!listModel.contains("."+result)) {
                if (GUI.DEBUG) System.out.printf("Item '.%s' was added to the extensionlist.\n", result);
                listModel.addElement("." + result);
                extensionList.setSelectedIndex(listModel.size()-1);
            } else {
                if (GUI.DEBUG) System.out.printf("Item '%s' was NOT added to the extensionlist.\n", result);
            }
        } else if (result.lastIndexOf(".") == 0) {
            if (!listModel.contains(result)) {
                if (GUI.DEBUG) System.out.printf("Item '%s' was added to the extensionlist.\n", result);
                listModel.addElement(result);
                extensionList.setSelectedIndex(listModel.size()-1);
            } else {
                if (GUI.DEBUG) System.out.printf("Item '%s' was NOT added to the extensionlist.\n", result);
            }
        } else {
            if (GUI.DEBUG) System.out.printf("Illegal input for extensionList: %s\n", result);
        }
    }
}
