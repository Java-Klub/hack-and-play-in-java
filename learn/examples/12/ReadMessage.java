import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

String readMessage(Path file) throws IOException {
    return Files.readString(file).strip();
}

void main() {
    Path file = Path.of("message.txt");
    IO.println("Reading " + file.toAbsolutePath());
    try {
        IO.println("Message: " + readMessage(file));
    } catch (IOException failure) {
        IO.println("Cannot read message.txt.");
    }
}
