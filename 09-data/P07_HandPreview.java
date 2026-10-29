void main() {
    Card[] hand = {new Card("Moon", 2), new Card("Sun", 5)};
    Card[] preview = hand;
    preview[0] = new Card("Star", 9);
    IO.println("Hand: " + hand[0].suit() + " " + hand[0].value());
    IO.println("Preview: " + preview[0].suit() + " " + preview[0].value());
    IO.println("Separate arrays: " + (hand != preview));
    IO.println("Second card kept: " + hand[1].equals(preview[1]));
}

record Card(String suit, int value) {
}
