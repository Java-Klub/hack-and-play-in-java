// Funkční kontroly: pouze chování hry.
public class M08_MiniInvadersTest {
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
        var g = new M08_MiniInvaders();
        g.restart();
        g.speedX = 0;
        g.bulletActive = true;
        g.bulletX = g.formationX + 10;
        g.bulletY = g.formationY + 17;
        g.step();
        expect(!g.bulletActive, "I050/F25: a hitting bullet is consumed");
        equal(1, g.score, "I050/F25: one bullet awards exactly one hit");
        equal(14, g.countAliens(), "I050/F25: one alien removed");
        g.step();
        equal(14, g.countAliens(), "I050/F25: consumed bullet cannot hit another row");
        System.out.println("PASS " + checks + " checks");
    }
}
