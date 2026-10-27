// Funkční kontroly: pouze chování hry.
public class M09_CrossingRushTest {
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
        var g = new M09_CrossingRush();
        g.restart();
        g.hop(-1000, 0);
        equal(12, g.x, "I062/F25: whole player remains inside left boundary");
        g.hop(1000, 0);
        equal(588, g.x, "I062/F25: whole player remains inside right boundary");
        g.cars.clear();
        g.hop(0, -500);
        expect(g.won, "I062/F25: legitimate top goal still wins");
        int x = g.x;
        g.hop(-30, 0);
        equal(x, g.x, "I062/F25: terminal round does not keep moving");
        System.out.println("PASS " + checks + " checks");
    }
}
