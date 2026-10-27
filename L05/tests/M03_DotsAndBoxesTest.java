// Funkční kontroly: pouze chování hry.
public class M03_DotsAndBoxesTest {
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
        var g = new M03_DotsAndBoxes();
        equal(new int[]{0, 2, 6, 7}, g.box(0), "I007/F16: first box needs its four real edges");
        equal(new int[]{1, 3, 7, 8}, g.box(1), "I007/F16: neighbouring box shares one edge");
        equal(new int[]{3, 5, 10, 11}, g.box(3), "I007/F16: final box has different indices");
        g.reset();
        for (String edge : new String[]{"1", "3", "7", "8"}) {
            g.command(edge);
        }
        expect(g.owners[0] != 0, "I007/F16: closing a real box awards it");
        System.out.println("PASS " + checks + " checks");
    }
}
