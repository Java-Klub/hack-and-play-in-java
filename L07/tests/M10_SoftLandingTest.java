// Functional checks: game behaviour only.
public class M10_SoftLandingTest {
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
        var g = new M10_SoftLanding();
        g.restart();
        g.state = "FLYING";
        g.fuel = 0;
        g.up = true;
        g.step();
        equal(0, g.fuel, "I064/F25: fuel never becomes negative");
        expect(Math.abs(g.velocityY-.04)<1e-9, "I064/F25: empty tank gives no thrust");
        g.restart();
        g.state = "FLYING";
        g.left = g.right = g.up = true;
        g.step();
        equal(239, g.fuel, "I064/F25: combined controls consume one unit per tick");
        g.restart();
        expect(!g.left && !g.right && !g.up, "I064/F25: restart releases controls");
        System.out.println("PASS " + checks + " checks");
    }
}
