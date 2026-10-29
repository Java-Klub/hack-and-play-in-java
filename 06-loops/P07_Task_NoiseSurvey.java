/// # Úkol · 6.3 průzkum hluku
/// Lekce: 03-accumulators.html

void main() {
    int total = 0;
    int loud = 0;
    for (int sample = 0; sample < 3; sample++) {
        int noise = Integer.parseInt(IO.readln("Noise: ").strip());
    }
    IO.println("Total: " + total);
    IO.println("Loud: " + loud);
}
