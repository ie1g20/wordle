package cloud.wordle.wordle.Repository;

import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;
import java.util.Map;

@Repository
public class WordOfTheDayRepository {
    private final CollectionReference collection;

    public WordOfTheDayRepository(Firestore db) {
        this.collection = db.collection("dailyWords");
    }

    public void choose(String word, String date) {
        collection.document(date).set(Map.of("word", word));
    }

    public String getWordOfTheDay(String date) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = collection.document(date).get().get();
        if (doc.exists()) {
            return doc.getString("word");
        } else {
            return null; // explicitly return null if the document doesn't exist
        }
    }

}
