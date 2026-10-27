// Funkční kontroly: pouze chování hry.
public class M01_CrazyEightsSimpleMauMauTest {
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
        var g = new M01_CrazyEightsSimpleMauMau();
        g.top = 0;
        g.suit = 0;
        expect(g.legal(20), "I035/F09: an eight is wild even in a different suit");
        expect(g.legal(13), "I035/F09: matching rank accepted");
        expect(g.legal(5), "I035/F09: matching suit accepted");
        expect(!g.legal(15), "I035/F09: unrelated non-wild card rejected");
        System.out.println("PASS " + checks + " checks");
    }
}
