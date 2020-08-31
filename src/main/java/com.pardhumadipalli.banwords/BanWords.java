package com.pardhumadipalli.banwords;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The plugin can be used to help check discriminatory words in the project.
 *
 * @author Pardhu Madipalli
 * @since v0.1.0
 */
@Mojo(
                name = "banwords",
                defaultPhase = LifecyclePhase.COMPILE
                )
public class BanWords extends AbstractMojo {

    @Parameter(property = "projectroot", defaultValue = ".")
    private String projectRootDirectory;

    @Parameter(property = "includes")
    private List<String> additionalWords;

    @Parameter(property = "excludes")
    private List<String> excludedwords;

    private String bannedWordsFilePath = "bannedWords.txt";
    private List<String> bannedWords;
    private List<File> allFilesList = new ArrayList<>();

    /**
     * Check each file and see whether any banned words are found.
     * <p>
     *     The exception message contains how many files contain banned words.
     *
     * @throws MojoFailureException
     */
    @Override
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

    /**
     * Reads the banned words file and puts them into a list of strings.
     * <p>
     *     The banned word should not be found in excluded words list.
     *     New banned words provided by user from inluded words have to be add to the final list.
     *     If a single word is present in both exluded and included lists, then inclusion list takes preference.
     *     The generated list will be used in the execute method to check if any item in the list exists.
     *
     * @return List of banned words
     */
    public List<String> setBannedWords () {
        excludedwords = excludedwords == null ? new ArrayList<>() : excludedwords;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream(this.bannedWordsFilePath))))) {
            ArrayList<String> bannedWordsList = new ArrayList();
            String bannedWord = br.readLine();

            // Verify whether the word is in the exluded list or not
            while (bannedWord != null && !excludedwords.contains(bannedWord)) {
                logDebug("Banned word: "+bannedWord);
                bannedWordsList.add(bannedWord);
                bannedWord = br.readLine();
            }

            // Add aditional words chosen by the user
            addAdditionalWords(bannedWordsList);

            return bannedWordsList;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Given a file object, it reads the file to see if any of the banned words exists.
     * <p>
     *     If any banned word is found,
     *     it prints the line number and file name where the banned word is found in the log message.
     * </p>
     * @param file
     * @return True if the file contains banned words
     */
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

    /**
     * Set the project root directory.
     * <p>
     *     All the files in the directory structure will be scanned for banned words.
     *     The method is intended to use only for testing.
     *     This method is not used in actual implementation of the plugin.
     * </p>
     * @param projectRootDirectory Path of the root directory
     */
    public void setProjectRootDirectory(String projectRootDirectory) {
        this.projectRootDirectory = projectRootDirectory;
    }

    /**
     * Set the file path of list of banned words.
     * <p>
     *     The list of words found in this file will be used to scan the files.
     *     The method is intended to use only for testing.
     *     This method is not used in actual implementation of the plugin.
     * </p>
     * @param bannedWordsFilePath banned words file path
     */
    public void setBannedWordsFilePath(String bannedWordsFilePath) {
        this.bannedWordsFilePath = bannedWordsFilePath;
    }

    /**
     * Set the additional banwords words that are not there in the default banwords.txt file.
     * <p>
     *     The method is intended to use only for testing.
     *     This method is not used in actual implementation of the plugin.
     * </p>
     * @param additionalWords List of additional banned words
     */
    public void setAdditionalWords(List<String> additionalWords) {
        this.additionalWords = additionalWords;
    }

    public void setExcludedwords(List<String> excludedwords) {
        this.excludedwords = excludedwords;
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

    private void addAdditionalWords(ArrayList<String> bannedWordsList) {
        if(additionalWords != null){
            logDebug("Number of additional words chosen as banned words by user: " +
                    (additionalWords == null ? 0 : additionalWords.size()));
            bannedWordsList.addAll(additionalWords);
            logDebug("Total number of registered banned words are " + bannedWordsList.size());
        }
    }
}
