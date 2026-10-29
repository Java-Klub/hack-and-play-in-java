int boost(int streak) {
    return streak >= 3 ? streak * 2 : streak;
}

void equal(int expected, int actual, String name) {
    if (expected != actual) {
        throw new AssertionError(name + ": expected " + expected + ", actual " + actual);
    }
}

void main() {
    equal(0, boost(0), "empty streak");
    equal(2, boost(2), "before the boundary");
    equal(6, boost(3), "first doubled streak");
    IO.println("PASS 3 checks");
}
