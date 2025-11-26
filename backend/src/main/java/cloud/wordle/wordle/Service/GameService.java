package cloud.wordle.wordle.Service;

import cloud.wordle.wordle.Model.Game;
import cloud.wordle.wordle.Repository.GameRepository;
import cloud.wordle.wordle.Repository.WordRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public int generateNextId() throws ExecutionException, InterruptedException {
        int lastGameId = gameRepository.getLatestId();
        return lastGameId + 1;
    }

    public Game getGameById(String gameId) throws ExecutionException, InterruptedException {
        return gameRepository.findById(gameId);
    }

    public Game getPlayerCurrentGame(String playerId) throws ExecutionException, InterruptedException {
        return gameRepository.getPlayerLatestGame(playerId);
    }

    public void saveGame(Game game) throws ExecutionException, InterruptedException {
        gameRepository.save(game);
    }

}