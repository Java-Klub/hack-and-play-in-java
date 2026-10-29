/// # Úkol · 11.4 hoď skutečnou dvojicí
/// Lekce: 04-random.html

void main() {
    Random random = new Random();
    IO.println("Sum: " + rollPair(random));
}

int rollPair(Random random) {
    return random.nextInt(1, 7);
}
