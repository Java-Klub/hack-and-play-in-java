import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/// # Hotový příklad · 12.5 Ukládej až po ověření
/// Lekce: 05-save.html

void saveNote(Path destination, String note) throws IOException {
    if (note.isBlank()) {
        throw new IllegalArgumentException("A note needs text.");
    }
    Files.writeString(destination, note.strip() + "\n");
}

void main() {
    String reply = IO.readln("Note: ");
    if (reply == null) return;
    try {
        Path destination = Path.of("note.txt");
        saveNote(destination, reply);
        IO.println("Saved " + destination.toAbsolutePath() + ".");
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    } catch (IOException failure) {
        IO.println("Cannot save note.txt.");
    }
}
