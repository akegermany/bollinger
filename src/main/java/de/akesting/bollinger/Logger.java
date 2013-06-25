package de.akesting.bollinger;

import java.io.File;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

public final class Logger {

    private Logger() {
        throw new IllegalStateException();
    }

    public static void initalize(File log4jConfigFile) {
        if (!log4jConfigFile.exists() || log4jConfigFile.isFile()) {
            throw new IllegalArgumentException("log4jConfigFile=" + log4jConfigFile + " does not exit");
        }
        System.out.println("log4j configuration=" + log4jConfigFile.getAbsolutePath());
        PropertyConfigurator.configure(log4jConfigFile.getAbsolutePath());
    }

    public static void initialize(String log4jConfigName) {
        URL log4jConfigUrl = Logger.class.getResource(log4jConfigName);
        System.out
                .println("log4j configName=" + log4jConfigName + ", configuration=" + log4jConfigUrl.toExternalForm());
        PropertyConfigurator.configure(log4jConfigUrl);
    }

}
