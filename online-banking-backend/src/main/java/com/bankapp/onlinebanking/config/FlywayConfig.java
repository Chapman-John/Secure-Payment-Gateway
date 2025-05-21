package com.bankapp.onlinebanking.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayConfig {

    @Bean
    @Profile("dev") // Only use in development environment
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // Clean and rebuild the database (ONLY FOR DEVELOPMENT!)
            flyway.clean();
            flyway.migrate();
        };
    }

    @Bean
    @Profile("!dev") // Use in all other environments
    public FlywayMigrationStrategy productionFlywayMigrationStrategy() {
        return flyway -> {
            // Just migrate, never clean in production
            flyway.migrate();
        };
    }
}