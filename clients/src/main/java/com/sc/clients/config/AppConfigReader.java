package com.sc.clients.config;

import com.sc.clients.constants.AppConstants;
import com.sc.clients.model.AppConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfigReader {

    public static AppConfig fetchAppConfig() throws IOException {
        try (InputStream input = AppConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            return AppConfig.builder()
                    .host(prop.getProperty(AppConstants.SERVER_HOST, "localhost"))
                    .port(Integer.parseInt(prop.getProperty(AppConstants.SERVER_PORT, "9099")))
                    .threads(Integer.parseInt(prop.getProperty(AppConstants.SERVER_THREADS, "100")))
                    .mode(prop.getProperty(AppConstants.SERVER_MODE, "BLOCKING"))
                    .build();

        }
    }
}
