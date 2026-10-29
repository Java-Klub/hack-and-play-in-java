void main() {
    int energy = 5;
    int cost = Integer.parseInt(IO.readln("Move cost: ").strip());
    if (cost < 1 || cost > energy) {
        IO.println("Move refused");
        IO.println("Energy: " + energy);
        return;
    }
    energy = energy - cost;
    IO.println("Move accepted");
    IO.println("Energy: " + energy);
}
