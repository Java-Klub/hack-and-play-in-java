void main() {
    Random random = new Random();
    IO.println("Sum: " + rollPair(random));
}

int rollPair(Random random) {
    return random.nextInt(1, 7);
}
