// Functional checks: game behaviour only.
public class M06_MiniPacmanTest {
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
        var g = new M06_MiniPacman();
        equal(M06_MiniPacman.RoundState.CAUGHT, g.afterContact(true, 0), "I049/F21: capture has priority over the last pellet");
        equal(M06_MiniPacman.RoundState.WON, g.afterContact(false, 0), "I049/F21: empty safe maze wins");
        equal(M06_MiniPacman.RoundState.PLAY, g.afterContact(false, 1), "I049/F21: unfinished round keeps playing");
        g.restart();
        g.playerX = 1;
        g.playerY = 1;
        g.ghostX = 2;
        g.ghostY = 1;
        g.remaining = 1;
        g.pellets[1][2] = true;
        g.direction(1, 0);
        g.step();
        equal(M06_MiniPacman.RoundState.CAUGHT, g.state, "I049/F21: real step uses terminal-state priority");
        System.out.println("PASS " + checks + " checks");
    }
}
