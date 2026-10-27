// Functional checks: game behaviour only.
public class M09_FourColourMapTest {
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
        var g = new M09_FourColourMap();
        g.reset();
        g.colours = new int[]{1, 2, 3, 4, 4};
        equal(1, g.conflicts(), "I087/F17: conflict on the last adjacency must count");
        g.colours = new int[]{1, 2, 3, 4, 1};
        equal(0, g.conflicts(), "I087/F17: non-adjacent equal colours allowed");
        g.colours = new int[]{1, 1, 1, 0, 0};
        equal(3, g.conflicts(), "I087/F17: count every coloured conflicting edge");
        g.reset();
        equal(0, g.conflicts(), "I087/F17: uncoloured regions are not conflicts");
        System.out.println("PASS " + checks + " checks");
    }
}
