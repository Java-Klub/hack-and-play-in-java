// Functional checks: game behaviour only.
public class M07_BridgesTest {
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
        var g = new M07_Bridges();
        g.restart();
        g.choose(140, 45);
        equal(1, g.bridges[0], "I090/F20: edge changed");
        g.undo();
        equal(0, g.bridges[0], "I090/F20: undo needs an independent previous array");
        int[] copy = g.snapshot();
        copy[0] = 2;
        equal(0, g.bridges[0], "I090/F20: changing snapshot cannot alter live network");
        g.choose(140, 45);
        g.choose(140, 45);
        g.undo();
        equal(1, g.bridges[0], "I090/F20: undo restores exactly the preceding state");
        System.out.println("PASS " + checks + " checks");
    }
}
