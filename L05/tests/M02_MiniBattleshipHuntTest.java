// Functional checks: game behaviour only.
public class M02_MiniBattleshipHuntTest {
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
        var g = new M02_MiniBattleshipHunt();
        for (int layout = 0; layout < 3; layout++) {
            g.layout(layout);
            int[] counts = new int[3];
            for (var row : g.board) {
                for (int cell : row) {
                    counts[cell]++;
                }
            }
            equal(3, counts[2], "I041/F16: every layout has all three cells of ship two");
            equal(2, counts[1], "I041/F16: ship one occupies two cells");
        }
        g.layout(0);
        equal(2, g.board[3][4], "I041/F16: last coordinate is not omitted");
        System.out.println("PASS " + checks + " checks");
    }
}
