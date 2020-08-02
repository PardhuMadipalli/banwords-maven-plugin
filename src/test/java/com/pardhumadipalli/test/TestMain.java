package com.pardhumadipalli.test;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import com.pardhumadipalli.BanWords;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


public class TestMain {

	private Log log;

	@Test
	public void testWithBannedWords(){
		BanWords banWords = new BanWords();
		String resourcesPath = "src/test/resources/";
		banWords.setProjectRootDirectory(resourcesPath+"withBannedWords/");
		boolean receivedMojoException = false;
		try {
			getLog().info("");
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
		}

		Assertions.assertTrue(receivedMojoException);
	}

	@Test
	public void testWithoutBannedWords(){
		BanWords banWords = new BanWords();
		String resourcesPath = "src/test/resources/";
		banWords.setProjectRootDirectory(resourcesPath+"withoutBannedWords/");
		boolean receivedMojoException = false;
		try {
			getLog().info("");
			banWords.execute();
		} catch (MojoFailureException ex) {
			receivedMojoException = true;
			getLog().info("Received an exception as expected");
		}

		Assertions.assertFalse(receivedMojoException);
	}

	private Log getLog() {
		if (this.log == null) {
			this.log = new SystemStreamLog();
		}

		return this.log;
	}
}
