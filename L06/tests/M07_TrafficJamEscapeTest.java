// Functional checks: game behaviour only.
public class M07_TrafficJamEscapeTest {
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
        var g = new M07_TrafficJamEscape();
        g.reset();
        int[] oldX = g.xs.clone(), oldY = g.ys.clone();
        expect(!g.fits(0, 1, 2), "I019/F15: blocker detected");
        equal(oldX, g.xs, "I019/F15: failed placement probe must not move the car");
        equal(oldY, g.ys, "I019/F15: query preserves every y position");
        expect(g.fits(1, 2, 3), "I019/F15: legal downward position accepted");
        equal(oldY, g.ys, "I019/F15: successful query is pure too");
        System.out.println("PASS " + checks + " checks");
    }
}
