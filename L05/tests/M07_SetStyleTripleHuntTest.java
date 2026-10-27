// Funkční kontroly: pouze chování hry.
public class M07_SetStyleTripleHuntTest {
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
        var g = new M07_SetStyleTripleHunt();
        g.table = new java.util.ArrayList<>(java.util.List.of(0, 1, 2));
        expect(g.hasSet(), "I036/F17: the last card can complete the only triple");
        g.table = new java.util.ArrayList<>(java.util.List.of(0, 1, 3));
        expect(!g.hasSet(), "I036/F17: near miss is not a triple");
        g.table = new java.util.ArrayList<>(java.util.List.of(0, 1));
        expect(!g.hasSet(), "I036/F17: fewer than three cards cannot work");
        expect(g.triple(0, 1, 2), "I036/F17: supplied attribute rule still used");
        System.out.println("PASS " + checks + " checks");
    }
}
