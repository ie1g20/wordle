package cloud.wordle.wordle.Controller;

import cloud.wordle.wordle.DTO.*;
import cloud.wordle.wordle.Enums.GameStatus;
import cloud.wordle.wordle.Enums.GuessColour;
import cloud.wordle.wordle.Model.Game;
import cloud.wordle.wordle.Service.GameService;
import cloud.wordle.wordle.Service.GuessVerifyingService;
import cloud.wordle.wordle.Service.WordOfTheDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wordle")
@CrossOrigin(origins = "*")
@Slf4j
public class GameController {
    private final WordOfTheDayService wordOfTheDayService;
    private final GuessVerifyingService guessVerifyingService;
    private final GameService gameService;

    public GameController(WordOfTheDayService wordOfTheDayService,
                          GuessVerifyingService guessVerifyingService,
                          GameService gameService) {
        this.wordOfTheDayService = wordOfTheDayService;
        this.guessVerifyingService = guessVerifyingService;
        this.gameService = gameService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void pickWordOfTheDay() throws ExecutionException, InterruptedException {
        String today = LocalDate.now().toString();
        log.info("Scheduled job: pickWordOfTheDay started for date={}", today);

        if (!wordOfTheDayService.wordOfTheDayIsChosen(today)) {
            log.info("Word of the day not chosen yet for {} — choosing now.", today);
            wordOfTheDayService.chooseWordOfTheDay(today);
            log.info("Word of the day chosen for {}", today);
        } else {
            log.debug("Word of the day already chosen for {}", today);
        }
    }

    @PostMapping("/sendFeedback")
    public ResponseEntity<?> sendResponse(
            @RequestParam String guess,
            @RequestParam String gameId) throws ExecutionException, InterruptedException {

        log.info("Received sendFeedback request: gameId={}, guess={}", gameId, guess);

        if (!wordOfTheDayService.wordOfTheDayIsChosen(LocalDate.now().toString())) {
            log.debug("Word of the day not yet chosen for today; invoking pickWordOfTheDay()");
            pickWordOfTheDay();
        }

        Game game = gameService.getGameById(gameId);

        if (game == null) {
            log.warn("Game not found for id={}", gameId);
            return ResponseEntity.notFound().build();
        }

        if (!guessVerifyingService.valdiateWord(guess)) {
            log.warn("Invalid guess '{}' for game {}: not a valid word", guess, gameId);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid guess: not a valid word"));
        }

        if (game.getGuesses().containsKey(guess)) {
            log.warn("Duplicate guess '{}' for game {}", guess, gameId);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Already guessed!"));
        }

        String wordOfTheDay = wordOfTheDayService.getWordOfToday(LocalDate.now().toString());
        List<GuessColour> feedback = guessVerifyingService.checkGuess(guess, wordOfTheDay);
        game.getGuesses().put(guess, feedback);

        boolean isCorrect = guess.equals(wordOfTheDay);
        int attemptCount = game.getGuesses().size();
        game.setAttemps(attemptCount);

        if (isCorrect) {
            game.setStatus(GameStatus.WON);
            log.info("Game {} WON by player {} in {} attempts", gameId, game.getPlayerId(), attemptCount);
        } else if (attemptCount >= 6) {
            game.setStatus(GameStatus.LOST);
            log.info("Game {} LOST for player {} after {} attempts", gameId, game.getPlayerId(), attemptCount);
        } else {
            game.setStatus(GameStatus.IN_PROGRESS);
            log.debug("Game {} still in progress (attempts={})", gameId, attemptCount);
        }

        log.debug("Saving game {} (status={}, attempts={})", gameId, game.getStatus(), game.getAttemps());
        gameService.saveGame(game);
        log.debug("Game {} saved", gameId);

        log.info("Feedback for game {} guess '{}': {}", gameId, guess, feedback);

        GameResponse response = GameResponse.builder()
                .gameId(String.valueOf(game.getGameId()))
                .playerId(game.getPlayerId().toString())
                .date(game.getDate().toString())
                .attemptNumber(game.getAttemps())
                .guesses(convertGuessesToResponse(game.getGuesses()))
                .currentGuess(guess)
                .feedback(feedback.stream().map(GuessColour::toString).collect(Collectors.toList()))
                .attemptNumber(attemptCount)
                .maxAttempts(6)
                .status(game.getStatus().toString())
                .build();

        log.debug("Returning GameResponse for game {}", gameId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/play")
    public ResponseEntity<GameResponse> startNewGame(
            @RequestParam String playerId) throws ExecutionException, InterruptedException {

        log.info("Received startNewGame request for playerId={}", playerId);

        UUID playerUUID = playerId != null ? UUID.fromString(playerId) : UUID.randomUUID();
        LocalDate today = LocalDate.now();

        Game game = gameService.getPlayerCurrentGame(playerId);

        int gameId;
        String message;
        List<GuessResponse> guesses = new ArrayList<>();

        if (game != null) {
            // Existing game found
            gameId = game.getGameId();
            message = "Resuming existing game";
            guesses = convertGuessesToResponse(game.getGuesses());
            log.info("Resuming existing game {} for player {}", gameId, playerUUID);
        } else {
            // Create new game
            gameId = gameService.generateNextId();
            String wordOfTheDay = wordOfTheDayService.getWordOfToday(today.toString());

            game = new Game(
                    gameId,
                    playerUUID,
                    today,
                    wordOfTheDay,
                    new LinkedHashMap<>(),
                    0,
                    GameStatus.IN_PROGRESS,
                    LocalDateTime.now()
            );

            gameService.saveGame(game);
            message = "New game started";
            log.info("Created new game {} for player {} (wordOfTheDay length={})", gameId, playerUUID,
                    wordOfTheDay != null ? wordOfTheDay.length() : 0);
        }

        GameResponse response = GameResponse.builder()
                .gameId(String.valueOf(gameId))
                .playerId(playerUUID.toString())
                .date(today.toString())
                .maxAttempts(6)
                .status(game.getStatus().toString())
                .message(message)
                .guesses(guesses)
                .attemptNumber(guesses.size())
                .isGameOver(game.getStatus() != GameStatus.IN_PROGRESS)
                .build();

        log.debug("Returning startNewGame response for game {} (isGameOver={})", gameId, game.getStatus() != GameStatus.IN_PROGRESS);
        return ResponseEntity.ok(response);
    }

    private List<GuessResponse> convertGuessesToResponse(HashMap<String, List<GuessColour>> guesses) {
        if (guesses == null || guesses.isEmpty()) {
            log.debug("convertGuessesToResponse: no guesses to convert");
            return Collections.emptyList();
        }
        log.debug("convertGuessesToResponse: converting {} guesses", guesses.size());
        return guesses.entrySet().stream()
                .map(entry -> GuessResponse.builder()
                        .word(entry.getKey())
                        .feedback(entry.getValue().stream()
                                .map(GuessColour::toString)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
