/// # Task · 6.3 a noise survey
/// Lesson: 03-accumulators.html

void main() {
    int total = 0;
    int loud = 0;
    for (int sample = 0; sample < 3; sample++) {
        int noise = Integer.parseInt(IO.readln("Noise: ").strip());
    }
    IO.println("Total: " + total);
    IO.println("Loud: " + loud);
}
