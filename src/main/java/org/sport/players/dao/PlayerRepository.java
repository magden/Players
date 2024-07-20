package org.sport.players.dao;

import org.sport.players.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
    // Custom query methods (if needed) can be added here
}
