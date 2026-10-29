/// # Solved task · 6.3 a noise survey
/// Lesson: 03-accumulators.html

void main() {
    int total = 0;
    int loud = 0;
    for (int sample = 0; sample < 3; sample++) {
        int noise = Integer.parseInt(IO.readln("Noise: ").strip());
        total += noise;
        if (noise >= 10) {
            loud++;
        }
    }
    IO.println("Total: " + total);
    IO.println("Loud: " + loud);
}
