package org.sport.players.web;

import lombok.AllArgsConstructor;
import org.sport.players.model.Player;
import org.sport.players.services.PlayerHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
@AllArgsConstructor
public class PlayerController {
    private final PlayerHandler playerHandler;

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers(@RequestParam(defaultValue = "1") int pageCursor,
                                                      @RequestParam(defaultValue = "10000") int pageSize) {
        try {
            List<Player> players = playerHandler.findAll(pageCursor, pageSize);
            return ResponseEntity.ok(players);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id) {
        Optional<Player> player = playerHandler.findById(id);

        return player.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null));
    }

}
