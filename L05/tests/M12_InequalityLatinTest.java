// Funkční kontroly: pouze chování hry.
public class M12_InequalityLatinTest {
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
        var g = new M12_InequalityLatin();
        var rule = new M12_InequalityLatin.Rule(1, 0);
        int[][] values = {{4, 1, 3, 2}};
        expect(rule.satisfiedBy(values), "I093/F19: record coordinates identify the left and right cells");
        values[0][1] = 3;
        expect(!rule.satisfiedBy(values), "I093/F19: equality does not satisfy strict inequality");
        int[][] other = {{4, 1, 3, 2}, {1, 4, 2, 3}};
        expect(!new M12_InequalityLatin.Rule(0, 0).satisfiedBy(other), "I093/F19: changed x reads a different pair");
        expect(new M12_InequalityLatin.Rule(0, 1).satisfiedBy(other), "I093/F19: changed y reads a different row");
        expect(!new M12_InequalityLatin.Rule(1, 1).satisfiedBy(other), "I093/F19: record row and column cannot be ignored");
        expect(new M12_InequalityLatin.Rule(2, 1).satisfiedBy(other), "I093/F19: rightmost pair can satisfy the rule");
        g.restart();
        g.cells = new int[][]{{1, 2, 3, 4}, {2, 3, 4, 1}, {3, 4, 1, 2}, {4, 1, 2, 3}};
        expect(g.won(), "I093/F19: authored Latin square obeys all Rule records");
        g.rules = new M12_InequalityLatin.Rule[]{new M12_InequalityLatin.Rule(0, 3)};
        expect(!g.won(), "I093/F19: a valid Latin square can violate its actual Rule coordinates");
        System.out.println("PASS " + checks + " checks");
    }
}
