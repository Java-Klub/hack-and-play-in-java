// Functional checks: game behaviour only.
public class M01_MemoryPairsTest {
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
        var g = new M01_MemoryPairs();
        g.reset(12);
        g.cards = new int[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6};
        g.first = 4;
        equal(5, g.visible(4), "I033/F16: reveal the chosen slot, not card zero");
        equal(0, g.visible(3), "I033/F16: covered card remains hidden");
        g.second = 8;
        equal(3, g.visible(8), "I033/F16: second chosen slot visible");
        g.first = g.second = -1;
        g.matched[11] = true;
        equal(6, g.visible(11), "I033/F16: last matched card stays visible");
        System.out.println("PASS " + checks + " checks");
    }
}
