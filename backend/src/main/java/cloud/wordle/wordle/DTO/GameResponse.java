package cloud.wordle.wordle.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private String gameId;
    private String playerId;
    private String date;
    private List<GuessResponse> guesses;
    private String currentGuess;
    private List<String> feedback;
    private int attemptNumber;
    private int maxAttempts;
    private String status;
    private String message;
    private boolean isGameOver;
}