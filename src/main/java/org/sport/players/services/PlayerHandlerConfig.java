package org.sport.players.services;

import lombok.extern.slf4j.Slf4j;
import org.sport.players.dao.PlayerRepository;
import org.sport.players.utils.CsvReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PlayerHandlerConfig {

    @Bean
    @ConditionalOnProperty(name = "playerHandler.isDataBase", havingValue = "true")
    public DatabasePlayerHandler databasePlayerHandler(PlayerRepository playerRepository, CsvReader csvReader,
                                                       @Value("${playerHandler.reloadDatabaseOnStart}") Boolean reloadDatabaseOnStart) {
        log.info("DatabasePlayerHandler in use");
        return new DatabasePlayerHandler(playerRepository, csvReader, reloadDatabaseOnStart);
    }

    @Bean
    @ConditionalOnProperty(name = "playerHandler.isDataBase", havingValue = "false", matchIfMissing = true)
    public InMemoryPlayerHandler inMemoryPlayerHandler(CsvReader csvReader) {
        log.info("InMemoryPlayerHandler in use");
        return new InMemoryPlayerHandler(csvReader);
    }

}
