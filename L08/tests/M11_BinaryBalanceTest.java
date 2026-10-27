// Functional checks: game behaviour only.
public class M11_BinaryBalanceTest {
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
        var g = new M11_BinaryBalance();
        g.restart();
        g.cycle(1, 0);
        equal(0, g.givens[0][1], "I091/F20: playing must not rewrite the fixed clue template");
        g.restart();
        equal(0, g.cells[0][1], "I091/F20: reset restores originally empty cell");
        int[][] copy = g.copyBoard(g.givens);
        copy[2][1] = 2;
        equal(0, g.givens[2][1], "I091/F20: every inner row is independent");
        equal(1, copy[0][0], "I091/F20: copying retains given values");
        System.out.println("PASS " + checks + " checks");
    }
}
