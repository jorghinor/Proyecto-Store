package com.gutti.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author Ivan Alban
 */
@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class
        }
)
public class StoreApp {

    public static void main(String[] args) {
        SpringApplication.run(StoreApp.class, args);
    }
}
