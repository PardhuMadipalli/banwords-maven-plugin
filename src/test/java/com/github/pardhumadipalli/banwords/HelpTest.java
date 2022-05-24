package com.github.pardhumadipalli.banwords;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HelpTest extends BaseTest {

    @Test
    @DisplayName("Help")
    void testHelp() throws MojoExecutionException, MojoFailureException {
        Help help = new Help();
        help.execute();
    }
}
