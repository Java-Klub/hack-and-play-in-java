// Functional checks: game behaviour only.
public class M08_NonogramTest {
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
        var g = new M08_Nonogram();
        equal(new int[]{3}, g.runs(new int[]{0, 1, 1, 1}), "I059/F17: flush the run at the end of the array");
        equal(new int[]{2, 1}, g.runs(new int[]{1, 1, 0, 1}), "I059/F17: separate groups remain separate");
        equal(new int[]{}, g.runs(new int[]{0, 0, 0}), "I059/F17: blank line has no runs");
        equal(new int[]{1}, g.runs(new int[]{1}), "I059/F17: single-cell run");
        System.out.println("PASS " + checks + " checks");
    }
}
