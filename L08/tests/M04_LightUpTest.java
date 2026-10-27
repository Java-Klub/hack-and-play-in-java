// Functional checks: game behaviour only.
public class M04_LightUpTest {
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
        var g = new M04_LightUp();
        g.reset();
        g.bulbs[0][2] = true;
        equal(1, g.visible(0, 0), "I086/F18: light travels beyond the immediate neighbour");
        g.bulbs = new boolean[3][3];
        g.bulbs[1][2] = true;
        equal(0, g.visible(1, 0), "I086/F18: numbered wall blocks a ray");
        g.bulbs = new boolean[3][3];
        g.bulbs[2][2] = true;
        equal(0, g.visible(0, 0), "I086/F18: diagonal bulb is not visible");
        System.out.println("PASS " + checks + " checks");
    }
}
