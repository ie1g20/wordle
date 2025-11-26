package cloud.wordle.wordle.Service;

import cloud.wordle.wordle.Enums.GuessColour;
import cloud.wordle.wordle.Repository.WordOfTheDayRepository;
import cloud.wordle.wordle.Repository.WordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A class that verifies a guess
 */
@Service
public class GuessVerifyingService {
    private final WordOfTheDayRepository wordOfTheDayRepository;
    private final WordRepository wordRepository;

    public GuessVerifyingService(WordOfTheDayRepository wordOfTheDayRepository, WordRepository wordRepository) {
        this.wordOfTheDayRepository = wordOfTheDayRepository;
        this.wordRepository = wordRepository;
    }

    public boolean valdiateWord(String word) {
        return wordRepository.checkWordExists(word);
    }

    /**
     * A class that given a word, returns feedback based on the word of the day
     * @param word
     * @return feedback
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<GuessColour> checkGuess(String word, String wordOfTheDay) {
        List<GuessColour> feedback = new ArrayList<>();
        StringBuilder remaining = new StringBuilder(wordOfTheDay);

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == remaining.charAt(i)) {
                feedback.add(GuessColour.GREEN);
                remaining.setCharAt(i, '0'); // mark as used
            } else {
                feedback.add(null); // placeholder for now
            }
        }

        for (int i = 0; i < word.length(); i++) {
            if (feedback.get(i) == null) { // only check letters not already green
                char c = word.charAt(i);
                int index = remaining.indexOf(String.valueOf(c));
                if (index != -1) {
                    feedback.set(i, GuessColour.YELLOW);
                    remaining.setCharAt(index, '0'); // mark as used
                } else {
                    feedback.set(i, GuessColour.GREY);
                }
            }
        }

        return feedback;
    }

}
