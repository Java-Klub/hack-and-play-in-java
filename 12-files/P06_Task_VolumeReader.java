/// # Úkol · 12.2 čtení hlasitosti se smluvenými pravidly
/// Lekce: 02-exceptions.html

int readVolume(String text) {
    int volume = Integer.parseInt(text.strip());
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
