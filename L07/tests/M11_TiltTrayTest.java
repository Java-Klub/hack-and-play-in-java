// Functional checks: game behaviour only.
public class M11_TiltTrayTest {
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
        var g = new M11_TiltTray();
        g.restart();
        g.started = true;
        g.x = 168;
        g.y = 150;
        g.vx = 3;
        g.vy = 0;
        g.step();
        equal(170.0, g.x, "I067/F25: collision separates the ball outside obstacle");
        expect(g.vx < 0 && !g.hit(g.obstacles[0]), "I067/F25: reflected ball is not left penetrating");
        g.clearKeys();
        for (int n = 0; n < 100; n++) {
            g.step();
        }
        expect(Double.isFinite(g.x) && Double.isFinite(g.y), "I067/F25: later updates remain finite");
        g.restart();
        g.started = true;
        g.x = 530;
        g.y = 345;
        g.vx = g.vy = 0;
        g.step();
        expect(g.won, "I067/F25: slow target entry still works");
        System.out.println("PASS " + checks + " checks");
    }
}
