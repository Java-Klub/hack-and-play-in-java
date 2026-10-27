// Funkční kontroly: pouze chování hry.
public class M03_SlidingTilesTest {
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
        var g = new M03_SlidingTiles();
        g.moves = 17;
        g.reset(42);
        equal(0, g.moves, "I016/F14: new puzzle clears previous move counter");
        int[] sorted = g.tiles.clone();
        java.util.Arrays.sort(sorted);
        equal(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, sorted, "I016/F14: shuffle keeps all tile values");
        int chosen = -1;
        for (int n = 0; n < 9; n++) {
            if (g.canMove(n)) {
                chosen = n;
                break;
            }
        }
        expect(g.play(chosen) && g.moves == 1, "I016/F14: first slide is move one");
        g.reset(42);
        equal(0, g.moves, "I016/F14: repeated restart remains independent");
        System.out.println("PASS " + checks + " checks");
    }
}
