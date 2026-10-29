import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_Hangman.java`
/// Checks call the test methods directly in this JVM.
/// Games remain compact sources; the checks below access them through reflection.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_Hangman", "L04.01 Hangman · F10", () -> M01_HangmanTest.main(new String[0]));
        run("M02_Ghost", "L04.02 Ghost · F10", () -> M02_GhostTest.main(new String[0]));
        run("M03_Flood", "L04.03 Flood · F10", () -> M03_FloodTest.main(new String[0]));
        run("M04_SameGame", "L04.04 SameGame · F10", () -> M04_SameGameTest.main(new String[0]));
        run("M05_WordSearch", "L04.05 WordSearch · F11", () -> M05_WordSearchTest.main(new String[0]));
        run("M06_VirtualScribbler", "L04.06 VirtualScribbler · F11", () -> M06_VirtualScribblerTest.main(new String[0]));
        run("M07_KochSnowflakeCards", "L04.07 KochSnowflakeCards · F11", () -> M07_KochSnowflakeCardsTest.main(new String[0]));
        run("M08_DragonCurve", "L04.08 DragonCurve · F11", () -> M08_DragonCurveTest.main(new String[0]));
        run("M09_EvilHangman", "L04.09 EvilHangman · F12", () -> M09_EvilHangmanTest.main(new String[0]));
        run("M10_WordleStyleWordHunt", "L04.10 WordleStyleWordHunt · F12", () -> M10_WordleStyleWordHuntTest.main(new String[0]));
        run("M11_Jotto", "L04.11 Jotto · F12", () -> M11_JottoTest.main(new String[0]));
        run("M12_WordChain", "L04.12 WordChain · F12", () -> M12_WordChainTest.main(new String[0]));
        run("M13_YatzyScorecard", "L04.13 YatzyScorecard · F12", () -> M13_YatzyScorecardTest.main(new String[0]));
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
class M01_HangmanTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L04.01/F10: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M01_Hangman");
        String[][] samples = {{"BANANA", "", "A"}, {"BANANA", "A", "B"}, {"IDEA", "", "I"}, {"AUDIO", "A", "U"},
                {"BYE", "Y", "E"}, {"RHYTHM", "H", "R"}, {"METER", "E", "M"},
                {"EYE", "E", "Y"}, {"ALPHA", "ALPH", ""}, {"", "", ""}};
        for (String[] sample : samples) {
            Reflect.set(game, "word", sample[0]);
            Reflect.set(game, "tried", sample[1]);
            Reflect.set(game, "misses", 3);
            equal(sample[2], Reflect.call(game, "hintLetter"), "advice for " + Arrays.toString(sample));
            equal(sample[2], Reflect.call(game, "hintLetter"), "repeated advice is stable");
            equal(sample[0], Reflect.get(game, "word"), "advice preserves the secret");
            equal(sample[1], Reflect.get(game, "tried"), "advice is not a guess");
            equal(3, Reflect.get(game, "misses"), "advice costs no miss");
        }
        Reflect.set(game, "word", "BANANA");
        Reflect.set(game, "tried", "A");
        equal("_A_A_A", Reflect.call(game, "mask"), "ordinary word display remains available");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_GhostTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.02 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M02_Ghost");
        check(((boolean) Reflect.call(game, "possiblePrefix", "RO")), "the search must reach dictionary entries beyond the first word");
        check(((boolean) Reflect.call(game, "possiblePrefix", "GHO")), "the first matching word may return immediately");
        check(!((boolean) Reflect.call(game, "possiblePrefix", "XY")), "a missing prefix terminates with false");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "R");
        check(((java.lang.String) Reflect.get(game, "outcome")).isEmpty() && ((int) Reflect.get(game, "player")) == 2, "the actual legal R prefix must keep the game alive");
        Reflect.set(game, "words", new String[0]);
        check(!((boolean) Reflect.call(game, "possiblePrefix", "A")), "an empty dictionary terminates without access");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_FloodTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.03 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M03_Flood");
        Reflect.set(game, "board", new int[8][8]);
        ((int[][]) Reflect.get(game, "board"))[7][7] = 1;
        Reflect.call(game, "fill", 2);
        check(((int[][]) Reflect.get(game, "board"))[7][6] == 2, "the expanding queue must be processed beyond the first cell");
        int changed = 0;
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) { for (int value : row) { if (value == 2) changed++; } }
        check(changed == 63 && ((int[][]) Reflect.get(game, "board"))[7][7] == 1, "all and only the connected old colour must change");
        check(((int) Reflect.get(game, "moves")) == 1, "one completed flood counts as one move");
        Reflect.call(game, "spread", new ArrayList<>(), 0, 1);
        check(((int[][]) Reflect.get(game, "board"))[7][7] == 1, "an empty work queue performs no iteration");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_SameGameTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.04 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M04_SameGame");
        Reflect.set(game, "board", new int[5][5]);
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) Arrays.fill(row, 1);
        check(((java.util.List<int[]>) Reflect.call(game, "group", 0, 0)).size() == 25, "the queue loop must discover the full connected group");
        check(((java.util.List<int[]>) Reflect.call(game, "group", -1, 0)).isEmpty(), "an invalid start is rejected before queue access");
        Reflect.set(game, "outcome", "");
        Reflect.call(game, "command", "1 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).startsWith("Board clear") && ((int) Reflect.get(game, "score")) == 600, "the actual complete group can be removed to win");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_WordSearchTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L04.05/F11: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M05_WordSearch");
        for (String[] sample : new String[][]{{"TAC", "CAT"}, {"GOD", "DOG"}, {"REGIT", "TIGER"},
                {"CAT", "CAT"}, {"CAX", "CAX"}, {"", ""}}) {
            equal(sample[1], Reflect.call(game, "targetName", sample[0]), "target identity for " + sample[0]);
        }
        equal("TAC", Reflect.call(game, "read", 0, 2, 0, 0), "the raw path still preserves selection order");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "1 3 1 1");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "reverse selection finds the canonical target");
        boolean rejected = false;
        try {
            Reflect.call(game, "command", "1 1 1 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "the same word cannot be found again in its other direction");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "a duplicate changes no finds");
        Reflect.call(game, "command", "2 4 2 2");
        Reflect.call(game, "command", "3 5 3 1");
        equal(Set.of("CAT", "DOG", "TIGER"), Reflect.get(game, "found"), "all reverse targets are playable");
        equal("All words found!", Reflect.get(game, "outcome"), "real round ends after three distinct finds");
        Reflect.call(game, "reset");
        rejected = false;
        try {
            Reflect.call(game, "command", "4 1 4 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "reversing does not make an unrelated path a target");
        for (String invalid : new String[]{"1 1 2 3", "0 1 1 3", "1 1 1 1"}) {
            rejected = false;
            try {
                Reflect.call(game, "command", invalid);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }
            expect(rejected, "straight in-bounds multi-cell selection is still required: " + invalid);
            equal(Set.of(), Reflect.get(game, "found"), "invalid selections do not invent a find");
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_VirtualScribblerTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L04.06/F11: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M06_VirtualScribbler");
        Reflect.call(game, "restart");
        Reflect.set(game, "drift", 9);
        Reflect.set(game, "jitter", 10);
        Reflect.set(game, "x", 7.0);
        Reflect.call(game, "signature");
        int width = (int) Reflect.get(game, "WIDTH");
        int height = (int) Reflect.get(game, "HEIGHT");
        expect(Math.abs((double) Reflect.get(game, "x") - width / 2) < .00001,
                "the square returns to its starting horizontal position");
        expect(Math.abs((double) Reflect.get(game, "y") - height / 2) < .00001,
                "the square returns to its starting vertical position");
        equal(120, Reflect.get(game, "steps"), "four thirty-step sides draw 120 pen steps");
        equal(true, Reflect.get(game, "paused"), "the signature ends paused");
        equal(0, Reflect.get(game, "drift"), "straight sides have no drift");
        equal(0, Reflect.get(game, "jitter"), "repeated stamps have no random jitter");
        var image = (java.awt.image.BufferedImage) Reflect.get(game, "canvas");
        int cx = width / 2;
        int cy = height / 2;
        for (int[] point : new int[][]{{cx + 33, cy}, {cx + 66, cy + 33},
                {cx + 33, cy + 66}, {cx, cy + 33}}) {
            expect((image.getRGB(point[0], point[1]) & 0xffffff) != 0xffffff,
                    "each of the four square sides must actually draw");
        }
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        Reflect.set(game, "paused", false);
        Reflect.call(game, "burst", 11);
        Reflect.call(game, "signature");
        var again = (java.awt.image.BufferedImage) Reflect.get(game, "canvas");
        equal(pixels, again.getRGB(0, 0, width, height, null, 0, width), "a second signature resets earlier drawing and settings");
        Reflect.call(game, "oneShot", 1);
        equal(121, Reflect.get(game, "steps"), "ordinary step controls still use the working drawing model");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_KochSnowflakeCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.07 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M07_KochSnowflakeCards");
        var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
        var pen = image.createGraphics();
        check(((int) Reflect.call(game, "snowflake", pen, 0)) == 3, "the counted loop must draw all three base sides");
        boolean firstSide = false;
        for (int y = 215; y <= 221; y++) {
            for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
        }
        check(firstSide, "the actual first edge must be painted, not merely counted");
        check(((int) Reflect.call(game, "snowflake", pen, 1)) == 12, "three supplied four-segment edges give twelve segments");
        pen.dispose();
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_DragonCurveTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.08 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M08_DragonCurve");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 1)).equals(List.of(1)), "one requested fold must run the outer loop once");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 0)).isEmpty(), "zero folds must run zero times");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 2)).equals(List.of(1,1,-1)), "two folds use two outer passes");
        check(((java.util.List<java.awt.Point>) Reflect.call(game, "path", 3)).size() == 9, "the actual three-fold path has eight segments and nine points");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_EvilHangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.09 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M09_EvilHangman");
        check(((int) Reflect.call(game, "familySize", List.of("ALLY","COOL","GOOD"))) == 3, "the family count must accumulate across all words");
        check(((int) Reflect.call(game, "familySize", List.of())) == 0 && ((int) Reflect.call(game, "familySize", List.of("ALLY"))) == 1, "zero and one member are counted correctly");
        Reflect.call(game, "reset");
        Reflect.set(game, "pool", new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA")));
        Reflect.call(game, "command", "E");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "pool")).size() == 3 && ((java.lang.String) Reflect.get(game, "mask")).equals("____"), "the actual adversary must keep the largest consistent family");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_WordleStyleWordHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.10 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M10_WordleStyleWordHunt");
        Reflect.set(game, "secret", "LEVEL");
        char[] marks = ".....".toCharArray();
        int[] counts = ((int[]) Reflect.call(game, "leftoverLetters", "ALLEY", marks));
        check(counts['L'-'A'] == 2, "two unmatched L letters need an accumulated count of two");
        check(((java.lang.String) Reflect.call(game, "grade", "ALLEY")).equals(".YYG."), "both guessed L letters receive the actual remaining matches");
        check(((java.lang.String) Reflect.call(game, "grade", "LEVEL")).equals("GGGGG"), "exact matches stay green and consume no leftover count");
        check(((java.lang.String) Reflect.call(game, "grade", "STONE")).equals("....Y"), "an absent letter cannot gain a false match");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_JottoTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.11 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M11_Jotto");
        Reflect.set(game, "secret", "PLANT");
        check(((int) Reflect.call(game, "matches", "PLANT")) == 5, "all five shared letters must accumulate");
        check(((int) Reflect.call(game, "matches", "STONE")) == 2, "T and N are counted regardless of positions");
        check(((int) Reflect.call(game, "matches", "BRICK")) == 0, "no shared letters leaves zero");
        Reflect.call(game, "reset");
        Reflect.set(game, "secret", "PLANT");
        Reflect.call(game, "command", "STONE");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "history")).getFirst().contains("2 shared"), "the actual clue uses the repaired total");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M12_WordChainTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.12 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M12_WordChain");
        Reflect.call(game, "reset");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 20, "the accumulated choices must retain all initially legal words");
        Reflect.set(game, "last", "APPLE");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "all five E continuations survive the loop");
        ((java.util.Set<java.lang.String>) Reflect.get(game, "used")).add("EARTH");
        check(!((java.util.List<java.lang.String>) Reflect.call(game, "choices")).contains("EARTH") && ((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 4, "used words are excluded without losing other choices");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "APPLE");
        check(((java.lang.String) Reflect.get(game, "last")).equals("APPLE"), "the actual game accepts an early legal word, not only the final dictionary entry");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M13_YatzyScorecardTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L04.13 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M13_YatzyScorecard");
        Reflect.call(game, "reset");
        ((int[]) Reflect.get(game, "scores"))[0] = 1;
        ((int[]) Reflect.get(game, "scores"))[1] = 4;
        ((int[]) Reflect.get(game, "scores"))[2] = 9;
        check(((int) Reflect.call(game, "total")) == 14, "filled categories must accumulate rather than replace the running sum");
        Reflect.call(game, "reset");
        check(((int) Reflect.call(game, "total")) == 0, "unfilled negative entries contribute nothing");
        Reflect.set(game, "scores", new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1});
        check(((int) Reflect.call(game, "total")) == 98, "upper sum 63 adds the bonus exactly once");
        ((int[]) Reflect.get(game, "scores"))[5] = 17;
        check(((int) Reflect.call(game, "total")) == 62, "upper sum 62 earns no bonus");
        System.out.println("PASS " + checks + " checks");
    }
}

// Prepared access to compact programs. Tests keep ordinary assertions, arrays and JDK collections.
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
