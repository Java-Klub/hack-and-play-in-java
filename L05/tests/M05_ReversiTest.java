// Funkční kontroly: pouze chování hry.
public class M05_ReversiTest {
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
        var g = new M05_Reversi();
        g.reset();
        expect(g.hasMove(1), "I004/F17: search must find a move outside the top row");
        g.board = new int[6][6];
        g.board[5][4] = 2;
        g.board[5][5] = 1;
        expect(g.hasMove(1), "I004/F17: legal move in the final row is not skipped");
        g.board = new int[6][6];
        expect(!g.hasMove(1), "I004/F17: empty board has no bracketed captures");
        System.out.println("PASS " + checks + " checks");
    }
}
