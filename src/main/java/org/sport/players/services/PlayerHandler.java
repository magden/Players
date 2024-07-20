package org.sport.players.services;

import org.sport.players.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerHandler {

    /**
     * Retrieves a paginated list of players.
     * This method returns a list of players based on the provided page cursor and page size.
     *
     * @param pageCursor The page number to fetch, starting from 1.
     * @param pageSize   The number of players to include in each page. A value less than 1 will result
     *                   in an IllegalArgumentException.
     * @return A list of players for the requested page. If the page cursor is out of range or there
     * are no players to return, an empty list is returned
     * @throws IllegalArgumentException in case pageCursor or pageSize is wrong
     */
    List<Player> findAll(int pageCursor, int pageSize);

    /**
     * Retrieves a player by their unique identifier.
     * <p>
     * This method looks up a player using the provided player ID. If a player with the given ID is found,
     * it is returned wrapped in an {@code Optional}. If no player with the given ID exists, an empty
     * {@code Optional} is returned.
     *
     * @param id The unique identifier of the player to retrieve.
     * @return An {@code Optional} containing the player with the specified ID if found; otherwise, an
     * empty {@code Optional}.
     */
    Optional<Player> findById(String id);

}
