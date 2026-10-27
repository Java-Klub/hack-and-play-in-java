// Functional checks: game behaviour only.
public class M01_ConnectFourTest {
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
        var g = new M01_ConnectFour();
        g.reset();
        g.drop(1);
        g.drop(2);
        equal(2, g.moves, "I003/F14: setup played two moves");
        g.reset();
        equal(0, g.moves, "I003/F14: reset must clear the round field, not a shadow local");
        equal('X', g.player, "I003/F14: first player restored");
        equal('.', g.winner, "I003/F14: winner cleared");
        expect(g.drop(7) && g.moves == 1, "I003/F14: next round starts counting at one");
        System.out.println("PASS " + checks + " checks");
    }
}
