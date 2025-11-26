package cloud.wordle.wordle.Config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class FirebaseConfig {

    public static Firestore getEmulatorClient() {
        String host = System.getenv("FIRESTORE_EMULATOR_HOST");
        if (host == null || host.isEmpty()) {
            host = "localhost:8085"; // default for local development
        }

        return FirestoreOptions.newBuilder()
                .setProjectId("demo-no-project")
                .setHost(host)
                .build()
                .getService();
    }
}
