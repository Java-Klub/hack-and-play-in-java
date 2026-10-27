// Functional checks: game behaviour only.
public class M02_CooperateOrDefectTest {
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
        var g = new M02_CooperateOrDefect();
        equal(3, g.reward(true, true), "I044/F09: mutual cooperation rewards both");
        equal(0, g.reward(true, false), "I044/F09: betrayed cooperator earns zero");
        equal(5, g.reward(false, true), "I044/F09: lone defector earns five");
        equal(1, g.reward(false, false), "I044/F09: mutual defection earns one");
        g.reset();
        g.play(true, false);
        equal(new int[]{0, 5}, g.scores, "I044/F09: both players use the same viewpoint rule");
        System.out.println("PASS " + checks + " checks");
    }
}
