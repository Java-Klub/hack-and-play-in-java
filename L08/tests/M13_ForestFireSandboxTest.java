// Functional checks: game behaviour only.
public class M13_ForestFireSandboxTest {
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
        var g = new M13_ForestFireSandbox();
        g.cells = new int[40][60];
        g.cells[20][10] = 2;
        for (int x = 11; x <= 14; x++) {
            g.cells[20][x] = 1;
        }
        g.cells[21][11] = 1;
        g.step();
        equal(1, g.cells[20][12], "I125/F26: fire advances one neighbour, not across the row in one tick");
        equal(0, g.cells[20][10], "I125/F26: old burning tree becomes empty");
        equal(2, g.cells[20][11], "I125/F26: adjacent old tree ignites");
        equal(1, g.cells[21][11], "I125/F26: newly burning tree cannot ignite another during the same tick");
        g.step();
        equal(2, g.cells[20][12], "I125/F26: next tick advances the front");
        int[][] old = new int[40][60];
        old[0][0] = 2;
        old[0][1] = 1;
        int[][] copy = java.util.Arrays.stream(old).map(int[]::clone).toArray(int[][]::new);
        g.nextForest(old);
        equal(copy, old, "I125/F26: source forest remains unchanged");
        var random = new java.util.Random(7);
        int[][] forest = new int[40][60];
        for (var row : forest) {
            for (int x = 0; x < 60; x++) {
                row[x] = random.nextInt(3);
            }
        }
        int[][] expected = new int[40][60];
        for (int y = 39; y >= 0; y--) {
            for (int x = 59; x >= 0; x--) {
                if (forest[y][x] == 1) {
                    expected[y][x] = 1;
                    for (var d : new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
                        int xx = x + d[0], yy = y + d[1];
                        if (xx >= 0 && xx < 60 && yy >= 0 && yy < 40 && forest[yy][xx] == 2) {
                            expected[y][x] = 2;
                        }
                    }
                }
            }
        }
        equal(expected, g.nextForest(forest), "I125/F26: reversing both traversals leaves simultaneous update unchanged");
        System.out.println("PASS " + checks + " checks");
    }
}
