/// # Hotový příklad · 3.4 Z odpovědi udělej číslo
/// Lekce: 04-numbers.html

void main() {
    String text = IO.readln("Players: ");
    int players = Integer.parseInt(text.strip());
    int tokens = players * 3;
    IO.println("Tokens needed: " + tokens);
}
