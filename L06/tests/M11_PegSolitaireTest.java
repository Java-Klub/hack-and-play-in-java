// Functional checks: game behaviour only.
public class M11_PegSolitaireTest {
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
        var g = new M11_PegSolitaire();
        g.reset();
        var cases = g.learnerChecks();
        for (var e : cases) {
            equal(e.expected(), g.legal(e.row(), e.column(), e.toRow(), e.toColumn()), "I015/F33: expectations must accept actual legal()");
        }
        String[] names = {"missing-middle-peg", "allows-diagonal", "always-legal"};
        for (int mutant = 0; mutant < 3; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                int r = e.row(), c = e.column(), nr = e.toRow(), nc = e.toColumn();
                boolean inside = r >= 0 && c >= 0 && nr >= 0 && nc >= 0 && r < 5 && c < 5 && nr < 5 && nc < 5;
                boolean base = inside && Math.abs(nr - r) + Math.abs(nc - c) == 2 && g.board[r][c] == 1 && g.board[nr][nc] == 0;
                boolean wrong = switch (mutant) {
                    case 0 -> base && (r == nr || c == nc);
                    case 1 -> base && g.board[(r + nr) / 2][(c + nc) / 2] == 1;
                    default -> true;
                };
                if (wrong != e.expected()) {
                    rejected = true;
                }
            }
            expect(rejected, "I015/F33: your examples did not reject " + names[mutant]);
        }
        g.verifyExamples();
        System.out.println("PASS " + checks + " checks");
    }
}
