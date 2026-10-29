import java.util.function.IntUnaryOperator;

/// # Úkol · 14.1 otestuj pravidlo doručení
/// Lekce: 01-checks.html

record Example(int distance, int expected) {}

List<Example> examples() {
    return List.of(new Example(1, 2));
}

int price(int distance) {
    if (distance < 0 || distance > 9) {
        throw new IllegalArgumentException("Use distances 0..9.");
    }
    return distance <= 3 ? 2 : 5;
}

void verify(IntUnaryOperator rule) {
    if (examples().isEmpty()) throw new AssertionError("Add examples.");
    for (Example example : examples()) {
        if (example.distance() < 0 || example.distance() > 9) {
            throw new AssertionError("Use distances 0..9.");
        }
        int actual = rule.applyAsInt(example.distance());
        if (actual != example.expected()) {
            throw new AssertionError("Distance " + example.distance()
                    + ": expected " + example.expected() + ", actual " + actual);
        }
    }
}

boolean rejects(IntUnaryOperator rule) {
    try {
        verify(rule);
        return false;
    } catch (AssertionError failure) {
        return true;
    }
}

void main() {
    verify(this::price);
    if (!rejects(distance -> distance < 3 ? 2 : 5)) {
        throw new AssertionError("Missing last nearby example.");
    }
    if (!rejects(distance -> 2)) {
        throw new AssertionError("Missing a far example.");
    }
    if (!rejects(distance -> 5)) {
        throw new AssertionError("Missing a nearby example.");
    }
    IO.println("PASS delivery examples");
}
