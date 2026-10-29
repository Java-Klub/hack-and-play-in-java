void main() {
    int distance = Integer.parseInt(IO.readln("Distance: ").strip());
    if (distance <= 2) {
        IO.println("Danger");
    } else if (distance <= 5) {
        IO.println("Near");
    } else {
        IO.println("Clear");
    }
}
