// Functional checks: game behaviour only.
public class M06_AntHighwayTest {
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
        var g = new M06_AntHighway();
        g.reset();
        g.x = 0;
        g.y = 0;
        g.direction = 2;
        g.step();
        equal(80, g.x, "I124/F18: west neighbour wraps to the final column");
        equal(0, g.y, "I124/F18: horizontal step does not change row");
        g.reset();
        g.x = 0;
        g.y = 0;
        g.direction = 1;
        g.black[0][0] = true;
        g.step();
        equal(60, g.y, "I124/F18: north neighbour wraps to final row");
        expect(!g.black[0][0] && g.steps == 1, "I124/F18: old tile flipped exactly once");
        System.out.println("PASS " + checks + " checks");
    }
}
