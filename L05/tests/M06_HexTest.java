// Functional checks: game behaviour only.
public class M06_HexTest {
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
        var g = new M06_Hex();
        g.reset();
        for (int r = 0; r < 5; r++) {
            g.board[r][4 - r] = 1;
        }
        expect(g.connected(1), "I006/F17: hex diagonal is a connected path");
        g.board[2][2] = 0;
        expect(!g.connected(1), "I006/F17: gap breaks the path");
        g.reset();
        for (int c = 0; c < 5; c++) {
            g.board[2][c] = 2;
        }
        expect(g.connected(2), "I006/F17: player two connects left to right");
        expect(!g.connected(1), "I006/F17: opposing pieces are not traversed");
        System.out.println("PASS " + checks + " checks");
    }
}
