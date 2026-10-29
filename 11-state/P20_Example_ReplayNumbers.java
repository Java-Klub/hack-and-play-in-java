/// # Worked example · 11.5 Same start, same experiment
/// Lesson: 05-seeds.html

void main() {
    int[] first = sequence(42);
    int[] replay = sequence(42);
    IO.println("First: " + Arrays.toString(first));
    IO.println("Replay: " + Arrays.toString(replay));
    IO.println("Same: " + Arrays.equals(first, replay));
}

int[] sequence(int seed) {
    Random random = new Random(seed);
    int[] values = new int[4];
    for (int i = 0; i < values.length; i++) {
        values[i] = random.nextInt(10);
    }
    return values;
}
