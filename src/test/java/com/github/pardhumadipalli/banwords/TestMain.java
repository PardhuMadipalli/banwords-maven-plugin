package com.github.pardhumadipalli.banwords;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Collections;


public class TestMain extends BaseTest {

//	@BeforeEach
//	void printTestName(TestInfo testInfo) {
//		Log log = new SystemStreamLog();
//		log.info("-------------------------------------------------------");
//		log.info("	TEST : " + testInfo.getDisplayName());
//		log.info("-------------------------------------------------------");
//		System.setProperty(Constants.SKIP_EXECUTION_PROPERTY, "dummy");
//	}

	@Test
	@DisplayName("With Banned words")
	void testWithBannedWords() {
		BanWords banWords = getbanWordsObject("withBannedWords");
		Assertions.assertThrows(MojoFailureException.class, banWords::execute);
	}

	@Test
	@DisplayName("Banned word but skip execution")
	void testWithBannedWordsSkipExecution() throws MojoFailureException {
		BanWords banWords = getbanWordsObject("withBannedWords");
		System.setProperty(Constants.SKIP_EXECUTION_PROPERTY, "true");
		banWords.execute();
	}

	@Test
	@DisplayName("Without banned words")
	void testWithoutBannedWords() throws MojoFailureException {
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY);
		banWords.execute();
	}

	@Test
	@DisplayName("Invalid root directory path")
	void invalidProjectRootDir() {
		// Give a non existing filepath
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY + "/hello.txt");
		Assertions.assertThrows(MojoFailureException.class, banWords::execute);
	}

	@Test
	@DisplayName("Test with additional included words and file contains banned word")
	void fileWithAdditionalBannedWords() {
		// Use file without any banned words.
		// But add a word from this file to additional words to verify if we receive exception.
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY);
		banWords.setIncludeWords(Collections.singletonList(TestConstants.ADDITIONAL_BANNED_WORD));
		Assertions.assertThrows(MojoFailureException.class, banWords::execute);
	}

	@Test
	@DisplayName("Test with additional included words and file does not contain additional word")
	void fileWithoutAdditionalBannedWords() {
		// Use file without any banned words.
		// But add a word from this file to additional words to verify if we receive exception.
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_FREE_DIRECTORY);
		banWords.setIncludeWords(Collections.singletonList(TestConstants.SAFE_WORD));
	}

	@Test
	@DisplayName("Exclude bannedword1")
	void exludeABannedWord() throws MojoFailureException {
		BanWords banWords = getbanWordsObject(TestConstants.BANNED_WORD_CONTAINING_DIRECTORY);
		banWords.setExcludeWords(Collections.singletonList(TestConstants.BANNED_WORD));
		banWords.execute();
	}


	private BanWords getbanWordsObject(String rootDirectory) {
		BanWords banWords = new BanWords();
		banWords.setProjectRootDirectory(TestConstants.resourcesPath + rootDirectory);
		banWords.setBannedWordsFilePath(TestConstants.sampleBannedWordsPath);
		return banWords;
	}

}
