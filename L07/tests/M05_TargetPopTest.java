// Funkční kontroly: pouze chování hry.
public class M05_TargetPopTest {
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
        var g = new M05_TargetPop();
        g.restart();
        g.tick(999);
        equal(M05_TargetPop.Phase.READY, g.phase, "I060/F21: idle game does not expire");
        g.click(g.targetX, g.targetY, 1000);
        equal(M05_TargetPop.Phase.RUNNING, g.phase, "I060/F21: first click starts round");
        g.tick(g.deadline);
        equal(M05_TargetPop.Phase.FINISHED, g.phase, "I060/F21: exact deadline finishes instead of resetting");
        int hits = g.hits;
        g.click(g.targetX, g.targetY, g.deadline + 1);
        equal(hits, g.hits, "I060/F21: finished round rejects late clicks");
        g.restart();
        equal(M05_TargetPop.Phase.READY, g.phase, "I060/F21: explicit restart returns to ready");
        System.out.println("PASS " + checks + " checks");
    }
}
