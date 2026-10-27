// Funkční kontroly: pouze chování hry.
public class M01_MinesweeperTest {
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
        var g = new M01_Minesweeper();
        g.mines = new boolean[6][6];
        g.mines[0][0] = true;
        equal(1, g.adjacent(1, 1), "I043/F18: diagonal mine is a neighbour");
        equal(0, g.adjacent(0, 0), "I043/F18: centre does not count itself");
        g.mines[0][1] = g.mines[1][0] = g.mines[1][1] = true;
        equal(3, g.adjacent(0, 0), "I043/F18: clipped corner has three neighbours");
        g.mines = new boolean[6][6];
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                g.mines[r][c] = true;
            }
        }
        equal(8, g.adjacent(2, 2), "I043/F18: all eight surrounding cells counted");
        System.out.println("PASS " + checks + " checks");
    }
}
