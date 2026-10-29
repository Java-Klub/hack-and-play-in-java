void main() {
    int seed = Integer.parseInt(IO.readln("Seed: ").strip());
    int[] first = route(seed);
    int[] replay = route(seed);
    IO.println("Route: " + Arrays.toString(first));
    IO.println("Replay matches: " + Arrays.equals(first, replay));
}

int[] route(int seed) {
    Random random = new Random(seed);
    int[] doors = new int[4];
    for (int i = 0; i < doors.length; i++) {
        doors[i] = random.nextInt(1, 7);
    }
    return doors;
}
