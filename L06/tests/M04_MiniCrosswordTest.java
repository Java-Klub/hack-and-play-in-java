// Functional checks: game behaviour only.
public class M04_MiniCrosswordTest {
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
        var g = new M04_MiniCrossword();
        g.reset();
        g.command("1 CAT");
        g.command("2 ARE");
        g.command("3 TEN");
        expect(g.outcome.isEmpty(), "I030/F14: recount is local to each command, not accumulated across calls");
        g.command("4 CAT");
        g.command("5 ARE");
        g.command("6 TEN");
        expect(!g.outcome.isEmpty(), "I030/F14: all six clues finish crossword");
        g.reset();
        g.command("1 CAT");
        g.command("erase 1");
        expect(!g.solved[0] && g.outcome.isEmpty(), "I030/F14: erasing updates the next recount");
        System.out.println("PASS " + checks + " checks");
    }
}
