// Funkční kontroly: pouze chování hry.
public class M08_MagnetDominoesTest {
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
        var g = new M08_MagnetDominoes();
        g.restart();
        g.cycle(0);
        g.undo();
        equal(-1, g.state[0], "I092/F20: undo restores undecided magnet");
        g.cycle(0);
        g.cycle(1);
        g.undo();
        equal(1, g.state[0], "I092/F20: undo keeps earlier domino change");
        equal(-1, g.state[1], "I092/F20: latest change reverted");
        int[] copy = g.copyState();
        copy[0] = 0;
        equal(1, g.state[0], "I092/F20: saved state does not alias live poles");
        System.out.println("PASS " + checks + " checks");
    }
}
