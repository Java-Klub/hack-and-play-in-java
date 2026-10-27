// Funkční kontroly: pouze chování hry.
public class M07_GoFishTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
        + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
        + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }
    public static void main(String[] args) throws Exception {
        var g = new M07_GoFish();
        g.hands = java.util.List.of(new java.util.ArrayList<>(java.util.List.of(7, 7, 7, 7)), new java.util.ArrayList<>());
        g.books = new int[2];
        g.collect(0);
        equal(0, g.hands.get(0).size(), "I034/F25: booking four cards removes exactly four");
        equal(1, g.books[0], "I034/F25: one complete rank awards one book");
        equal(4, g.hands.get(0).size() + 4 * g.books[0], "I034/F25: card-plus-book value conserved");
        g.collect(0);
        equal(1, g.books[0], "I034/F25: collecting again cannot duplicate the book");
        System.out.println("PASS " + checks + " checks");
    }
}
