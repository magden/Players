package org.sport.players.utils;

import org.junit.jupiter.api.Test;
import org.sport.players.model.Player;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvReaderTest {


    @Test
    public void testLoadPlayers_validFile() throws IOException {
        Resource resource = new ClassPathResource("/data/player_test.csv");
        String filePath = resource.getFile().getAbsolutePath();
        CsvReader csvReader = new CsvReader(filePath);
        List<Player> players = csvReader.loadPlayers();
        // Assertions
        assertNotNull(players, "Players should not be NULL");
        assertFalse(players.isEmpty(), "Player's list could not be empty");
        assertEquals(9, players.size(), "Players size could be 9");
        assertEquals("aardsda01", players.getFirst().getPlayerID(), "PlayerID could be aardsda01");
        assertEquals(1981, players.getFirst().getBirthYear(), "BirthYear could be 1981");
        assertEquals(12, players.getFirst().getBirthMonth(), "BirthMonth could be 12");
        assertEquals(27, players.getFirst().getBirthDay(), "BirthDay could be 27");
        assertEquals("USA", players.getFirst().getBirthCountry(),"BirthCountry could be USA");
        assertEquals("CO", players.getFirst().getBirthState(), "BirthState could be CO");
        assertEquals("R", players.getFirst().getThrowsHand(), "Throws hand could be R");
        assertNull(players.getFirst().getDeathYear(), "Death year could be NULL");
    }

    @Test
    public void testLoadPlayers_EmptyFile() throws IOException {
        Resource resource = new ClassPathResource("/data/player_empty_test.csv");
        String filePath = resource.getFile().getAbsolutePath();
        CsvReader csvReader = new CsvReader(filePath);
        List<Player> players = csvReader.loadPlayers();
        // Assertions
        assertNotNull(players, "Players should not be NULL");
        assertTrue(players.isEmpty(), "Player's list could not be empty");
    }

    @Test
    public void testLoadPlayers_fileDoesNotExist() {
        String filePath = "path/to/nonexistentfile.csv";
        CsvReader csvReader = new CsvReader(filePath);
        RuntimeException thrown = assertThrows(RuntimeException.class, csvReader::loadPlayers);
        assertEquals("Error reading CSV file path/to/nonexistentfile.csv", thrown.getMessage());
    }

}
