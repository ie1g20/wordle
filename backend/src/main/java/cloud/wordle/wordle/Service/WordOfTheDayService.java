package cloud.wordle.wordle.Service;

import cloud.wordle.wordle.Model.Word;
import cloud.wordle.wordle.Repository.WordOfTheDayRepository;
import cloud.wordle.wordle.Repository.WordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * A service class that chooses a word for each day
 */
@Service
public class WordOfTheDayService {

    private final WordRepository wordRepository;
    private final WordOfTheDayRepository wordOfTheDayRepository;

    public WordOfTheDayService(WordRepository wordRepository, WordOfTheDayRepository wordOfTheDayRepository) {
        this.wordRepository = wordRepository;
        this.wordOfTheDayRepository = wordOfTheDayRepository;
    }

    /**
     * A method that sets the word of the uses and updates Firebase
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void chooseWordOfTheDay(String date) throws ExecutionException, InterruptedException {
        Word word = selectWordOfTheDay();

        wordRepository.markWordAsUsed(word);

        wordOfTheDayRepository.choose(word.getWord(), date);
    }

    /**
     * A method that selects one of the unsed words as the word of the day
     * @return the word of the day
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private Word selectWordOfTheDay() throws ExecutionException, InterruptedException {
        List<Word> unusedWords = wordRepository.getUnusedWords();

        Random random = new Random();

        return unusedWords.get(random.nextInt(unusedWords.size()));
    }

    public boolean wordOfTheDayIsChosen(String date) throws ExecutionException, InterruptedException {
        return wordOfTheDayRepository.getWordOfTheDay(date) != null;
    }

    public String getWordOfToday(String date) throws ExecutionException, InterruptedException {
        return wordOfTheDayRepository.getWordOfTheDay(date);
    }
}
