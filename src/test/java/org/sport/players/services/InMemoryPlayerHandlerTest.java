package org.sport.players.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sport.players.model.Player;
import org.sport.players.utils.CsvReader;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InMemoryPlayerHandlerTest {

    private InMemoryPlayerHandler playerHandler;

    @BeforeEach
    public void setUp() {
        CsvReader csvReader = mock(CsvReader.class);
        playerHandler = new InMemoryPlayerHandler(csvReader);
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        Player player4 = new Player();
        player1.setPlayerID("1");
        player2.setPlayerID("2");
        player3.setPlayerID("3");
        player4.setPlayerID("4");
        List<Player> samplePlayers = List.of(player1, player2, player3, player4);
        when(csvReader.loadPlayers()).thenReturn(samplePlayers);
        playerHandler.init();
    }

    @Test
    public void testFindAll_validPage() {
        List<Player> result = playerHandler.findAll(1, 3);

        assertEquals(3, result.size(), "Page size is 3, could be 3 records");
        assertEquals("1", result.get(0).getPlayerID());
        assertEquals("2", result.get(1).getPlayerID());
        assertEquals("3", result.get(2).getPlayerID());

        result = playerHandler.findAll(1, 4);
        assertEquals(4, result.size(), "Page size is 4, could be 4 records");
    }

    @Test
    public void testFindAll_invalidPageCursor() {
        assertThrows(IllegalArgumentException.class, () -> playerHandler.findAll(0, 3));
        assertThrows(IllegalArgumentException.class, () -> playerHandler.findAll(1, 0));
    }

    @Test
    public void testFindById_existingPlayer() {
        Optional<Player> result = playerHandler.findById("2");
        // Assert
        assertTrue(result.isPresent());
        assertEquals("2", result.get().getPlayerID());
    }

    @Test
    public void testFindById_nonExistingPlayer() {
        Optional<Player> result = playerHandler.findById("nonexistent");
        assertFalse(result.isPresent());
    }

}
