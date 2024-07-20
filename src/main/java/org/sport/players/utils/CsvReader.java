package org.sport.players.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.sport.players.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class CsvReader {

    private final String filePath;

    public CsvReader(@Value("${data.players.csv}") String filePath) {
        this.filePath = filePath;
    }

    public List<Player> loadPlayers() {
        try (FileReader reader = new FileReader(filePath)) {
            log.info("Reading csv file: {}", filePath);
            return new CsvToBeanBuilder<Player>(reader)
                    .withType(Player.class)
                    .build()
                    .parse();
        } catch (IOException e) {
            String errorMessage = String.format("Error reading CSV file %s", filePath);
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

}
