// Funkční kontroly: pouze chování hry.
public class M02_NetTest {
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
        var g = new M02_Net();
        g.tiles = new int[4][4];
        g.tiles[0][0] = 2;
        expect(!g.connected()[0][1], "I058/F18: neighbour needs a reciprocal connector");
        g.tiles[0][1] = 8;
        expect(g.connected()[0][1], "I058/F18: right-left ports form a connection");
        expect(!g.connected()[1][1], "I058/F18: diagonal tile is not automatically connected");
        g.solvedBoard();
        expect(g.won(), "I058/F18: authored pipe tree remains valid");
        System.out.println("PASS " + checks + " checks");
    }
}
