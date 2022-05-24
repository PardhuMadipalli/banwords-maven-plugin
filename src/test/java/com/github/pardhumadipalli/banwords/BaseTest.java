package com.github.pardhumadipalli.banwords;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class BaseTest {
    @BeforeEach
    void printTestName(TestInfo testInfo) {
        Log log = new SystemStreamLog();
        log.info("-------------------------------------------------------");
        log.info("	TEST : " + testInfo.getDisplayName());
        log.info("-------------------------------------------------------");
        System.setProperty(Constants.SKIP_EXECUTION_PROPERTY, "dummy");
    }
}
