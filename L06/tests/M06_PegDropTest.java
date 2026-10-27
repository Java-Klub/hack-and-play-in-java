// Functional checks: game behaviour only.
public class M06_PegDropTest {
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
        var g = new M06_PegDrop();
        g.restart();
        equal(100, g.scoreAt(3), "I081/F15: return the score of the requested bin");
        equal(10, g.scoreAt(7), "I081/F15: final bin accepted");
        int old = g.score;
        g.scoreAt(2);
        equal(old, g.score, "I081/F15: score query does not award points");
        boolean rejected = false;
        try {
            g.scoreAt(-1);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        expect(rejected, "I081/F15: invalid index has an explicit contract");
        System.out.println("PASS " + checks + " checks");
    }
}
