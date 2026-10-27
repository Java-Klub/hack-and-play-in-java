// Functional checks: game behaviour only.
public class M10_MirrorLaserTest {
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
        var g = new M10_MirrorLaser();
        var r = new M10_MirrorLaser.RayState(1, 2, 1, 0);
        var next = r.advance();
        equal(new M10_MirrorLaser.RayState(2, 2, 1, 0), next, "I057/F19: record update follows its named direction");
        equal(1, r.x(), "I057/F19: advancing leaves original record unchanged");
        equal(new M10_MirrorLaser.RayState(1, 1, 0, -1), new M10_MirrorLaser.RayState(1, 2, 0, -1).advance(), "I057/F19: vertical update");
        g.restart();
        g.mirrors[2][2] = 1;
        g.mirrors[0][2] = 1;
        g.mirrors[0][5] = 2;
        g.trace();
        equal("Target reached!", g.result, "I057/F19: real beam follows the authored route");
        System.out.println("PASS " + checks + " checks");
    }
}
