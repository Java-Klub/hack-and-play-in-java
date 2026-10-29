/// # Solved task · 11.4 roll a real pair
/// Lesson: 04-random.html

void main() {
    Random random = new Random();
    IO.println("Sum: " + rollPair(random));
}

int rollPair(Random random) {
    int first = random.nextInt(1, 7);
    int second = random.nextInt(1, 7);
    return first + second;
}
