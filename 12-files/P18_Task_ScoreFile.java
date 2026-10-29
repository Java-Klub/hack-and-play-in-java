import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/// # Task · 12.5 save the score to the chosen destination
/// Lesson: 05-save.html

void saveScore(Path destination, int score) throws IOException {
    Files.writeString(Path.of("score.txt"), score + "\n");
}

void main() {
    String filename = IO.readln("Save file: ");
    if (filename == null) return;
    String reply = IO.readln("Score: ");
    if (reply == null) return;
    Path destination = Path.of(filename.isBlank() ? "score.txt" : filename.strip());
    try {
        int score = Integer.parseInt(reply.strip());
        saveScore(destination, score);
        IO.println("Saved " + destination.toAbsolutePath() + ".");
    } catch (NumberFormatException failure) {
        IO.println("Use a whole score.");
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    } catch (IOException failure) {
        IO.println("Cannot save score.");
    }
}
