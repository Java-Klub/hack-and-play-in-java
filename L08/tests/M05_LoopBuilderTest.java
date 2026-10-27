// Functional checks: game behaviour only.
public class M05_LoopBuilderTest {
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
        var g = new M05_LoopBuilder();
        equal(new int[]{0, 4}, g.vertices(12), "I088/F18: vertical edge joins the next vertex row");
        equal(new int[]{11, 15}, g.vertices(23), "I088/F18: final vertical edge stays in its column");
        equal(new int[]{14, 15}, g.vertices(11), "I088/F18: horizontal edge joins adjacent columns");
        g.reset();
        for (int n : new int[]{0, 1, 2, 9, 10, 11, 12, 15, 16, 19, 20, 23}) {
            g.edges[n] = true;
        }
        expect(g.oneLoop(), "I088/F18: perimeter edges form one real loop");
        System.out.println("PASS " + checks + " checks");
    }
}
