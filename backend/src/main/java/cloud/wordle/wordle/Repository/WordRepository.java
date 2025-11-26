package cloud.wordle.wordle.Repository;

import com.google.cloud.firestore.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import cloud.wordle.wordle.Model.*;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

@Repository
public class WordRepository {

    private final CollectionReference collection;

    public WordRepository(Firestore db) {
        this.collection = db.collection("words");
    }

    public boolean checkWordExists(String word) {
        try {
            Query query = collection.whereEqualTo("word", word).limit(1);
            QuerySnapshot querySnapshot = query.get().get();

            return !querySnapshot.isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Word> getUnusedWords() throws ExecutionException, InterruptedException {
        // Query documents where 'used' is false and only select 'word' and 'used' fields
        Query query = collection
                .whereEqualTo("used", false)
                .select("word", "used");

        QuerySnapshot querySnapshot = query.get().get();
        return querySnapshot.getDocuments().stream()
                .map(doc -> doc.toObject(Word.class))
                .collect(Collectors.toList());
    }

    public void markWordAsUsed(Word wordToMark)
            throws ExecutionException, InterruptedException {

        Query query = collection.whereEqualTo("word", wordToMark.getWord()).limit(1);
        QuerySnapshot querySnapshot = query.get().get();

        DocumentReference docRef = querySnapshot.getDocuments().get(0).getReference();

        docRef.update("used", true).get();
    }
}
