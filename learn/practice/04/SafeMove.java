void main() {
    int energy = 5;
    int cost = Integer.parseInt(IO.readln("Move cost: ").strip());
    energy = energy - cost;
    if (cost < 1 || cost > 5) {
        IO.println("Move refused");
    }
    IO.println("Move accepted");
    IO.println("Energy: " + energy);
}
