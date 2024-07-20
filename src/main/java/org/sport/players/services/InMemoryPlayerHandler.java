package org.sport.players.services;

import jakarta.annotation.PostConstruct;
import org.sport.players.utils.CsvReader;
import org.sport.players.model.Player;

import java.util.*;

import static org.sport.players.utils.JvmAnalyzer.runJvmMemoryAnalyze;


public class InMemoryPlayerHandler implements PlayerHandler {

    private final CsvReader csvReader;

    private List<Player> playersList = new ArrayList<>();
    private final Map<String, Player> playersMap = new HashMap<>();

    public InMemoryPlayerHandler(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    @PostConstruct
    void init(){
        runJvmMemoryAnalyze("Before data loading:");
        playersList = csvReader.loadPlayers();
        for(Player player : playersList){
            playersMap.put(player.getPlayerID(), player);
        }
        runJvmMemoryAnalyze("Memory analyze after data loading:");
    }

    @Override
    public List<Player> findAll(int pageCursor, int pageSize) {
        if (pageCursor < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page cursor and page size must be greater than 0.");
        }
        int fromIndex = (pageCursor - 1) * pageSize;
        if (fromIndex >= playersList.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + pageSize, playersList.size());
        return playersList.subList(fromIndex, toIndex);
    }

    @Override
    public Optional<Player> findById(String id) {
              return Optional.ofNullable(playersMap.get(id));
    }

}
