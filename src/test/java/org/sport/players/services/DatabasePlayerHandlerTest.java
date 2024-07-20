package org.sport.players.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sport.players.dao.PlayerRepository;
import org.sport.players.model.Player;
import org.sport.players.utils.CsvReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabasePlayerHandlerTest {

    private PlayerRepository playerRepository;
    private CsvReader csvReader;
    private DatabasePlayerHandler playerHandler;

    @BeforeEach
    public void setUp() {
        playerRepository = mock(PlayerRepository.class);
        csvReader = mock(CsvReader.class);
        playerHandler = new DatabasePlayerHandler(playerRepository, csvReader, true);
    }

    @Test
    public void testInit_withEmptyDatabase() {
        when(playerRepository.count()).thenReturn(0L);
        List<Player> samplePlayers = generatePlayers();
        when(csvReader.loadPlayers()).thenReturn(samplePlayers);
        playerHandler.init();
        // Verify that saveAll was called with the sample data
        verify(playerRepository, times(1)).saveAll(samplePlayers);
    }

    @Test
    public void testInit_withNonEmptyDatabase() {
        playerHandler = new DatabasePlayerHandler(playerRepository, csvReader, false);
        when(playerRepository.count()).thenReturn(3L);
        playerHandler.init();
        verify(playerRepository, never()).saveAll(anyList());
    }

    @Test
    public void testFindAll_validPage() {
        List<Player> samplePlayers = generatePlayers();
        Pageable pageable = PageRequest.of(0, 4);
        Page<Player> playerPage = new PageImpl<>(samplePlayers, pageable, samplePlayers.size());
        when(playerRepository.findAll(pageable)).thenReturn(playerPage);

        List<Player> result = playerHandler.findAll(1, 4);
        // Assert
        assertEquals(4, result.size());
        assertEquals("1", result.get(0).getPlayerID());
        assertEquals("2", result.get(1).getPlayerID());
        assertEquals("3", result.get(2).getPlayerID());
    }

    @Test
    public void testFindAll_invalidPageCursor() {
        assertThrows(IllegalArgumentException.class, () -> playerHandler.findAll(0, 3));
        assertThrows(IllegalArgumentException.class, () -> playerHandler.findAll(1, 0));
    }

    @Test
    public void testFindById_existingPlayer() {
        Player samplePlayer = new Player();
        samplePlayer.setPlayerID("1");
        samplePlayer.setBats("A");

        when(playerRepository.findById("1")).thenReturn(Optional.of(samplePlayer));
        Optional<Player> result = playerHandler.findById("1");
        assertTrue(result.isPresent());
        assertEquals("A", result.get().getBats());
    }

    @Test
    public void testFindById_nonExistingPlayer() {
        when(playerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        Optional<Player> result = playerHandler.findById("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    public void testDatabaseTableDoesNotExist() {
        when(playerRepository.findById("1")).thenThrow(new RuntimeException("Table does not exist"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> playerHandler.findById("1"));
        assertEquals("Table does not exist", thrown.getMessage());
    }

    @Test
    public void testDatabaseConnectionProblem() {
        // Mock the findById method to throw a RuntimeException simulating a connection problem
        when(playerRepository.findById("1")).thenThrow(new RuntimeException("Connection problem"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> playerHandler.findById("1"));
        assertEquals("Connection problem", thrown.getMessage());
    }

    List<Player> generatePlayers() {
        Player player1 = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        Player player4 = new Player();
        player1.setPlayerID("1");
        player2.setPlayerID("2");
        player3.setPlayerID("3");
        player4.setPlayerID("4");
        return List.of(player1, player2, player3, player4);
    }

}
