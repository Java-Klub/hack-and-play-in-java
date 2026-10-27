// Functional checks: game behaviour only.
public class M10_TentsTest {
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
        var g = new M10_Tents();
        g.restart();
        var cases = g.learnerChecks();
        for (var e : cases) {
            equal(e.expected(), g.treeAt(e.x(), e.y()), "I089/F33: expectations must accept actual tree positions");
        }
        String[] names = {"only-first-tree", "swapped-coordinates", "every-cell-is-a-tree"};
        for (int mutant = 0; mutant < 3; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                boolean wrong = false;
                if (mutant == 0) {
                    wrong = e.x() == g.trees[0].x && e.y() == g.trees[0].y;
                }
                if (mutant == 1) {
                    for (var tree : g.trees) {
                        if (e.x() == tree.y && e.y() == tree.x) {
                            wrong = true;
                        }
                    }
                }
                if (mutant == 2) {
                    wrong = true;
                }
                if (wrong != e.expected()) {
                    rejected = true;
                }
            }
            expect(rejected, "I089/F33: your examples did not reject " + names[mutant]);
        }
        g.verifyExamples();
        System.out.println("PASS " + checks + " checks");
    }
}
