package cloud.wordle.wordle.Config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class FirebaseConfig {
    public static Firestore getEmulatorClient() {
        return FirestoreOptions.newBuilder()
                .setProjectId("demo-no-project") // Must match emulator project
                .setHost("localhost:8085")       // Emulator API port
                .build()
                .getService();
    }

}
