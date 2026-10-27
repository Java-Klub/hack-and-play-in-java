// Functional checks: game behaviour only.
public class M04_CarrotInABoxTest {
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
        var g = new M04_CarrotInABox();
        g.prizeA = true;
        equal(M04_CarrotInABox.Holder.PLAYER_TWO, g.holderAfterSwap(true), "I045/F21: swapping changes the named holder");
        equal(M04_CarrotInABox.Holder.PLAYER_ONE, g.holderAfterSwap(false), "I045/F21: keeping leaves prize with player one");
        g.prizeA = false;
        equal(M04_CarrotInABox.Holder.PLAYER_ONE, g.holderAfterSwap(true), "I045/F21: opposite starting box also swaps");
        g.outcome = "";
        g.choose(false);
        expect(g.outcome.contains("Player 2"), "I045/F21: actual reveal uses the enum result");
        System.out.println("PASS " + checks + " checks");
    }
}
