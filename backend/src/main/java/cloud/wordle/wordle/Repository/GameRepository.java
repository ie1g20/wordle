package cloud.wordle.wordle.Repository;

import cloud.wordle.wordle.Enums.GameStatus;
import cloud.wordle.wordle.Enums.GuessColour;
import cloud.wordle.wordle.Model.Game;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class GameRepository {
    private final CollectionReference collection;

    public GameRepository(Firestore db) {
        this.collection = db.collection("games");
    }

    public Game getPlayerLatestGame(String playerId) throws ExecutionException, InterruptedException {
        String today = LocalDate.now().toString();

        Query query = collection
                .whereEqualTo("playerId", playerId)
                .whereEqualTo("date", today)
                .limit(1);

        QuerySnapshot querySnapshot = query.get().get();

        if (querySnapshot.isEmpty()) {
            return null;
        }

        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        return documentToGame(document);
    }

    public Game findById(String gameId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = collection.document(gameId).get().get();

        if (!document.exists()) {
            return null;
        }

        return documentToGame(document);
    }

    public int getLatestId() throws ExecutionException, InterruptedException {
        Query query = collection.orderBy("gameId", Query.Direction.DESCENDING).limit(1);
        QuerySnapshot querySnapshot = query.get().get();

        if (querySnapshot.isEmpty()) {
            return 0;
        }

        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        Long gameId = document.getLong("gameId");

        return gameId != null ? gameId.intValue() : 0;
    }

    /**
     * Save a game to Firestore (fixed to store LocalDate as string)
     */
    public void save(Game game) throws ExecutionException, InterruptedException {
        Map<String, Object> gameData = gameToMap(game);
        collection.document(String.valueOf(game.getGameId()))
                .set(gameData)
                .get();
    }

    private Game documentToGame(DocumentSnapshot document) {
        int gameId = document.getLong("gameId").intValue();
        UUID playerId = UUID.fromString(document.getString("playerId"));
        LocalDate date = LocalDate.parse(document.getString("date"));
        String word = document.getString("word");

        Map<String, Object> guessesRaw = (Map<String, Object>) document.get("guesses");
        LinkedHashMap<String, List<GuessColour>> guesses = new LinkedHashMap<>();

        if (guessesRaw != null) {
            guesses = guessesRaw.entrySet().stream()
                    .map(e -> {
                        Map<String, Object> valueMap = (Map<String, Object>) e.getValue();
                        List<String> feedbackStrings = (List<String>) valueMap.get("feedback");
                        int index = ((Long) valueMap.get("index")).intValue();
                        List<GuessColour> feedback = feedbackStrings.stream()
                                .map(String::toUpperCase)
                                .map(GuessColour::valueOf)
                                .collect(Collectors.toList());
                        return new AbstractMap.SimpleEntry<>(e.getKey(),
                                new AbstractMap.SimpleEntry<>(feedback, index));
                    })
                    .sorted(Comparator.comparingInt(e -> e.getValue().getValue())) // sort by index
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().getKey(),
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));
        }

        int attempts = document.getLong("attempts").intValue();
        GameStatus status = GameStatus.valueOf(document.getString("status"));

        com.google.cloud.Timestamp timestamp = document.getTimestamp("createdAt");
        LocalDateTime createdAt = LocalDateTime.ofInstant(
                timestamp.toDate().toInstant(),
                ZoneId.systemDefault()
        );

        return new Game(gameId, playerId, date, word, guesses, attempts, status, createdAt);
    }


    private Map<String, Object> gameToMap(Game game) {
        Map<String, Object> gameData = new HashMap<>();
        gameData.put("gameId", game.getGameId());
        gameData.put("playerId", game.getPlayerId().toString());
        gameData.put("date", game.getDate().toString()); // LocalDate -> String
        gameData.put("word", game.getWord());

        // Save guesses with index
        Map<String, Object> guessesForFirestore = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, List<GuessColour>> entry : game.getGuesses().entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("feedback", entry.getValue().stream()
                    .map(GuessColour::toString)
                    .collect(Collectors.toList()));
            map.put("index", i++);
            guessesForFirestore.put(entry.getKey(), map);
        }
        gameData.put("guesses", guessesForFirestore);

        gameData.put("attempts", game.getAttemps());
        gameData.put("status", game.getStatus().toString());
        gameData.put("createdAt", com.google.cloud.Timestamp.now());

        return gameData;
    }


}
