![](https://github.com/PardhuMadipalli/banwords-maven-plugin/workflows/Java%20Build/badge.svg) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Maven Central](https://img.shields.io/maven-central/v/com.github.pardhumadipalli/banwords-maven-plugin?label=latest%20version)
# banwords-maven-plugin
A maven plugin to check if any offensive words are used in the project.

## Introduction
This plugin helps you to check if your project contains any offensive words. By default, the plugin checks for all
the words in this [text file](https://github.com/PardhuMadipalli/banwords-maven-plugin/blob/main/src/main/resources/bannedWords.txt).

More words can be included and default words can be excluded while configuring the plugin.

## How to use the plugin

### Maven
Add the following to your pom.xml to run the plugin with default settings.
```xml
<build>
    ...
    <plugins>
        ...
        <plugin>
            <groupId>com.github.pardhumadipalli</groupId>
            <artifactId>banwords-maven-plugin</artifactId>
            <version>0.0.3</version>
            <executions>
                <execution>
                    <goals>
                        <goal>banwords</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Additional configuration options
| Name | Default value | Description |
|------|---------------|-------------|
| projectRootDirectory | ${project.basedir} | The directory in which all files are to be searched for banned words. |
| includeWords | Nil | Words that are to be banned in addition to the default list. |
| excludeWords | Nil | Any words that are excluded from the list of banned words. |

#### Example
```xml
<plugin>
    <groupId>com.github.pardhumadipalli</groupId>
    <artifactId>banwords-maven-plugin</artifactId>
    <version>0.0.3</version>
    <configuration>
        <excludeWords>
            <excludeWord>nsfw-word-1</excludeWord>
            <excludeWord>nsfw-word-2</excludeWord>
        </excludeWords>
        <includeWords>
            <includeWord>offensiveword1</includeWord>
            <includeWord>offensiveword2</includeWord>
        </includeWords>
        <projectRootDirectory>${project.basedir}/src/main</projectRootDirectory>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>banwords</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
