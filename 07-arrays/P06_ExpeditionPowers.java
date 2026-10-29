void main() {
    int[] powers = {4, 5, 9, 0};
    int minimum = Integer.parseInt(IO.readln("Minimum: ").strip());
    int ready = 0;
    int total = 0;
    for (int power : powers) {
        ready++;
        total += power;
    }
    IO.println("Ready: " + ready);
    IO.println("Power: " + total);
}
