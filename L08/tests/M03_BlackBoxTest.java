// Funkční kontroly: pouze chování hry.
public class M03_BlackBoxTest {
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
        var g = new M03_BlackBox();
        g.reset();
        boolean[][] box = new boolean[4][4];
        box[1][1] = true;
        equal("W1", g.trace(box, "N1"), "I084/F18: a right-side diagonal ball deflects the ray left");
        box = new boolean[4][4];
        equal("S1", g.trace(box, "N1"), "I084/F18: empty corridor crosses straight");
        box[0][0] = true;
        equal("HIT", g.trace(box, "N1"), "I084/F18: direct hit retains priority");
        equal("REFLECT", g.trace(box, "N2"), "I084/F18: entry-edge diagonal reflects");
        g.command("mark 1 1");
        g.command("mark 1 2");
        g.command("check");
        expect(g.message.contains("disagree"), "I084/F18: incorrect guesses give a current disagreement");
        g.command("ray N1");
        equal("Probe recorded", g.message, "I084/F18: a new probe replaces the earlier failed-check event");
        g.command("mark 1 1");
        equal("Guess removed", g.message, "I084/F18: a changed guess reports its own event");
        System.out.println("PASS " + checks + " checks");
    }
}
