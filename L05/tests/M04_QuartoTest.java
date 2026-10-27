// Functional checks: game behaviour only.
public class M04_QuartoTest {
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
        var g = new M04_Quarto();
        g.reset();
        int[] diagonal = {0, 5, 10, 15};
        for (int n = 0; n < 4; n++) {
            g.board[diagonal[n]] = n;
        }
        expect(g.won(), "I011/F16: the main diagonal must be searched");
        g.reset();
        for (int n = 0; n < 4; n++) {
            g.board[1 + n * 4] = n;
        }
        expect(g.won(), "I011/F16: a full column can win");
        g.reset();
        for (int n = 0; n < 4; n++) {
            g.board[n] = n;
        }
        expect(g.won(), "I011/F16: existing row wins remain");
        g.reset();
        expect(!g.won(), "I011/F16: empty slots cannot form a line");
        System.out.println("PASS " + checks + " checks");
    }
}
