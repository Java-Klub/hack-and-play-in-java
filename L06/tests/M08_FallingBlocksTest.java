// Functional checks: game behaviour only.
public class M08_FallingBlocksTest {
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
        var g = new M08_FallingBlocks();
        g.restart();
        int oldX = g.px, oldY = g.py;
        expect(g.fits(g.piece, 6, 5), "I054/F15: free trial position fits");
        expect(g.px == oldX && g.py == oldY, "I054/F15: fit query must not teleport the falling piece");
        expect(!g.fits(g.piece, -1, 0), "I054/F15: out-of-bounds trial rejected");
        expect(g.px == oldX && g.py == oldY, "I054/F15: rejected query is also pure");
        g.board[5][6] = 1;
        expect(!g.fits(g.piece, 6, 5), "I054/F15: occupied cells block placement");
        System.out.println("PASS " + checks + " checks");
    }
}
