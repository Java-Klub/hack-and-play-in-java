void main() {
    String text = IO.readln("Energy: ");
    int energy = Integer.parseInt(text.strip());
    int boosted = energy * 2 + 2;
    IO.println("Boosted energy: " + boosted);
}
