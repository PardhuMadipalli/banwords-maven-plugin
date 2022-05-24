package com.github.pardhumadipalli.banwords;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Mojo(name = "help", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class Help extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResourceAsStream(Constants.HELP_FILE))))) {
            String line;
            while ((line = br.readLine()) != null) {
                getLog().info(line);
            }
        } catch (IOException e) {
            throw new MojoFailureException("Could not print the help text.", e);
        }
    }
}
