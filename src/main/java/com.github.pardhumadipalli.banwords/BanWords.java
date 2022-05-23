package com.github.pardhumadipalli.banwords;

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
                name = Constants.PLUGIN_NAME,
                defaultPhase = LifecyclePhase.COMPILE
                )
public class BanWords extends AbstractMojo {

    @Parameter(property = "projectroot", defaultValue = ".")
    private String projectRootDirectory;

    @Parameter(property = "includeWords")
    private List<String> includeWords;

    @Parameter(property = "excludeWords")
    private List<String> excludeWords;

    private String bannedWordsFilePath = Constants.DEFAULT_BANNED_WORDS_FILE_NAME;

    private List<String> bannedWords;
    private final List<File> allFilesList = new ArrayList<>();

    /**
     * Check each file and see whether any banned words are found.
     * <p>
     *     The exception message contains how many files contain banned words.
     * </p>
     * @throws MojoFailureException When banned words are found in the project.
     */
    @Override
    public void execute() throws MojoFailureException {
        if (System.getProperty(Constants.SKIP_EXECUTION_PROPERTY) != null &&
                "true".equals(System.getProperty(Constants.SKIP_EXECUTION_PROPERTY).toLowerCase())) {
            getLog().info(String.format("Skipping executing %s plugin.", Constants.PLUGIN_NAME));
            return;
        }
        bannedWords = setBannedWords();
        getValidFiles(new File(projectRootDirectory));
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
     * </p>
     * @return List of banned words
     */
    List<String> setBannedWords () {
        excludeWords = excludeWords == null ? new ArrayList<>() : excludeWords;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream(this.bannedWordsFilePath))))) {
            ArrayList<String> bannedWordsList = new ArrayList<>();
            String bannedWord = br.readLine();
            // Verify whether the word is in the excluded list or not
            while (bannedWord != null && !excludeWords.contains(bannedWord)) {
                logDebug("Banned word: "+bannedWord);
                bannedWordsList.add(bannedWord);
                bannedWord = br.readLine();
            }
            // Add additional words chosen by the user
            addAdditionalWords(bannedWordsList);
            return bannedWordsList;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Check the banned words in the given file.
     * @param file Only a readable file.
     * @return Whether the file contains a banned words or not.
     */
    private boolean hasBannedWords(File file) {
        String lineContent;
        boolean bannedWordFound = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            lineContent = br.readLine();
            for(int lineNumber = 1; lineContent != null; lineNumber ++) {
                if(bannedWords.stream().anyMatch(lineContent.toLowerCase()::contains)) {
                        bannedWordFound = true;
                        getLog().error(String.format("Banned word found at line number %d in the file %s",
                                lineNumber,
                                file.getAbsolutePath()));
                    }
                lineContent = br.readLine();
            }
            return bannedWordFound;
        } catch (IOException ex) {
            getLog().error("Could not search the file "+file.getAbsolutePath()+" for banned words");
            return false;
        }
    }

    void setProjectRootDirectory(String projectRootDirectory) {
        this.projectRootDirectory = projectRootDirectory;
    }

    void setBannedWordsFilePath(String bannedWordsFilePath) {
        this.bannedWordsFilePath = bannedWordsFilePath;
    }

    void setIncludeWords(List<String> includeWords) {
        this.includeWords = includeWords;
    }

    void setExcludeWords(List<String> excludeWords) {
        this.excludeWords = excludeWords;
    }

    private void getValidFiles(File rootDirectory) throws MojoFailureException {
        if(!rootDirectory.isDirectory()) {
            throw new MojoFailureException("The given root directory path is not a directory");
        }
        File[] currentDirectoryFiles = rootDirectory.listFiles();
        assert currentDirectoryFiles != null;
        for (File file : currentDirectoryFiles) {
            if(file.isDirectory()) {
                getValidFiles(file);
            } else if (
                    file.canRead()                                                      &&
                    !Constants.POM_FILE_NAME.equals(file.getName())                     &&
                    !file.getAbsolutePath().contains(Constants.MAVEN_TARGET_DIRECTORY)) {
                allFilesList.add(file);
            }
        }
    }

    private void addAdditionalWords(ArrayList<String> bannedWordsList) {
        if(includeWords != null){
            logDebug("Number of additional words chosen as banned words by user: " +
                    includeWords.size());
            bannedWordsList.addAll(includeWords);
            logDebug("Total number of registered banned words are " + bannedWordsList.size());
        }
    }

    private void logDebug(String meesage) {
        if(getLog().isDebugEnabled()) {
            getLog().debug(meesage);
        }
    }
}
