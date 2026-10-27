// Functional checks: game behaviour only.
public class M11_DominosaTest {
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
        var g = new M11_Dominosa();
        g.reset();
        equal(g.pairKey(2, 3), g.pairKey(3, 2), "I083/F19: reversed endpoints describe the same domino value");
        equal(new M11_Dominosa.Domino(0, 1), g.pairKey(2, 3), "I083/F19: record stores ordered endpoint values");
        expect(!g.pairKey(0, 1).equals(g.pairKey(2, 3)), "I083/F19: different unordered pairs stay distinct");
        g.command("1 3 1 4");
        expect(g.used.contains(new M11_Dominosa.Domino(0, 1)), "I083/F19: game tracks the record value");
        System.out.println("PASS " + checks + " checks");
    }
}
