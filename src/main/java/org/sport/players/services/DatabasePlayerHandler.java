package org.sport.players.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.sport.players.utils.CsvReader;
import org.sport.players.dao.PlayerRepository;
import org.sport.players.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.sport.players.utils.JvmAnalyzer.runJvmMemoryAnalyze;


@RequiredArgsConstructor
public class DatabasePlayerHandler implements PlayerHandler {
    private final PlayerRepository playerRepository;
    private final CsvReader csvReader;
    private final Boolean reloadDatabase;

    @PostConstruct
    void init() {
        if (playerRepository.count() == 0 || reloadDatabase) {
            runJvmMemoryAnalyze("Reading from csv:");
            List<Player> players = csvReader.loadPlayers();
            runJvmMemoryAnalyze("After reading csv:");
            playerRepository.saveAll(players);
            runJvmMemoryAnalyze("After Storing to db:");
        }
    }

    @Override
    public List<Player> findAll(int pageCursor, int pageSize) {
        if (pageCursor < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page cursor and page size must be greater than 0.");
        }
        Pageable pageable = PageRequest.of(pageCursor - 1, pageSize);
        Page<Player> playerPage = playerRepository.findAll(pageable);
        return playerPage.getContent();
    }

    @Override
    public Optional<Player> findById(String id) {
        return playerRepository.findById(id);
    }

}
