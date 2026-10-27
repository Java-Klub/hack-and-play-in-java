// Functional checks: game behaviour only.
public class M03_TrailDuelTest {
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
        var g = new M03_TrailDuel();
        equal(new M03_TrailDuel.Direction(0, -1), g.directionFor(java.awt.event.KeyEvent.VK_W), "I061/F09: W maps to upward movement");
        equal(new M03_TrailDuel.Direction(0, -1), g.directionFor(java.awt.event.KeyEvent.VK_UP), "I061/F09: both players share direction values");
        equal(new M03_TrailDuel.Direction(-1, 0), g.directionFor(java.awt.event.KeyEvent.VK_LEFT), "I061/F09: left direction");
        equal(null, g.directionFor(java.awt.event.KeyEvent.VK_F1), "I061/F09: unrelated key has no direction");
        g.restart();
        g.key(java.awt.event.KeyEvent.VK_W, true);
        g.step();
        equal(9, g.ay, "I061/F09: actual cyan trail turns up");
        System.out.println("PASS " + checks + " checks");
    }
}
