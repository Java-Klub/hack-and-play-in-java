// Functional checks: game behaviour only.
public class M05_SokobanTest {
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
        var g = new M05_Sokoban();
        g.reset();
        expect(!g.floor(0, 2), "I017/F15: wall is not walkable floor");
        expect(g.floor(4, 2), "I017/F15: open cell accepted");
        expect(!g.floor(-1, 2) && !g.floor(2, 5), "I017/F15: outside coordinates rejected");
        int x = g.x, y = g.y, bx = g.bx, by = g.by;
        g.floor(3, 3);
        expect(g.x == x && g.y == y && g.bx == bx && g.by == by, "I017/F15: asking about floor must not move anything");
        System.out.println("PASS " + checks + " checks");
    }
}
