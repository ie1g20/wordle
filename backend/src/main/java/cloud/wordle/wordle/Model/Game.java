package cloud.wordle.wordle.Model;
import cloud.wordle.wordle.Enums.GameStatus;
import cloud.wordle.wordle.Enums.GuessColour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
public class Game {
    private final int gameId;
    private final UUID playerId;
    private final LocalDate date;
    private transient final String word;
    private HashMap<String, List<GuessColour>> guesses;
    private int attemps;
    private static final int MAX_ATTEMPT = 6;
    private GameStatus status;
    private final LocalDateTime createdAt;

}
