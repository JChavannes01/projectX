package main;

import GUI.GUI;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
//import sun.nio.fs

/**
 * Created by Janko on 5/30/2017.
 */
public class DocumentProcessor {
    private List<Path> files;
    private List<String> extensions;
    private Path targetDir;
    // TODO get variable values from GUI
    private int minFileSize;

    public DocumentProcessor(int minFileSize, Path targetDir) {
        setMinFileSize(minFileSize);
        this.targetDir = targetDir;
        files = new ArrayList<>();
        extensions = new ArrayList<>();
    }

    public void addExtension(String extension) {
        this.extensions.add(extension);
    }

    public void setMinFileSize(int minFileSize) {
        this.minFileSize = minFileSize;
    }

    public void setTargetDir(Path targetDir) {
        this.targetDir = targetDir;
    }

    public void moveFiles() {
        if (GUI.DEBUG) System.out.printf("Attempting to move %d file(s) to %s", files.size(), targetDir);
        for (Path path : files) {
            try {
//                Files.move(path, targetDir);
                Files.copy(path, targetDir.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a list of all files in a directory and all subdirectories, that match search criteria.
     * @param path Path to the directory.
     * @return
     */
    public List<Path> getAllFiles(String path) {
        Path searchDir = Paths.get(path);
        files = getFileNames(new ArrayList<>(), searchDir, minFileSize, extensions);
        return files;
    }

    /**
     * Lists all files that match search criteria (Extensions and size), and recursively searches for files in subdirectory
     * @param fileNames List that keeps track of the files
     * @param dir Current directory that is being searched
     * @param minFileSize Minimum size of the file to be added.
     * @param extensions File extensions that will be included.
     * @return
     */
    private List<Path> getFileNames(List<Path> fileNames, Path dir, int minFileSize, List<String> extensions) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getFileNames(fileNames, path, minFileSize, extensions);
                } else {
                    if (checkConstraints(path, minFileSize, extensions)) {
                        fileNames.add(path);
                        if (GUI.DEBUG)System.out.println(path.getFileName());
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    /**
     * Checks whether a given file matches the given searchcriteria.
     * @param path Path to the file
     * @param minFileSize Minimum size of the file.
     * @param extensions List of accepted extensions.
     * @return False if constraints are violated, True otherwise.
     */
    private boolean checkConstraints(Path path, int minFileSize, List<String> extensions) {
        String name = path.getFileName().toString();
        String extension = name.substring(name.lastIndexOf("."));

        if (!extensions.contains(extension)) {
            return false;
        } else {
            int fileSize = Math.toIntExact(path.toFile().length() / 1000000);
            return (fileSize >= minFileSize);
        }
    }
}
