/// # Solved task · 12.2 a volume reader with a contract
/// Lesson: 02-exceptions.html

int readVolume(String text) {
    int volume = Integer.parseInt(text.strip());
    if (volume < 0 || volume > 10) {
        throw new IllegalArgumentException("Volume must be 0..10.");
    }
    return volume;
}

void main() {
    String reply = IO.readln("Volume: ");
    if (reply == null) return;
    try {
        IO.println("Volume set: " + readVolume(reply));
    } catch (NumberFormatException failure) {
        IO.println("Volume needs a whole number.");
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    }
}
