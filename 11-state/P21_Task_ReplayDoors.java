/// # Task · 11.5 repair the repeatable door route
/// Lesson: 05-seeds.html

void main() {
    int seed = Integer.parseInt(IO.readln("Seed: ").strip());
    int[] first = route(seed);
    int[] replay = route(seed);
    IO.println("Route: " + Arrays.toString(first));
    IO.println("Replay matches: " + Arrays.equals(first, replay));
}

int[] route(int seed) {
    int[] doors = new int[4];
    for (int i = 0; i < doors.length; i++) {
        Random random = new Random(seed);
        doors[i] = random.nextInt(1, 7);
    }
    return doors;
}
