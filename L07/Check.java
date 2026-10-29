import module java.base;


/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_CrazyEightsSimpleMauMau.java`
/// Kontroly volají testovací metody přímo v této JVM.
/// Hry zůstávají compact sources; následující kontroly k nim přistupují pomocí reflection.
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
        IO.println(checked + " missions; " + failed + " mission goals remaining.");
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

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M01_CrazyEightsSimpleMauMau");
        Reflect.set(g, "top", 0);
        Reflect.set(g, "suit", 0);
        expect(((boolean) Reflect.call(g, "legal", 20)), "L07.01/F09: an eight is wild even in a different suit");
        expect(((boolean) Reflect.call(g, "legal", 13)), "L07.01/F09: matching rank accepted");
        expect(((boolean) Reflect.call(g, "legal", 5)), "L07.01/F09: matching suit accepted");
        expect(!((boolean) Reflect.call(g, "legal", 15)), "L07.01/F09: unrelated non-wild card rejected");
        expect(!(boolean) Reflect.call(g, "legal", -1) && !(boolean) Reflect.call(g, "legal", 52), "invalid deck positions are rejected");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M02_CooperateOrDefect");
        equal(3, ((int) Reflect.call(g, "reward", true, true)), "L07.02/F09: mutual cooperation rewards both");
        equal(0, ((int) Reflect.call(g, "reward", true, false)), "L07.02/F09: betrayed cooperator earns zero");
        equal(5, ((int) Reflect.call(g, "reward", false, true)), "L07.02/F09: lone defector earns five");
        equal(1, ((int) Reflect.call(g, "reward", false, false)), "L07.02/F09: mutual defection earns one");
        Reflect.call(g, "reset");
        Reflect.call(g, "play", true, false);
        equal(new int[]{0, 5}, ((int[]) Reflect.get(g, "scores")), "L07.02/F09: both players use the same viewpoint rule");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_TrailDuelTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M03_TrailDuel");
        int[][] keys = {{java.awt.event.KeyEvent.VK_W, java.awt.event.KeyEvent.VK_UP}, {java.awt.event.KeyEvent.VK_D, java.awt.event.KeyEvent.VK_RIGHT}, {java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.VK_DOWN}, {java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.VK_LEFT}};
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int i = 0; i < keys.length; i++) {
            for (int key : keys[i]) {
                Object d = Reflect.call(g, "directionFor", key);
                expect(d != null && (int) Reflect.call(d, "dx") == directions[i][0] && (int) Reflect.call(d, "dy") == directions[i][1], "L07.03: both layouts turn their requested direction clockwise");
            }
        }
        expect(Reflect.call(g, "directionFor", java.awt.event.KeyEvent.VK_SPACE) == null, "L07.03: unrelated keys do not steer");
        Reflect.call(g, "restart");
        Reflect.call(g, "key", java.awt.event.KeyEvent.VK_W, true);
        Reflect.call(g, "restart");
        Reflect.call(g, "key", java.awt.event.KeyEvent.VK_D, true);
        expect((int) Reflect.get(g, "anx") == 0 && (int) Reflect.get(g, "any") == 1, "L07.03: real input uses the hacked direction");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M04_CarrotInABox");
        Reflect.set(g, "prizeA", true);
        equal(Reflect.get(Reflect.type("M04_CarrotInABox$Holder"), "PLAYER_TWO"), Reflect.call(g, "holderAfterSwap", true), "L07.04/F21: swapping changes the named holder");
        equal(Reflect.get(Reflect.type("M04_CarrotInABox$Holder"), "PLAYER_ONE"), Reflect.call(g, "holderAfterSwap", false), "L07.04/F21: keeping leaves prize with player one");
        Reflect.set(g, "prizeA", false);
        equal(Reflect.get(Reflect.type("M04_CarrotInABox$Holder"), "PLAYER_ONE"), Reflect.call(g, "holderAfterSwap", true), "L07.04/F21: opposite starting box also swaps");
        Reflect.set(g, "outcome", "");
        Reflect.call(g, "choose", false);
        expect(((java.lang.String) Reflect.get(g, "outcome")).contains("Player 2"), "L07.04/F21: actual reveal uses the enum result");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M05_TargetPop");
        Reflect.call(g, "restart");
        Reflect.call(g, "tick", 999);
        equal(Reflect.get(Reflect.type("M05_TargetPop$Phase"), "READY"), Reflect.get(g, "phase"), "L07.05/F21: idle game does not expire");
        Reflect.call(g, "click", ((int) Reflect.get(g, "targetX")), ((int) Reflect.get(g, "targetY")), 1000);
        equal(Reflect.get(Reflect.type("M05_TargetPop$Phase"), "RUNNING"), Reflect.get(g, "phase"), "L07.05/F21: first click starts round");
        Reflect.call(g, "tick", ((long) Reflect.get(g, "deadline")));
        equal(Reflect.get(Reflect.type("M05_TargetPop$Phase"), "FINISHED"), Reflect.get(g, "phase"), "L07.05/F21: exact deadline finishes instead of resetting");
        int hits = ((int) Reflect.get(g, "hits"));
        Reflect.call(g, "click", ((int) Reflect.get(g, "targetX")), ((int) Reflect.get(g, "targetY")), ((long) Reflect.get(g, "deadline")) + 1);
        equal(hits, ((int) Reflect.get(g, "hits")), "L07.05/F21: finished round rejects late clicks");
        Reflect.call(g, "restart");
        equal(Reflect.get(Reflect.type("M05_TargetPop$Phase"), "READY"), Reflect.get(g, "phase"), "L07.05/F21: explicit restart returns to ready");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M06_MiniPacman");
        equal(Reflect.get(Reflect.type("M06_MiniPacman$RoundState"), "CAUGHT"), Reflect.call(g, "afterContact", true, 0), "L07.06/F21: capture has priority over the last pellet");
        equal(Reflect.get(Reflect.type("M06_MiniPacman$RoundState"), "WON"), Reflect.call(g, "afterContact", false, 0), "L07.06/F21: empty safe maze wins");
        equal(Reflect.get(Reflect.type("M06_MiniPacman$RoundState"), "PLAY"), Reflect.call(g, "afterContact", false, 1), "L07.06/F21: unfinished round keeps playing");
        Reflect.call(g, "restart");
        Reflect.set(g, "playerX", 1);
        Reflect.set(g, "playerY", 1);
        Reflect.set(g, "ghostX", 2);
        Reflect.set(g, "ghostY", 1);
        Reflect.set(g, "remaining", 1);
        ((boolean[][]) Reflect.get(g, "pellets"))[1][2] = true;
        Reflect.call(g, "direction", 1, 0);
        Reflect.call(g, "step");
        equal(Reflect.get(Reflect.type("M06_MiniPacman$RoundState"), "CAUGHT"), Reflect.get(g, "state"), "L07.06/F21: real step uses terminal-state priority");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M07_GoFish");
        Reflect.set(g, "hands", java.util.List.of(new java.util.ArrayList<>(java.util.List.of(7, 7, 7, 7)), new java.util.ArrayList<>()));
        Reflect.set(g, "books", new int[2]);
        Reflect.call(g, "collect", 0);
        equal(0, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size(), "L07.07/F25: booking four cards removes exactly four");
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "L07.07/F25: one complete rank awards one book");
        equal(4, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size() + 4 * ((int[]) Reflect.get(g, "books"))[0], "L07.07/F25: card-plus-book value conserved");
        Reflect.call(g, "collect", 0);
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "L07.07/F25: collecting again cannot duplicate the book");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_MiniInvadersTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M08_MiniInvaders");
        Reflect.call(g, "restart");
        Reflect.set(g, "speedX", 0.0);
        Reflect.set(g, "fire", true);
        Reflect.call(g, "step");
        Reflect.set(g, "fire", false);
        Reflect.set(g, "bulletX", (double) Reflect.get(g, "formationX") + 10);
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 2 * 34 + 17);
        Reflect.call(g, "step");
        equal(1, Reflect.get(g, "score"), "L07.08: first real hit scores once");
        expect((boolean) Reflect.get(g, "bulletActive"), "L07.08: piercing pulse survives its first alien");
        Reflect.call(g, "hitAlien", 2, 0);
        equal(1, Reflect.get(g, "score"), "L07.08: an already removed alien cannot score again");
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 34 + 17);
        Reflect.call(g, "step");
        equal(2, Reflect.get(g, "score"), "L07.08: the same pulse reaches a second alien");
        expect(!(boolean) Reflect.get(g, "bulletActive"), "L07.08: the two-hit pulse ends after its second hit");
        Reflect.call(g, "hitAlien", 0, 0);
        equal(2, Reflect.get(g, "score"), "L07.08: no third score from a spent pulse");
        Reflect.set(g, "fire", true); Reflect.call(g, "step"); Reflect.set(g, "fire", false);
        Reflect.call(g, "hitAlien", 0, 0);
        expect((boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 3, "L07.08: a newly fired pulse receives a fresh allowance");
        Reflect.call(g, "hitAlien", 0, 1);
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 4, "L07.08: that new allowance is also finite");
        Reflect.call(g, "restart");
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 0 && (int) Reflect.call(g, "countAliens") == 15, "L07.08: restart resets the battle");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M09_CrossingRush");
        Reflect.call(g, "restart");
        Reflect.call(g, "hop", -1000, 0);
        equal(12, ((int) Reflect.get(g, "x")), "L07.09/F25: whole player remains inside left boundary");
        Reflect.call(g, "hop", 1000, 0);
        equal(588, ((int) Reflect.get(g, "x")), "L07.09/F25: whole player remains inside right boundary");
        ((java.util.ArrayList<Object>) Reflect.get(g, "cars")).clear();
        Reflect.call(g, "hop", 0, -500);
        expect(((boolean) Reflect.get(g, "won")), "L07.09/F25: legitimate top goal still wins");
        int x = ((int) Reflect.get(g, "x"));
        Reflect.call(g, "hop", -30, 0);
        equal(x, ((int) Reflect.get(g, "x")), "L07.09/F25: terminal round does not keep moving");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_SoftLandingTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M10_SoftLanding");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "fuel", 0);
        Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "L07.10: empty training tank still brakes against gravity");
        equal(0, Reflect.get(g, "fuel"), "L07.10: training leaves the counter unchanged at zero");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "left", true); Reflect.set(g, "right", true); Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        equal(240, Reflect.get(g, "fuel"), "L07.10: combined thrust does not spend training fuel");
        expect(Math.abs((double) Reflect.get(g, "velocityX") - 1.2) < 1e-9 && Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "L07.10: opposing engines cancel while vertical thrust works");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") - 0.04) < 1e-9, "L07.10: no key means gravity only");
        Reflect.set(g, "state", "CRASH");
        double y = (double) Reflect.get(g, "y");
        Reflect.set(g, "up", true); Reflect.call(g, "step");
        equal(y, Reflect.get(g, "y"), "L07.10: a crash remains terminal");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        expect(!(boolean) Reflect.get(g, "left") && !(boolean) Reflect.get(g, "right") && !(boolean) Reflect.get(g, "up"), "L07.10: restart releases thrust");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M11_TiltTray");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 168);
        Reflect.set(g, "y", 150);
        Reflect.set(g, "vx", 3);
        Reflect.set(g, "vy", 0);
        Reflect.call(g, "step");
        equal(170.0, ((double) Reflect.get(g, "x")), "L07.11/F25: collision separates the ball outside obstacle");
        expect(((double) Reflect.get(g, "vx")) < 0 && !((boolean) Reflect.call(g, "hit", ((java.awt.Rectangle[]) Reflect.get(g, "obstacles"))[0])), "L07.11/F25: reflected ball is not left penetrating");
        Reflect.call(g, "clearKeys");
        for (int n = 0; n < 100; n++) {
            Reflect.call(g, "step");
        }
        expect(Double.isFinite(((double) Reflect.get(g, "x"))) && Double.isFinite(((double) Reflect.get(g, "y"))), "L07.11/F25: later updates remain finite");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 530);
        Reflect.set(g, "y", 345);
        Reflect.set(g, "vx", ((double) Reflect.set(g, "vy", 0)));
        Reflect.call(g, "step");
        expect(((boolean) Reflect.get(g, "won")), "L07.11/F25: slow target entry still works");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M12_PlatformDash");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 285);
        Reflect.set(g, "vy", -4);
        Reflect.call(g, "step");
        expect(!((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "y")) < 285, "L07.12/F25: rising from below cannot snap onto platform top");
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 240);
        Reflect.set(g, "vy", 12);
        Reflect.call(g, "step");
        equal(252.0, ((double) Reflect.get(g, "y")), "L07.12/F25: descending top crossing still lands");
        expect(((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "vy")) == 0, "L07.12/F25: valid landing rests on top");
        Reflect.call(g, "restart");
        ((boolean[]) Reflect.get(g, "held"))[java.awt.event.KeyEvent.VK_RIGHT] = true;
        Reflect.call(g, "jump");
        for (int tick = 0; tick < 200 && !((boolean) Reflect.get(g, "won")); tick++) {
            Reflect.call(g, "step");
            if (((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "x")) < 380) {
                Reflect.call(g, "jump");
            }
        }
        expect(((boolean) Reflect.get(g, "won")) && ((int) Reflect.get(g, "falls")) == 0, "L07.12/F25: original two-jump course remains playable");
        System.out.println("PASS " + checks + " checks");
    }
}

// Připravený přístup ke compact programům. Testy používají běžné assertions, pole a JDK collections.
class Reflect {
    static Class<?> type(String name) {
        return switch (name) {
            case "boolean" -> boolean.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "char" -> char.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "float" -> float.class;
            case "double" -> double.class;
            case "void" -> void.class;
            default -> load(name);
        };
    }

    private static Class<?> load(String name) {
        try {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException missing) {
                int nested = name.indexOf('$');
                if (nested < 0) throw missing;
                Class.forName(name.substring(0, nested));
                return Class.forName(name);
            }
        } catch (ClassNotFoundException failure) {
            throw new IllegalStateException("Cannot find compact program type: " + name, failure);
        }
    }

    static Object create(String name, Object... args) {
        var matches = new ArrayList<Constructor<?>>();
        for (var constructor : type(name).getDeclaredConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) matches.add(constructor);
        }
        var constructor = one(matches, "constructor " + name);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot construct " + name, failure);
        }
    }

    static Object get(Object target, String name) {
        try {
            return field(target, name).get(target instanceof Class<?> ? null : target);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot read field " + name, failure);
        }
    }

    static Object set(Object target, String name, Object value) {
        try {
            var field = field(target, name);
            Object receiver = target instanceof Class<?> ? null : target;
            field.set(receiver, value);
            return field.get(receiver);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot write field " + name, failure);
        }
    }

    private static Field field(Object target, String name) throws NoSuchFieldException {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            try {
                var field = current.getDeclaredField(name);
                if (target instanceof Class<?> && !Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("An instance is required for field " + name);
                }
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException missing) {
                // Continue with inherited fields.
            }
        }
        throw new NoSuchFieldException(owner.getName() + "." + name);
    }

    static Object call(Object target, String name, Object... args) {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        var methods = new LinkedHashMap<String, Method>();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            for (var method : current.getDeclaredMethods()) {
                methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
            }
        }
        for (var method : owner.getMethods()) {
            methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
        }
        var matches = new ArrayList<Method>();
        for (var method : methods.values()) {
            if (!method.isBridge() && !method.isSynthetic()
                    && method.getName().equals(name) && accepts(method.getParameterTypes(), args)
                    && (!(target instanceof Class<?>) || Modifier.isStatic(method.getModifiers()))) {
                matches.add(method);
            }
        }
        var method = one(matches, "method " + owner.getName() + "." + name);
        try {
            method.setAccessible(true);
            return method.invoke(target instanceof Class<?> ? null : target, args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot call " + owner.getName() + "." + name, failure);
        }
    }

    static Object array(String component, Object... values) {
        Object result = Array.newInstance(type(component), values.length);
        for (int index = 0; index < values.length; index++) Array.set(result, index, values[index]);
        return result;
    }

    private static boolean accepts(Class<?>[] parameters, Object[] args) {
        if (parameters.length != args.length) return false;
        for (int index = 0; index < parameters.length; index++) {
            Class<?> to = parameters[index];
            Object value = args[index];
            if (value == null) {
                if (to.isPrimitive()) return false;
            } else if (to.isPrimitive()) {
                String targets = switch (value) {
                    case Boolean ignored -> "boolean";
                    case Byte ignored -> "byte short int long float double";
                    case Short ignored -> "short int long float double";
                    case Character ignored -> "char int long float double";
                    case Integer ignored -> "int long float double";
                    case Long ignored -> "long float double";
                    case Float ignored -> "float double";
                    case Double ignored -> "double";
                    default -> "";
                };
                if (!Arrays.asList(targets.split(" ")).contains(to.getName())) return false;
            } else if (!to.isInstance(value)) return false;
        }
        return true;
    }

    private static <T> T one(List<T> matches, String description) {
        if (matches.size() != 1) {
            throw new IllegalStateException((matches.isEmpty() ? "No compatible " : "Ambiguous ") + description);
        }
        return matches.getFirst();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException raise(Throwable failure) throws T {
        throw (T) failure;
    }
}
