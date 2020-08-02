package com.pardhumadipalli;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pardhu Madipalli
 */
@Mojo(
                name = "banwords",
                defaultPhase = LifecyclePhase.COMPILE
                )
public class BanWords extends AbstractMojo {

    @Parameter(property = "projectroot", defaultValue = ".")
    private String projectRootDirectory;
    private String bannedWordsFilePath = "bannedWords.txt";
    private List<String> bannedWords;
    private List<File> allFilesList = new ArrayList<>();

    public void execute() throws MojoFailureException {
        bannedWords = setBannedWords();
        final File rootDirectory = new File(projectRootDirectory);
        getAllFiles(rootDirectory);
        long bannedWordsFileCount=0;
        if (allFilesList.size()>0) {
        bannedWordsFileCount  = allFilesList.parallelStream()
                .filter(this::hasBannedWords)
                .count();
        }
        if (bannedWordsFileCount == 0) {
            logDebug("No banned words found");
        } else {
            throw new MojoFailureException("Banned words have been found in "+bannedWordsFileCount + " files.");
        }
    }

    public List<String> setBannedWords () {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
                .getClassLoader()
                .getResourceAsStream(this.bannedWordsFilePath)))) {
            ArrayList<String> bannedWordsList = new ArrayList();
            String bannedWord = br.readLine();
            while (bannedWord != null) {
                logDebug("Banned word: "+bannedWord);
                bannedWordsList.add(bannedWord);
                bannedWord = br.readLine();
            }
            logDebug("Total number of registered banned words are " + bannedWordsList.size());
            return bannedWordsList;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private boolean hasBannedWords(File file) {
        String lineContent;
        boolean bannedWordFound = false;
        try{
            if(!file.isDirectory() && !file.getAbsolutePath().contains("/target/")) {
                FileReader fileReader = new FileReader(file.getAbsolutePath());
                BufferedReader br = new BufferedReader(fileReader);
                int lineNumber = 1;
                lineContent = br.readLine();
                while (lineContent != null) {
                    lineContent = lineContent.toLowerCase();
                    if(bannedWords.parallelStream().anyMatch(lineContent::contains)) {
                            bannedWordFound = true;
                            getLog().error("Banned word found at line number: " +
                                    lineNumber +
                                    " File name: " +
                                    file.getAbsolutePath());
                        }
                    lineContent = br.readLine();
                    lineNumber++;
                }
            }
            return bannedWordFound;
        } catch (IOException ex) {
            getLog().error("Could not search the file "+file.getAbsolutePath()+" for banned words");
            return false;
        }
    }

    public void setProjectRootDirectory(String projectRootDirectory) {
        this.projectRootDirectory = projectRootDirectory;
    }
    public void setBannedWordsFilePath(String bannedWordsFilePath) {
        this.bannedWordsFilePath = bannedWordsFilePath;
    }

    private void logDebug(String meesage) {
        if(getLog().isDebugEnabled()) {
            getLog().debug(meesage);
        }
    }

    private void getAllFiles(File rootDirectory) throws MojoFailureException {
        if(!rootDirectory.isDirectory()) {
            throw new MojoFailureException("The given root directory path is not a directory");
        }
        File[] currentDirectoryFiles = rootDirectory.listFiles();
        for (File file : currentDirectoryFiles) {
            if(file.isDirectory()) {
                getAllFiles(file);
            } else {
                allFilesList.add(file);
            }
        }
    }
}
