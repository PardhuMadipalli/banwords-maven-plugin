package com.github.pardhumadipalli.banwords.test;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import com.github.pardhumadipalli.banwords.BanWords;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;


public class TestMain {

	private Log log;

	@BeforeEach
	public void printTestStartMessage(TestInfo testInfo) {
		getLog().info("-------------------------------------------------------");
		getLog().info(" T E S T : "+testInfo.getDisplayName());
		getLog().info("-------------------------------------------------------");
	}

	@Test
	@DisplayName("With Banned words")
	public void testWithBannedWords(){
		BanWords banWords = getbanWordsObject("withBannedWords");
		boolean receivedMojoException = false;
		try {
			Assertions.assertNotNull(banWords);
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
			getLog().info("Received an exception as expected");
		}

		Assertions.assertTrue(receivedMojoException);
	}

	@Test
	@DisplayName("Without banned words")
	public void testWithoutBannedWords(){
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY);
		boolean receivedMojoException = false;
		try {
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
		}

		Assertions.assertFalse(receivedMojoException);
	}

	@Test
	@DisplayName("Invalid root directory path")
	public void invalidProjectRootDir(){
		// Give a non existing filepath
		BanWords banWords =	getbanWordsObject( TestConstants.BANNED_WORD_FREE_DIRECTORY + "/hello.txt");
		boolean receivedMojoException = false;
		try {
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
			getLog().info("Received an exception as expected");
		}

		Assertions.assertTrue(receivedMojoException);
	}

	@Test
	@DisplayName("Test with additional included words and file contains banned word")
	public void fileWithAdditionalBannedWords() {
		boolean receivedMojoException = false;
		// Use file without any banned words.
		// But add a word from this file to additional words to verify if we receive exception.
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY);
		List<String> additionalBannedList = new ArrayList<String>(1);
		additionalBannedList.add(TestConstants.ADDITIONAL_BANNED_WORD);
		banWords.setAdditionalWords(additionalBannedList);

		try {
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
			getLog().info("Received an exception as expected");
		}
		Assertions.assertTrue(receivedMojoException);
	}

	@Test
	@DisplayName("Test with additional included words and file does not contain additional word")
	public void fileWithoutAdditionalBannedWords() {
		boolean receivedMojoException = false;
		// Use file without any banned words.
		// But add a word from this file to additional words to verify if we receive exception.
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY);
		List<String> additionalBannedList = new ArrayList<>(1);
		additionalBannedList.add(TestConstants.SAFE_WORD);
		banWords.setAdditionalWords(additionalBannedList);

		try {
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
		}
		Assertions.assertFalse(receivedMojoException);
	}

	@Test
	@DisplayName("Exclude bannedword1")
	public void exludeABannedWord(){
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_CONTAINING_DIRECTORY);

		List<String> exludedWord = new ArrayList<>(1);
		exludedWord.add(TestConstants.BANNED_WORD);
		banWords.setExcludedwords(exludedWord);

		boolean receivedMojoException = false;
		try {
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
		}
		// Although file used contains banned word "bannedword1",
		// no exception should be received as it is excluded
		Assertions.assertFalse(receivedMojoException);
	}


	private BanWords getbanWordsObject(String rootDirectory) {
		BanWords banWords = new BanWords();
		banWords.setProjectRootDirectory(TestConstants.resourcesPath + rootDirectory);
		banWords.setBannedWordsFilePath(TestConstants.sampleBannedWordsPath);
		return banWords;
	}

	private Log getLog() {
		if (this.log == null) {
			this.log = new SystemStreamLog();
		}

		return this.log;
	}
}
