import module java.base;


/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_CrazyEightsSimpleMauMau.java`
/// Kontroly volají testovací metody přímo v této JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_CrazyEightsSimpleMauMau", "L07.01 CrazyEightsSimpleMauMau · F09", () -> M01_CrazyEightsSimpleMauMauTest.main(new String[0]));
        run("M02_CooperateOrDefect", "L07.02 CooperateOrDefect · F09", () -> M02_CooperateOrDefectTest.main(new String[0]));
        run("M03_TrailDuel", "L07.03 TrailDuel · F09", () -> M03_TrailDuelTest.main(new String[0]));
        run("M04_CarrotInABox", "L07.04 CarrotInABox · F21", () -> M04_CarrotInABoxTest.main(new String[0]));
        run("M05_TargetPop", "L07.05 TargetPop · F21", () -> M05_TargetPopTest.main(new String[0]));
        run("M06_MiniPacman", "L07.06 MiniPacman · F21", () -> M06_MiniPacmanTest.main(new String[0]));
        run("M07_GoFish", "L07.07 GoFish · F25", () -> M07_GoFishTest.main(new String[0]));
        run("M08_MiniInvaders", "L07.08 MiniInvaders · F25", () -> M08_MiniInvadersTest.main(new String[0]));
        run("M09_CrossingRush", "L07.09 CrossingRush · F25", () -> M09_CrossingRushTest.main(new String[0]));
        run("M10_SoftLanding", "L07.10 SoftLanding · F25", () -> M10_SoftLandingTest.main(new String[0]));
        run("M11_TiltTray", "L07.11 TiltTray · F25", () -> M11_TiltTrayTest.main(new String[0]));
        run("M12_PlatformDash", "L07.12 PlatformDash · F25", () -> M12_PlatformDashTest.main(new String[0]));
        if (checked == 0) throw new IllegalArgumentException("Unknown game: " + selected);
        IO.println(checked + " missions; " + failed + " still need repair.");
        if (failed > 0) System.exit(1);
    }

    void run(String game, String label, Test test) {
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + label);
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + label + ": " + failure.getMessage());
        }
    }
}

class M01_CrazyEightsSimpleMauMauTest {
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

class M02_CooperateOrDefectTest {
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
        var g = new M02_CooperateOrDefect();
        equal(3, g.reward(true, true), "I044/F09: mutual cooperation rewards both");
        equal(0, g.reward(true, false), "I044/F09: betrayed cooperator earns zero");
        equal(5, g.reward(false, true), "I044/F09: lone defector earns five");
        equal(1, g.reward(false, false), "I044/F09: mutual defection earns one");
        g.reset();
        g.play(true, false);
        equal(new int[]{0, 5}, g.scores, "I044/F09: both players use the same viewpoint rule");
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_TrailDuelTest {
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
        var g = new M03_TrailDuel();
        equal(new M03_TrailDuel.Direction(0, -1), g.directionFor(java.awt.event.KeyEvent.VK_W), "I061/F09: W maps to upward movement");
        equal(new M03_TrailDuel.Direction(0, -1), g.directionFor(java.awt.event.KeyEvent.VK_UP), "I061/F09: both players share direction values");
        equal(new M03_TrailDuel.Direction(-1, 0), g.directionFor(java.awt.event.KeyEvent.VK_LEFT), "I061/F09: left direction");
        equal(null, g.directionFor(java.awt.event.KeyEvent.VK_F1), "I061/F09: unrelated key has no direction");
        g.restart();
        g.key(java.awt.event.KeyEvent.VK_W, true);
        g.step();
        equal(9, g.ay, "I061/F09: actual cyan trail turns up");
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_CarrotInABoxTest {
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
        var g = new M04_CarrotInABox();
        g.prizeA = true;
        equal(M04_CarrotInABox.Holder.PLAYER_TWO, g.holderAfterSwap(true), "I045/F21: swapping changes the named holder");
        equal(M04_CarrotInABox.Holder.PLAYER_ONE, g.holderAfterSwap(false), "I045/F21: keeping leaves prize with player one");
        g.prizeA = false;
        equal(M04_CarrotInABox.Holder.PLAYER_ONE, g.holderAfterSwap(true), "I045/F21: opposite starting box also swaps");
        g.outcome = "";
        g.choose(false);
        expect(g.outcome.contains("Player 2"), "I045/F21: actual reveal uses the enum result");
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_TargetPopTest {
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
        var g = new M05_TargetPop();
        g.restart();
        g.tick(999);
        equal(M05_TargetPop.Phase.READY, g.phase, "I060/F21: idle game does not expire");
        g.click(g.targetX, g.targetY, 1000);
        equal(M05_TargetPop.Phase.RUNNING, g.phase, "I060/F21: first click starts round");
        g.tick(g.deadline);
        equal(M05_TargetPop.Phase.FINISHED, g.phase, "I060/F21: exact deadline finishes instead of resetting");
        int hits = g.hits;
        g.click(g.targetX, g.targetY, g.deadline + 1);
        equal(hits, g.hits, "I060/F21: finished round rejects late clicks");
        g.restart();
        equal(M05_TargetPop.Phase.READY, g.phase, "I060/F21: explicit restart returns to ready");
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_MiniPacmanTest {
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

class M07_GoFishTest {
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
        var g = new M07_GoFish();
        g.hands = java.util.List.of(new java.util.ArrayList<>(java.util.List.of(7, 7, 7, 7)), new java.util.ArrayList<>());
        g.books = new int[2];
        g.collect(0);
        equal(0, g.hands.get(0).size(), "I034/F25: booking four cards removes exactly four");
        equal(1, g.books[0], "I034/F25: one complete rank awards one book");
        equal(4, g.hands.get(0).size() + 4 * g.books[0], "I034/F25: card-plus-book value conserved");
        g.collect(0);
        equal(1, g.books[0], "I034/F25: collecting again cannot duplicate the book");
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_MiniInvadersTest {
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
        var g = new M08_MiniInvaders();
        g.restart();
        g.speedX = 0;
        g.bulletActive = true;
        g.bulletX = g.formationX + 10;
        g.bulletY = g.formationY + 17;
        g.step();
        expect(!g.bulletActive, "I050/F25: a hitting bullet is consumed");
        equal(1, g.score, "I050/F25: one bullet awards exactly one hit");
        equal(14, g.countAliens(), "I050/F25: one alien removed");
        g.step();
        equal(14, g.countAliens(), "I050/F25: consumed bullet cannot hit another row");
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_CrossingRushTest {
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

class M10_SoftLandingTest {
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
        var g = new M10_SoftLanding();
        g.restart();
        g.state = "FLYING";
        g.fuel = 0;
        g.up = true;
        g.step();
        equal(0, g.fuel, "I064/F25: fuel never becomes negative");
        expect(Math.abs(g.velocityY-.04)<1e-9, "I064/F25: empty tank gives no thrust");
        g.restart();
        g.state = "FLYING";
        g.left = g.right = g.up = true;
        g.step();
        equal(239, g.fuel, "I064/F25: combined controls consume one unit per tick");
        g.restart();
        expect(!g.left && !g.right && !g.up, "I064/F25: restart releases controls");
        System.out.println("PASS " + checks + " checks");
    }
}

class M11_TiltTrayTest {
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
        var g = new M11_TiltTray();
        g.restart();
        g.started = true;
        g.x = 168;
        g.y = 150;
        g.vx = 3;
        g.vy = 0;
        g.step();
        equal(170.0, g.x, "I067/F25: collision separates the ball outside obstacle");
        expect(g.vx < 0 && !g.hit(g.obstacles[0]), "I067/F25: reflected ball is not left penetrating");
        g.clearKeys();
        for (int n = 0; n < 100; n++) {
            g.step();
        }
        expect(Double.isFinite(g.x) && Double.isFinite(g.y), "I067/F25: later updates remain finite");
        g.restart();
        g.started = true;
        g.x = 530;
        g.y = 345;
        g.vx = g.vy = 0;
        g.step();
        expect(g.won, "I067/F25: slow target entry still works");
        System.out.println("PASS " + checks + " checks");
    }
}

class M12_PlatformDashTest {
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
        var g = new M12_PlatformDash();
        g.restart();
        g.started = true;
        g.x = 240;
        g.y = 285;
        g.vy = -4;
        g.step();
        expect(!g.grounded && g.y < 285, "I063/F25: rising from below cannot snap onto platform top");
        g.x = 240;
        g.y = 240;
        g.vy = 12;
        g.step();
        equal(252.0, g.y, "I063/F25: descending top crossing still lands");
        expect(g.grounded && g.vy == 0, "I063/F25: valid landing rests on top");
        g.restart();
        g.held[java.awt.event.KeyEvent.VK_RIGHT] = true;
        g.jump();
        for (int tick = 0; tick < 200 && !g.won; tick++) {
            g.step();
            if (g.grounded && g.x < 380) {
                g.jump();
            }
        }
        expect(g.won && g.falls == 0, "I063/F25: original two-jump course remains playable");
        System.out.println("PASS " + checks + " checks");
    }
}
