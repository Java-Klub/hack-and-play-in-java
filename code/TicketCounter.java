void main() {
    String text = IO.readln("Players: ");
    int players = Integer.parseInt(text.strip());
    int tokens = players * 3;
    IO.println("Tokens needed: " + tokens);
}
