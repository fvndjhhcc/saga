package by.raikiri.migration_service.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flywayCreationService(
            @Value("${spring.datasource.primary.url}") String url,
            @Value("${spring.datasource.primary.username}") String user,
            @Value("${spring.datasource.primary.password}") String password) {
        return createFlywayInstance(url, user, password, "classpath:db/migration/creation_service");
    }

    @Bean
    public Flyway flywayConfirmationService(
            @Value("${spring.datasource.second.url}") String url,
            @Value("${spring.datasource.second.username}") String user,
            @Value("${spring.datasource.second.password}") String password) {
        return createFlywayInstance(url, user, password, "classpath:db/migration/confirmation_service");
    }

    @Bean
    public Flyway flywayProcessingService(
            @Value("${spring.datasource.third.url}") String url,
            @Value("${spring.datasource.third.username}") String user,
            @Value("${spring.datasource.third.password}") String password) {
        return createFlywayInstance(url, user, password, "classpath:db/migration/processing_service");
    }

    @Bean
    public CommandLineRunner commandLineRunner(Flyway flywayCreationService, Flyway flywayConfirmationService,
                                               Flyway flywayProcessingService) {
        return args -> {
            flywayCreationService.migrate();
            flywayConfirmationService.migrate();
            flywayProcessingService.migrate();
        };
    }

    private Flyway createFlywayInstance(String url, String user, String password, String location) {
        return Flyway.configure()
                .dataSource(url, user, password)
                .locations(location)
                .load();
    }
}