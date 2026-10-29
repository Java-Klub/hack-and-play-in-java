void main() {
    int[] powers = {2, 7, 4};
    int total = 0;
    for (int power : powers) {
        IO.println("Power: " + power);
        total += power;
    }
    IO.println("Total: " + total);
}
