void main() {
    int distance = Integer.parseInt(IO.readln("Distance: ").strip());
    if (distance < 0) {
        IO.println("Behind you");
    } else if (distance <= 3) {
        IO.println("Very close");
    } else {
        IO.println("Far away");
    }
}
