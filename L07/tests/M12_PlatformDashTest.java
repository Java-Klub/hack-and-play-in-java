// Funkční kontroly: pouze chování hry.
public class M12_PlatformDashTest {
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
        var g = new M12_PlatformDash();
        g.restart();
        g.started = true;
        g.x = 240;
        g.y = 285;
        g.vy = -4;
        g.step();
        expect(!g.grounded && g.y < 285, "I063/F25: rising from below cannot snap onto platform top");
        g.x = 240;
        g.y = 240;
        g.vy = 12;
        g.step();
        equal(252.0, g.y, "I063/F25: descending top crossing still lands");
        expect(g.grounded && g.vy == 0, "I063/F25: valid landing rests on top");
        g.restart();
        g.held[java.awt.event.KeyEvent.VK_RIGHT] = true;
        g.jump();
        for (int tick = 0; tick < 200 && !g.won; tick++) {
            g.step();
            if (g.grounded && g.x < 380) {
                g.jump();
            }
        }
        expect(g.won && g.falls == 0, "I063/F25: original two-jump course remains playable");
        System.out.println("PASS " + checks + " checks");
    }
}
