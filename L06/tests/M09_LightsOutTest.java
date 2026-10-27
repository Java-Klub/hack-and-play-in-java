// Functional checks: game behaviour only.
public class M09_LightsOutTest {
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
        var g = new M09_LightsOut();
        var cases = g.learnerChecks();
        for (var example : cases) {
            expect(example.row() >= 0 && example.row()<5 && example.column() >= 0 && example.column()<5,
            "I014/F33: examples must use actual game cells 0..4");
            g.lights = new boolean[5][5];
            g.flip(example.row(), example.column());
            int lit = 0;
            for (var row : g.lights) {
                for (boolean on : row) {
                    if (on) {
                        lit++;
                    }
                }
            }
            equal(example.expected(), lit, "I014/F33: your expectation must accept the real flip rule");
        }
        String[] names = {"always-five", "centre-only", "missing-centre", "edge-has-five"};
        for (int mutant = 0; mutant < 4; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                int correct = 1 + (e.row()>0 ? 1 : 0) + (e.row()<4 ? 1 : 0) + (e.column()>0 ? 1 : 0) + (e.column()<4 ? 1 : 0);
                int wrong = switch (mutant) {
                    case 0 -> 5;
                    case 1 -> 1;
                    case 2 -> correct - 1;
                    default -> (e.row() == 0 || e.row() == 4) && (e.column() == 0 || e.column() == 4) ? 3 : 5;
                };
                if (wrong != e.expected()) {
                    rejected = true;
                }
            }
            expect(rejected, "I014/F33: your examples did not reject " + names[mutant]);
        }
        g.verifyExamples();
        System.out.println("PASS " + checks + " checks");
    }
}
