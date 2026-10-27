// Functional checks: game behaviour only.
public class M09_ShikakuTest {
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
        var g = new M09_Shikaku();
        g.restart();
        expect(g.add(0, 0, 1, 2), "I094/F20: valid rectangle added");
        g.undo();
        equal(0, g.boxes.size(), "I094/F20: undo restores an independent list");
        g.add(0, 0, 1, 2);
        g.remove(0, 0);
        g.undo();
        equal(1, g.boxes.size(), "I094/F20: removed rectangle can be restored");
        var copy = g.snapshot();
        copy.clear();
        equal(1, g.boxes.size(), "I094/F20: list edits are not shared");
        expect(copy != g.boxes, "I094/F20: snapshot is a distinct mutable collection");
        System.out.println("PASS " + checks + " checks");
    }
}
