import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/// # Vyřešený úkol · 12.4 respektuj vybrané jméno souboru
/// Lekce: 04-files.html

String readTarget(String filename) throws IOException {
    return Files.readString(Path.of(filename)).strip().toUpperCase(Locale.ROOT);
}

void main() {
    String reply = IO.readln("File: ");
    if (reply == null) return;
    String filename = reply.isBlank() ? "target.txt" : reply.strip();
    IO.println("Reading " + Path.of(filename).toAbsolutePath());
    try {
        IO.println("Target: " + readTarget(filename));
    } catch (IOException failure) {
        IO.println("Cannot read " + filename + ".");
    }
}
