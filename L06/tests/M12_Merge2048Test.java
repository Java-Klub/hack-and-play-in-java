// Functional checks: game behaviour only.
public class M12_Merge2048Test {
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
        var g = new M12_Merge2048();
        var cases = g.learnerChecks();
        for (var e : cases) {
            expect(e.input().length == 4 && e.expected().length == 4 && e.score() >= 0, "I018/F33: examples use four-cell rows and nonnegative scores");
            for (int value : e.input()) {
                expect(value == 0 || value >= 2 && value <= 2048 && (value&(value - 1)) == 0, "I018/F33: example inputs must be legal 2048 tile values");
            }
            g.score = 0;
            equal(e.expected(), g.merge(e.input().clone()), "I018/F33: real merge output");
            equal(e.score(), g.score, "I018/F33: real merge score");
        }
        String[] names = {"never-merge", "does-not-compress-before-merge", "chain-merges-new-tile", "drops-score", "only-first-merge"};
        for (int mutant = 0; mutant < 5; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                int[] values = mutant == 1 ? e.input().clone() : java.util.Arrays.stream(e.input()).filter(n -> n != 0).toArray();
                int[] out = new int[4];
                int at = 0, points = 0;
                for (int n = 0; n < values.length; n++) {
                    int value = values[n];
                    if (value == 0) {
                        continue;
                    }
                    if (mutant != 0 && (mutant != 4 || points == 0) && n + 1 < values.length && value == values[n + 1]) {
                        value *= 2;
                        points += value;
                        n++;
                        if (mutant == 2) {
                            while (n + 1 < values.length && value == values[n + 1]) {
                                value *= 2;
                                points += value;
                                n++;
                            }
                        }
                    }
                    out[at++] = value;
                }
                if (mutant == 3) {
                    points = 0;
                }
                if (!java.util.Arrays.equals(out, e.expected()) || points != e.score()) {
                    rejected = true;
                }
            }
            expect(rejected, "I018/F33: your examples did not reject " + names[mutant]);
        }
        g.verifyExamples();
        System.out.println("PASS " + checks + " checks");
    }
}
