// Funkční kontroly: pouze chování hry.
public class M12_RuleGardenTest {
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
        var g = new M12_RuleGarden();
        equal(java.util.List.of("...#...", "..#.#..", ".#...#."), g.grow(90, 7, 3), "I123/F26: each row reads only the previous generation");
        boolean[] before = {false, true, false, false, false};
        boolean[] saved = before.clone();
        g.nextRow(before, 90);
        equal(saved, before, "I123/F26: computing next row does not overwrite its input");
        equal(java.util.List.of(".#.", "#.#", "..."), g.grow(90, 3, 3), "I123/F26: outside cells stay off");
        var random = new java.util.Random(3);
        for (int sample = 0; sample < 10; sample++) {
            boolean[] row = new boolean[13];
            for (int c = 0; c < 13; c++) {
                row[c] = random.nextBoolean();
            }
            boolean[] reverseOracle = new boolean[13];
            int rule = random.nextInt(256);
            for (int c = 12; c >= 0; c--) {
                int pattern = row[c] ? 2 : 0;
                if (c > 0 && row[c - 1]) {
                    pattern += 4;
                }
                if (c < 12 && row[c + 1]) {
                    pattern++;
                }
                reverseOracle[c] = ((rule>>pattern)&1) != 0;
            }
            equal(reverseOracle, g.nextRow(row, rule), "I123/F26: traversal order does not affect a generation");
        }
        System.out.println("PASS " + checks + " checks");
    }
}
