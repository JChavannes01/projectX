package GUI;

import main.DocumentProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Janko on 5/29/2017.
 */
public class Main extends Component {
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
        Main gui = new Main();
        JFrame frame = new JFrame("Project X");
        frame.setContentPane(gui.mainPanel);
        //Setup menubar
        gui.menuBar = new JMenuBar();
        gui.themes = new JMenu("Themes");
        gui.themes.add(new JMenuItem("Metal"));
        gui.themes.add(new JMenuItem("Nimbus"));
        gui.themes.add(new JMenuItem("CDE/Motif"));
        gui.themes.add(new JMenuItem("Windows"));
        gui.themes.add(new JMenuItem("Windows Classic"));
        gui.menuBar.add(gui.themes);
        frame.setJMenuBar(gui.menuBar);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.print("Program Shutting down...");
            System.out.println("Done!");
            gui.saveSettings();
        }));
    }

    private Main() {

        // Set spinner characteristics
        SpinnerNumberModel fileSizeModel = new SpinnerNumberModel(0, 0, null, 150);
        fileSizeSpinner.setModel(fileSizeModel);

        //Create new file chooser for search directory
        searchFC = new JFileChooser();
        searchFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        searchFC.setAcceptAllFileFilterUsed(false);

        //Create new file chooser for  target directory
        targetFC = new JFileChooser();
        targetFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        targetFC.setAcceptAllFileFilterUsed(false);

        // Set up document Processor
        documentProcessor = new DocumentProcessor(0);
        optionsPanel.setSize(400, 250);

        //Fill extensionList
        listModel = new DefaultListModel<>();
        extensionList.setModel(listModel);

        //Load Settings:
        defaultSettings();
        loadSettings();



        resetButton.setToolTipText("Reset the extensionslist to default values.");
        resetButton.addActionListener(e -> onReset());
        newButton.addActionListener(e -> onAddNewExtension());
        removeButton.addActionListener(e -> onRemove());
        selectSearchDirectoryButton.addActionListener(e -> {
            int returnVal = this.searchFC.showOpenDialog(Main.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Update UI
                File searchDir = this.searchFC.getSelectedFile();
                updateSearchDir(searchDir.toString());

                if(DEBUG)System.out.println("Opening: " + searchDir.getName() + ".");
            } else {
                if(DEBUG) System.out.println("FileChooser cancelled by user.");
            }
        });
        targetDirButton.addActionListener(e -> {
            int returnVal = this.targetFC.showOpenDialog(Main.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Update UI
                File targetDir = this.targetFC.getSelectedFile();
                updateTargetDir(targetDir.toString());
            }
        });
        moveButton.addActionListener(e -> {
            if (DEBUG)System.out.println("[Main] Movebutton pressed!");
            //Make sure filesize is consistent with Main
            documentProcessor.setMinFileSize(Integer.parseInt(fileSizeSpinner.getValue().toString()));
            documentProcessor.getAllFiles(searchFC.getCurrentDirectory().getPath());
            //TODO confirmation window
            saveSettings();
//            SelectExtensionsDialog dialog = new SelectExtensionsDialog();
//            dialog.pack();
//            dialog.setVisible(true);
//            documentProcessor.moveFiles();
        });
    }

    /** Resets the list of extensionfilters to default values*/
    private void onReset() {
        if (Main.DEBUG) System.out.println("ExtensionList reset to default values.");
        listModel.clear();
        documentProcessor.clearExtensions();
        addNewExtension(".txt");
        extensionList.setSelectedIndex(listModel.size()-1);
    }

    /** Removes an extension from the list of extensionfilters.*/
    private void onRemove() {
        int index = extensionList.getSelectedIndex();
        if (listModel.size() > 0) {
            if (Main.DEBUG) System.out.println("Removed Extension: " + listModel.get(index));
            documentProcessor.removeExtension(listModel.remove(index));
            extensionList.setSelectedIndex(listModel.size()-1);
        } else {
            if (Main.DEBUG) System.out.println("Could not remove extension: extensionlist empty");
        }
    }

    /**
     * Add a new extension to the extensionList. Accepts either a non-empty String that does not contain any a '.' symbol,
     * or a string starting with a '.' and containing no other '.' symbols.
     * Called by 'New extension button'
     */
    private void onAddNewExtension() {
        newExtensionDialog dialog = new newExtensionDialog();
        String result = dialog.showDialog();
        addNewExtension(result);
    }

    /** Add a new extension to the extensionList. Accepts either a non-empty String that does not contain any a '.' symbol,
     * or a string starting with a '.' and containing no other '.' symbols.*/
    private void addNewExtension(String result) {
        if (!result.contains(".") && !result.equals("")) {
            if (!listModel.contains("."+result)) {
                if (Main.DEBUG) System.out.printf("Item '.%s' was added to the extensionlist.\n", result);
                listModel.addElement("." + result);
                documentProcessor.addExtension("." + result);
                extensionList.setSelectedIndex(listModel.size()-1);
            } else {
                if (Main.DEBUG) System.out.printf("Item '%s' was NOT added to the extensionlist.\n", result);
            }
        } else if (result.lastIndexOf(".") == 0) {
            if (!listModel.contains(result)) {
                if (Main.DEBUG) System.out.printf("Item '%s' was added to the extensionlist.\n", result);
                listModel.addElement(result);
                documentProcessor.addExtension(result);
                extensionList.setSelectedIndex(listModel.size()-1);
            } else {
                if (Main.DEBUG) System.out.printf("Item '%s' was NOT added to the extensionlist.\n", result);
            }
        } else {
            if (Main.DEBUG) System.out.printf("Illegal input for extensionList: %s\n", result);
        }
    }

    /** Saves the current program settings to a file. (Path: "./projectX.settings"*/
    private void saveSettings() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(String.format("%-20s : %s", "targetDirectory", targetFC.getCurrentDirectory()));
            lines.add(String.format("%-20s : %s", "searchDirectory", searchFC.getCurrentDirectory()));
            lines.add(String.format("%-20s : %s", "minimumFileSize", fileSizeSpinner.getValue().toString()));
            String extensions = "";
            for (int i = 0; i < listModel.size(); i++) {
                if (i > 0) extensions += ",";
                extensions += listModel.get(i);
            }
            lines.add(String.format("%-20s : %s", "extensionList", extensions));

            Path file = Paths.get("./projectX.settings"); // path: root/<filename>
            Files.write(file, lines, Charset.forName("UTF-8"));
            if (DEBUG)System.out.println("Wrote current program settings to file, location: " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Sets all settings to default values */
    private void defaultSettings() {
        updateTargetDir("resources\\");
        updateSearchDir("resources\\");
        updateMinFileSize(0);
        onReset();
    }

    /** Loads the program settings from a file, path: .\projectX.settings */
    private void loadSettings(){
        String filename = "projectX.settings";
        try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset())) {
//            lines.forEachOrdered(System.out::println); //line -> processLine(line));
            lines.forEachOrdered(this::processLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Updates the SearchDir FC and label */
    private void updateSearchDir(String path) {
        File file = new File(path.trim());
        searchFC.setCurrentDirectory(file);
        searchDirLabel.setText(searchFC.getCurrentDirectory().getPath());
        searchDirLabel.setToolTipText(searchDirLabel.getText());
    }

    /** Updates the TargetDir FC and label */
    private void updateTargetDir(String path) {
        File file = new File(path.trim());
        targetFC.setCurrentDirectory(file.getAbsoluteFile());
        targetDirLabel.setText(targetFC.getCurrentDirectory().getPath());
        targetDirLabel.setToolTipText(targetDirLabel.getText());
        // Set dp target folder.
        documentProcessor.setTargetDir(file.toPath());
    }

    /** Processes a line of the settings file, updating the corresponding setting. */
    private void processLine(String line) {
        String[] parts = line.split(":");
        switch (parts[0].trim()) {
            case "targetDirectory":
                updateTargetDir(parts[1]);
                break;
            case "searchDirectory":
                updateSearchDir(parts[1]);
                break;
            case "minimumFileSize":
                updateMinFileSize(Integer.parseInt(parts[1].trim()));
                break;
            case "extensionList":
                System.out.println("Setting: extensionlist");
                updateExtensionList(parts[1].split(","));
                break;
            default:
                System.out.printf("Unrecognized setting %s with value %s\n", parts[0], parts[1]);
                break; //Setting not recognized, do nothing
        }

        System.out.println("Read setting: " + line);
    }

    private void updateExtensionList(String[] extensions) {
        if (Main.DEBUG) System.out.println("ExtensionList reset to default values.");
        listModel.clear();
        documentProcessor.clearExtensions();

        for (String ext : extensions) {
            addNewExtension(ext.trim());
        }
    }


    private void updateMinFileSize(int value) {
        value = (value >= 0) ? value : 0;
        fileSizeSpinner.setValue(value);
    }
}
