// Funkční kontroly: pouze chování hry.
public class M02_MancalaKalahTest {
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
        var g = new M02_MancalaKalah();
        g.reset();
        g.player = 1;
        g.pits[6] = 20;
        g.reset();
        equal(0, g.player, "I012/F14: restart resets the shared active-player field");
        equal(0, g.pits[6], "I012/F14: stores belong to the old round");
        equal(24, g.side(0), "I012/F14: six pits regain four stones");
        g.command("3");
        equal(0, g.player, "I012/F14: finishing in own store grants another turn");
        System.out.println("PASS " + checks + " checks");
    }
}
