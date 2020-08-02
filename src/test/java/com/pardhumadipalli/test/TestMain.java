package com.pardhumadipalli.test;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import com.pardhumadipalli.BanWords;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.*;


public class TestMain {

	private Log log;

	@BeforeEach
	public void printTestStartMessage(TestInfo testInfo) {
		getLog().info("-------------------------------------------------------");
		getLog().info("T E S T : "+testInfo.getDisplayName());
		getLog().info("-------------------------------------------------------");

	}

	@Test
	@DisplayName("With Banned words")
	public void testWithBannedWords(){
		BanWords banWords = new BanWords();
		String resourcesPath = "src/test/resources/";
		banWords.setProjectRootDirectory(resourcesPath+"withBannedWords/");
		banWords.setBannedWordsFilePath("sampleBannedWords.txt");
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
		BanWords banWords = new BanWords();
		String resourcesPath = "src/test/resources/";
		banWords.setProjectRootDirectory(resourcesPath+"withoutBannedWords/");
		banWords.setBannedWordsFilePath("sampleBannedWords.txt");
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
		BanWords banWords = new BanWords();
		String resourcesPath = "src/test/resources/";
		// Give a non existing filepath
		banWords.setProjectRootDirectory(resourcesPath+"withoutBannedWords/hello.txt");
		banWords.setBannedWordsFilePath("sampleBannedWords.txt");
		boolean receivedMojoException = false;
		try {
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
			getLog().info("Received an exception as expected");
		}

		Assertions.assertTrue(receivedMojoException);
	}



	private Log getLog() {
		if (this.log == null) {
			this.log = new SystemStreamLog();
		}

		return this.log;
	}
}
