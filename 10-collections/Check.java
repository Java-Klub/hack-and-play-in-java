import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Kontrola herních úloh této kapitoly. Krátké učebnicové úkoly spouštěj samostatně.
class Check {
    int failed;
    int checked;
    String selected;
    final ArrayList<String> available = new ArrayList<>();
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) {
            IO.println("Zadej nejvýše jeden název herního souboru. Bez argumentu se zkontrolují všechny herní úlohy kapitoly.");
            System.exit(2);
        }
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P03_Task_Flood", () -> P03_Task_FloodTest.main(new String[0]));
        run("P04_Task_SameGame", () -> P04_Task_SameGameTest.main(new String[0]));
        run("P05_Task_DragonCurve", () -> P05_Task_DragonCurveTest.main(new String[0]));
        run("P06_Task_EvilHangman", () -> P06_Task_EvilHangmanTest.main(new String[0]));
        run("P07_Task_WordChain", () -> P07_Task_WordChainTest.main(new String[0]));
        run("P08_Task_Hex", () -> P08_Task_HexTest.main(new String[0]));
        run("P09_Task_SetStyleTripleHunt", () -> P09_Task_SetStyleTripleHuntTest.main(new String[0]));
        run("P10_Task_Shikaku", () -> P10_Task_ShikakuTest.main(new String[0]));
        run("P11_Task_GapHopper", () -> P11_Task_GapHopperTest.main(new String[0]));
        run("P12_Task_FlipbookStudio", () -> P12_Task_FlipbookStudioTest.main(new String[0]));
        run("P15_Task_Hangman", () -> P15_Task_HangmanTest.main(new String[0]));
        run("P16_Task_WordSearch", () -> P16_Task_WordSearchTest.main(new String[0]));
        run("P17_Task_TinyStepSequencer", () -> P17_Task_TinyStepSequencerTest.main(new String[0]));
        run("P18_Task_CirclePacking", () -> P18_Task_CirclePackingTest.main(new String[0]));
        run("P19_Task_WireframeCube", () -> P19_Task_WireframeCubeTest.main(new String[0]));
        run("P22_Task_TruchetTileLab", () -> P22_Task_TruchetTileLabTest.main(new String[0]));
        run("P23_Task_MarkovMischief", () -> P23_Task_MarkovMischiefTest.main(new String[0]));
        run("P24_Task_DungeonBlueprints", () -> P24_Task_DungeonBlueprintsTest.main(new String[0]));
        run("P27_Task_PhotoRemixLab", () -> P27_Task_PhotoRemixLabTest.main(new String[0]));
        run("P30_Task_Nonogram", () -> P30_Task_NonogramTest.main(new String[0]));
        if (checked == 0) {
            IO.println("Pro tento soubor zde není připravená herní kontrola: " + selected + ".java");
            IO.println("Vyber jeden z těchto souborů: " + String.join(", ", available));
            System.exit(2);
        }
        IO.println("Kontroly: " + checked + "; hotovo: " + (checked - failed) + "; zbývá: " + failed);
        if (failed > 0) System.exit(1);
    }

    void run(String game, Test test) {
        available.add(game + ".java");
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + game + ".java");
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + game + ".java");
            String reason = failure.getMessage();
            if (reason == null || reason.isBlank()) reason = "Kontrola nedostala očekávaný výsledek.";
            if (!(failure instanceof AssertionError)) reason = "Program se při kontrole zastavil: " + failure.getClass().getSimpleName() + ": " + reason;
            IO.println("  " + reason.replace("\n", "\n  "));
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            if (cause != failure) IO.println("  " + cause.getClass().getSimpleName() + ": " + show(cause.getMessage()));
            for (var frame : cause.getStackTrace()) {
                if (frame.getClassName().equals(game) || frame.getClassName().startsWith(game + "$")) {
                    IO.println("  " + "Místo v programu: " + frame.getFileName() + ":" + frame.getLineNumber());
                    break;
                }
            }
        }
    }

    static String show(Object value) {
        if (value != null && value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value);
            var parts = new ArrayList<String>();
            for (int i = 0; i < Math.min(length, 8); i++) parts.add(show(java.lang.reflect.Array.get(value, i)));
            return parts + (length > 8 ? " … (" + length + " položek" + ")" : "");
        }
        String text = String.valueOf(value).replace("\n", "\\n").replace("\r", "\\r");
        return text.length() <= 180 ? text : text.substring(0, 177) + "…";
    }
}

@SuppressWarnings("unchecked")
class P03_Task_FloodTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Task_Flood");
        Reflect.set(game, "board", new int[8][8]);
        ((int[][]) Reflect.get(game, "board"))[7][7] = 1;
        Reflect.call(game, "fill", 2);
        check(((int[][]) Reflect.get(game, "board"))[7][6] == 2, "spread musí pokračovat i přes nově objevené buňky; změnit barvu musí také spojená buňka u vzdáleného rohu.");
        int changed = 0;
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) { for (int value : row) { if (value == 2) changed++; } }
        check(changed == 63 && ((int[][]) Reflect.get(game, "board"))[7][7] == 1, "fill musí přebarvit všech 63 spojených buněk a zachovat jinak obarvený roh.");
        check(((int) Reflect.get(game, "moves")) == 1, "Jeden dokončený fill musí zvýšit moves přesně o jedna.");
        Reflect.call(game, "spread", new ArrayList<>(), 0, 1);
        check(((int[][]) Reflect.get(game, "board"))[7][7] == 1, "Volání spread s prázdným pracovním seznamem musí ponechat desku beze změny.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_SameGameTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P04_Task_SameGame");
        Reflect.set(game, "board", new int[5][5]);
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) Arrays.fill(row, 1);
        check(((java.util.List<int[]>) Reflect.call(game, "group", 0, 0)).size() == 25, "group(0, 0) musí na této jednobarevné desce najít všech 25 spojených buněk.");
        check(((java.util.List<int[]>) Reflect.call(game, "group", -1, 0)).isEmpty(), "group(-1, 0) musí vrátit prázdnou skupinu bez přístupu na neplatnou buňku.");
        Reflect.set(game, "outcome", "");
        Reflect.call(game, "command", "1 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).startsWith("Board clear") && ((int) Reflect.get(game, "score")) == 600, "Odebrání celé skupiny 25 buněk musí vyčistit desku a přiznat 600 bodů.");
    }
}

@SuppressWarnings("unchecked")
class P05_Task_DragonCurveTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P05_Task_DragonCurve");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 1)).equals(List.of(1)), "foldTurns(1) musí provést jeden přehyb a vrátit [1].");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 0)).isEmpty(), "foldTurns(0) nesmí provést žádný přehyb a musí vrátit prázdný seznam.");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 2)).equals(List.of(1,1,-1)), "foldTurns(2) musí provést dva přehyby a vrátit [1, 1, -1].");
        check(((java.util.List<java.awt.Point>) Reflect.call(game, "path", 3)).size() == 9, "path(3) musí po třech přehybech vytvořit devět bodů spojených osmi úseky.");
    }
}

@SuppressWarnings("unchecked")
class P06_Task_EvilHangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P06_Task_EvilHangman");
        check(((int) Reflect.call(game, "familySize", List.of("ALLY","COOL","GOOD"))) == 3, "familySize musí započítat všechna tři slova ALLY, COOL a GOOD, ne jen poslední slovo.");
        check(((int) Reflect.call(game, "familySize", List.of())) == 0 && ((int) Reflect.call(game, "familySize", List.of("ALLY"))) == 1, "familySize musí vrátit 0 pro prázdnou rodinu a 1 pro rodinu s jedním slovem.");
        Reflect.call(game, "reset");
        Reflect.set(game, "pool", new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA")));
        Reflect.call(game, "command", "E");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "pool")).size() == 3 && ((java.lang.String) Reflect.get(game, "mask")).equals("____"), "Po zkoušeném tipu musí protivník zachovat největší rodinu tří slov s maskou ____.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_WordChainTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_WordChain");
        Reflect.call(game, "reset");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 20, "choices musí zachovat všech 20 původně povolených slov v pořadí slovníku.");
        Reflect.set(game, "last", "APPLE");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "Po slovu končícím na E musí choices obsahovat EARTH, EAGLE, ELEPHANT, ECHO a ENGINE v tomto pořadí.");
        ((java.util.Set<java.lang.String>) Reflect.get(game, "used")).add("EARTH");
        check(!((java.util.List<java.lang.String>) Reflect.call(game, "choices")).contains("EARTH") && ((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 4, "Po použití EARTH je choices musí vynechat a zachovat ostatní čtyři pokračování na E.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "APPLE");
        check(((java.lang.String) Reflect.get(game, "last")).equals("APPLE"), "Skutečný command musí přijmout povolené APPLE, i když nejde o poslední slovo ve slovníku.");
    }
}

@SuppressWarnings("unchecked")
class P08_Task_HexTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P08_Task_Hex");
        Reflect.call(g, "reset");
        for (int r = 0; r < 5; r++) {
            ((int[][]) Reflect.get(g, "board"))[r][4 - r] = 1;
        }
        expect(((boolean) Reflect.call(g, "connected", 1)), "connected(1) musí poznat připravený šikmý řetězec jako úplnou cestu v hexové síti. Ověř všech šest směrů sousedství.");
        ((int[][]) Reflect.get(g, "board"))[2][2] = 0;
        expect(!((boolean) Reflect.call(g, "connected", 1)), "Odebrání buňky z tohoto řetězce musí přerušit spojení hráče 1.");
        Reflect.call(g, "reset");
        for (int c = 0; c < 5; c++) {
            ((int[][]) Reflect.get(g, "board"))[2][c] = 2;
        }
        expect(((boolean) Reflect.call(g, "connected", 2)), "connected(2) musí poznat cestu hráče 2 od levého okraje k pravému.");
        expect(!((boolean) Reflect.call(g, "connected", 1)), "Cesta hráče 1 nesmí procházet buňkami obsazenými hráčem 2.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_SetStyleTripleHuntTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P09_Task_SetStyleTripleHunt");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 2)));
        expect(((boolean) Reflect.call(g, "hasSet")), "hasSet musí najít jedinou platnou trojici, i když obsahuje poslední kartu na stole.");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 3)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "hasSet musí pro tuto skoro shodnou trojici vrátit false: ne každé tři karty splňují pravidlo vlastností.");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "hasSet musí vrátit false, když jsou dostupné méně než tři karty.");
        expect(((boolean) Reflect.call(g, "triple", 0, 1, 2)), "Připravené pravidlo triple(0, 1, 2) musí nadále poznat platnou kombinaci vlastností.");
    }
}

@SuppressWarnings("unchecked")
class P10_Task_ShikakuTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P10_Task_Shikaku");
        Reflect.call(g, "restart");
        expect(((boolean) Reflect.call(g, "add", 0, 0, 1, 2)), "add(0, 0, 1, 2) musí nadále přijmout připravený platný obdélník.");
        Reflect.set(g, "startX", 2);
        Reflect.set(g, "message", "Rejected: earlier drag");
        Reflect.call(g, "undo");
        equal(0, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "Undo po přidání prvního obdélníku musí obnovit prázdný seznam. Uložený seznam nesmí sdílet pozdější změny členství.");
        equal("Previous layout restored", ((java.lang.String) Reflect.get(g, "message")), "Undo musí po obnovení předchozího rozložení oznámit Previous layout restored.");
        equal(-1, ((int) Reflect.get(g, "startX")), "Undo musí vedle obnovení obdélníků zrušit i nedokončený výběr tažením.");
        Reflect.call(g, "add", 0, 0, 1, 2);
        Reflect.call(g, "remove", 0, 0);
        Reflect.call(g, "undo");
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "Undo po odebrání obdélníku musí tento obdélník obnovit.");
        var copy = ((java.util.ArrayList<Object>) Reflect.call(g, "snapshot"));
        copy.clear();
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "Vymazání seznamu vráceného snapshot nesmí vymazat živý seznam obdélníků.");
        expect(copy != ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")), "snapshot musí vrátit samostatný měnitelný seznam místo dalšího odkazu na boxes.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_GapHopperTest {
    static int checks;
    static final String[] CASES = {
        "recyclePipes musí odebrat první vyřazenou trubku a zachovat následující existující trubku.",
        "recyclePipes musí přidat novou trubku za nynější poslední s vynulovaným příznakem bodování; dosud nevyřazené trubky zůstanou.",
        "recyclePipes musí přijmout prázdný seznam trubek bez přidávání překážek nebo chyby."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P11_Task_GapHopper");
            Reflect.call(p, "restart"); var old = ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst(); var next = ((java.util.List<Object>) Reflect.get(p, "pipes")).getLast();
            Reflect.set(old, "x", -60); Reflect.set(next, "x", 200); Reflect.call(p, "recyclePipes");
            check(((java.util.List<Object>) Reflect.get(p, "pipes")).size()==2 && ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst()==next && !((java.util.List<Object>) Reflect.get(p, "pipes")).contains(old),0,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            check(((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x"))==460 && !((boolean) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "scored")),1,((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x")));
            var stable=List.copyOf(((java.util.List<Object>) Reflect.get(p, "pipes")));Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).equals(stable),1,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            ((java.util.List<Object>) Reflect.get(p, "pipes")).clear();Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).isEmpty(),2,((java.util.List<Object>) Reflect.get(p, "pipes")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P12_Task_FlipbookStudioTest {
    static int checks;
    static final String[] CASES = {
        "Dvě volání undo musejí obnovit dvě poslední původní barvy v opačném pořadí úprav a každou úpravu odebrat z historie.",
        "undo musí při prázdné historii vrátit false bez změny snímku.",
        "undo musí obnovit snímek a souřadnice uvedené v úpravě; restart musí vymazat původní historii."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P12_Task_FlipbookStudio");
            Reflect.call(p, "restart");Reflect.call(p, "paintCell", 2, 3, 1);Reflect.call(p, "paintCell", 2, 3, 4);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==1 && ((java.util.List<Object>) Reflect.get(p, "history")).size()==1,0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==0 && ((java.util.List<Object>) Reflect.get(p, "history")).isEmpty(),0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(!((boolean) Reflect.call(p, "undo")),1,((java.util.List<Object>) Reflect.get(p, "history")));
            Reflect.set(p, "edited", 2);Reflect.call(p, "paintCell", 1, 4, 3);Reflect.set(p, "edited", 0);Reflect.call(p, "undo");check(((int[][][]) Reflect.get(p, "frames"))[2][4][1]==0,2,((int[][][]) Reflect.get(p, "frames"))[2][4][1]);
            Reflect.call(p, "paintCell", 0, 0, 2);Reflect.call(p, "restart");check(((java.util.List<Object>) Reflect.get(p, "history")).isEmpty()&&!((boolean) Reflect.call(p, "undo")),2,((java.util.List<Object>) Reflect.get(p, "history")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P15_Task_HangmanTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P15_Task_Hangman");
        String[][] samples = {{"BANANA", "", "A"}, {"BANANA", "A", "B"}, {"IDEA", "", "I"}, {"AUDIO", "A", "U"},
                {"BYE", "Y", "E"}, {"RHYTHM", "H", "R"}, {"METER", "E", "M"},
                {"EYE", "E", "Y"}, {"ALPHA", "ALPH", ""}, {"", "", ""}};
        for (String[] sample : samples) {
            Reflect.set(game, "word", sample[0]);
            Reflect.set(game, "tried", sample[1]);
            Reflect.set(game, "misses", 3);
            equal(sample[2], Reflect.call(game, "hintLetter"), "hintLetter musí upřednostnit první skrytou samohlásku, jinak první skryté písmeno. Případ [slovo, zkoušená písmena, rada]: " + Arrays.toString(sample));
            equal(sample[2], Reflect.call(game, "hintLetter"), "Opakované hintLetter bez tahu musí vrátit stejnou radu.");
            equal(sample[0], Reflect.get(game, "word"), "Dotaz hintLetter nesmí změnit tajné slovo.");
            equal(sample[1], Reflect.get(game, "tried"), "Dotaz hintLetter nesmí přidat písmeno do tried; rada není tip.");
            equal(3, Reflect.get(game, "misses"), "Dotaz hintLetter musí zachovat počet chybných tipů, který je zde 3.");
        }
        Reflect.set(game, "word", "BANANA");
        Reflect.set(game, "tried", "A");
        equal("_A_A_A", Reflect.call(game, "mask"), "Běžná mask musí nadále ukázat _A_A_A pro BANANA po vyzkoušení A.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_WordSearchTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P16_Task_WordSearch");
        for (String[] sample : new String[][]{{"TAC", "CAT"}, {"GOD", "DOG"}, {"REGIT", "TIGER"},
                {"CAT", "CAT"}, {"CAX", "CAX"}, {"", ""}}) {
            equal(sample[1], Reflect.call(game, "targetName", sample[0]), "targetName musí vrátit stejné jméno cíle pro oba směry čtení a zachovat nesouvisející text. Vybraný text: " + sample[0]);
        }
        equal("TAC", Reflect.call(game, "read", 0, 2, 0, 0), "read musí zachovat zvolený směr: čtení CAT pozpátku vytvoří TAC.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "1 3 1 1");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "Obrácený výběr TAC musí uložit cíl CAT, nikoli nový cíl nazvaný TAC.");
        boolean rejected = false;
        try {
            Reflect.call(game, "command", "1 1 1 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "Opětovný nález CAT z druhého konce se musí odmítnout jako opakování.");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "Odmítnuté opakování musí ponechat ve found pouze CAT.");
        Reflect.call(game, "command", "2 4 2 2");
        Reflect.call(game, "command", "3 5 3 1");
        equal(Set.of("CAT", "DOG", "TIGER"), Reflect.get(game, "found"), "Obrácený výběr každého cíle musí najít CAT, DOG a TIGER právě jednou.");
        equal("All words found!", Reflect.get(game, "outcome"), "Po nálezu tří různých cílů musí skutečné kolo oznámit All words found!");
        Reflect.call(game, "reset");
        rejected = false;
        try {
            Reflect.call(game, "command", "4 1 4 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "Obrácení nesouvisející cesty z ní nesmí udělat platný cíl.");
        for (String invalid : new String[]{"1 1 2 3", "0 1 1 3", "1 1 1 1"}) {
            rejected = false;
            try {
                Reflect.call(game, "command", invalid);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }
            expect(rejected, "Výběr musí zůstat přímý, uvnitř desky a delší než jedna buňka. Odmítaný vstup: " + invalid);
            equal(Set.of(), Reflect.get(game, "found"), "Odmítnuté výběry musejí ponechat found prázdné místo vytvoření domnělého slova.");
        }
    }
}

@SuppressWarnings("unchecked")
class P17_Task_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Dvojí přepnutí jedné noty musí obnovit původní aktivní noty a zachovat ostatní.",
        "Přepnutí neaktivní platné buňky musí přidat právě jednu odpovídající Note a oznámit úspěch.",
        "Neplatné souřadnice noty se musejí odmítnout beze změny notes; prázdná množina not musí vytvořit ticho."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P17_Task_TinyStepSequencer");
            Reflect.call(p, "reset");var original=Set.copyOf(((java.util.Set<Object>) Reflect.get(p, "notes")));Reflect.call(p, "toggle", 0, 1);Reflect.call(p, "toggle", 0, 1);
            check(((java.util.Set<Object>) Reflect.get(p, "notes")).equals(original),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            ((java.util.Set<Object>) Reflect.get(p, "notes")).clear();check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).contains(Reflect.create("P17_Task_TinyStepSequencer$Note", 2, 4)),1,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(!((boolean) Reflect.call(p, "toggle", -1, 0))&&!((boolean) Reflect.call(p, "toggle", 4, 0))&&!((boolean) Reflect.call(p, "toggle", 0, 8))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),2,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(Arrays.stream(((double[]) Reflect.call(p, "sequence", 1))).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P18_Task_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "claimCentre musí přijmout nový střed a zapamatovat si jej pro další pokusy.",
        "claimCentre musí odmítnout opakovaný střed, ale při různých x a y rozlišovat středy (x, y) a (y, x).",
        "Skutečný pack musí nadále umět umístit osm různých kruhů. Ověř přijímání nových středů."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P18_Task_CirclePacking");
            Set<Object> seen=new HashSet<>();
            check(((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,0,seen);
            check(!((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,1,seen);
            check(((boolean) Reflect.call(p, "claimCentre", seen, 7, 4))&&seen.size()==2,1,seen);
            var bubbles=((java.util.List<Object>) Reflect.call(p, "pack", 42, 8));check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->Reflect.create("P18_Task_CirclePacking$Centre", (int)((double) Reflect.call(b, "x")), (int)((double) Reflect.call(b, "y")))).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P19_Task_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "uniqueEdges musí sloučit opakované i obrácené dvojice konců a v tomto příkladu zachovat dvě různé hrany.",
        "Každá vrácená Edge musí mít menší konec první a zachované hrany musejí dodržet pořadí prvního výskytu.",
        "uniqueEdges musí vynechat neplatné a stejné konce, přijmout prázdný vstup a zachovat všech dvanáct skutečných hran krychle."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P19_Task_WireframeCube");
            var edges=((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{0,1},{1,0},{0,1},{1,2}}));
            check(edges.size()==2,0,edges);
            check(edges.contains(Reflect.create("P19_Task_WireframeCube$Edge", 0, 1))&&edges.contains(Reflect.create("P19_Task_WireframeCube$Edge", 1, 2)),1,edges);
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{-1,0},{0,8},{2,2}})).isEmpty(),2,"invalid");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[0][2])).isEmpty(),2,"empty");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size()==12,0,((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P22_Task_TruchetTileLabTest {
    static int checks;
    static final String[] CASES = {
        "chooseOrientation musí vrátit první platnou preferenci a zvýšit počet pouze této orientace.",
        "Když preferovaná orientace už vede alespoň o dva, musí chooseOrientation vybrat a započítat druhou orientaci.",
        "Vykreslené dílky musejí použít vrácenou orientaci a neznámá preference se musí odmítnout beze změny obou počtů."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        var p = Reflect.create("P22_Task_TruchetTileLab");
        var counts = (Map<String, Integer>) Reflect.get(p, "orientationCounts");
        check(Reflect.call(p, "chooseOrientation", "LEFT").equals("LEFT") && counts.equals(Map.of("LEFT", 1)), 0, counts);
        Reflect.call(p, "chooseOrientation", "LEFT");
        check(Reflect.call(p, "chooseOrientation", "LEFT").equals("RIGHT") && counts.equals(Map.of("LEFT", 2, "RIGHT", 1)), 1, counts);
        Reflect.call(p, "render", 42, 12);
        int left = counts.getOrDefault("LEFT", 0), right = counts.getOrDefault("RIGHT", 0);
        check(left + right == 144 && Math.abs(left - right) <= 2, 2, counts);
        var before = new HashMap<>(counts);
        boolean rejected = false;
        try { Reflect.call(p, "chooseOrientation", "UP"); } catch (IllegalArgumentException invalid) { rejected = true; }
        check(rejected && counts.equals(before), 2, counts);
    }
}

@SuppressWarnings("unchecked")
class P23_Task_MarkovMischiefTest {
    static int checks;
    static final String[] CASES = {
        "Po učení z moon cat moon dog moon cat musejí být pokračování moon [cat, dog, cat] v tomto pořadí.",
        "train musí při dalším výskytu slova zachovat jeho dřívější pokračování včetně opakovaných.",
        "Text tvořený jedním slovem musí pro toto slovo vytvořit prázdný seznam pokračování; prázdný text se musí odmítnout."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P23_Task_MarkovMischief");
            var links=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "moon cat moon dog moon cat"));
            check(links.get("moon").equals(List.of("cat","dog","cat")),0,links);
            check(links.containsKey("cat")&&links.get("cat").equals(List.of("moon")),1,links);
            var single=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "solo"));check(single.containsKey("solo")&&single.get("solo").isEmpty(),2,single);
            reject(()->Reflect.call(p, "train", "   "),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P24_Task_DungeonBlueprintsTest {
    static int checks;
    static final String[] CASES = {
        "admitRoom musí plochu 42 zařadit jako SMALL a přijmout první tři místnosti SMALL.",
        "admitRoom musí odmítnout čtvrtou SMALL nebo třetí LARGE místnost beze změny počtu v obou kategoriích.",
        "Vytvořený dungeon musí obsahovat pouze přijaté místnosti a dodržet kvóty obou kategorií."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        var p = Reflect.create("P24_Task_DungeonBlueprints");
        var counts = (Map<String, Integer>) Reflect.get(p, "roomCounts");
        Object small = Reflect.create("P24_Task_DungeonBlueprints$Room", 1, 1, 6, 7);
        Object large = Reflect.create("P24_Task_DungeonBlueprints$Room", 1, 1, 7, 7);
        check((boolean) Reflect.call(p, "admitRoom", small), 0, counts);
        check((boolean) Reflect.call(p, "admitRoom", small) && (boolean) Reflect.call(p, "admitRoom", small), 0, counts);
        check(!(boolean) Reflect.call(p, "admitRoom", small) && counts.get("SMALL") == 3, 1, counts);
        check((boolean) Reflect.call(p, "admitRoom", large) && (boolean) Reflect.call(p, "admitRoom", large), 1, counts);
        var before = new HashMap<>(counts);
        check(!(boolean) Reflect.call(p, "admitRoom", large) && counts.equals(before), 1, counts);
        Reflect.call(p, "generate", 42, 8);
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        check(total > 0 && total <= 5 && counts.getOrDefault("SMALL", 0) <= 3 && counts.getOrDefault("LARGE", 0) <= 2, 2, counts);
    }
}

@SuppressWarnings("unchecked")
class P27_Task_PhotoRemixLabTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P27_Task_PhotoRemixLab");
        var counts = (Map<Integer, Integer>) Reflect.get(g, "colourCounts");
        counts.put(-5, 2); counts.put(-7, 4); counts.put(-9, 2); counts.put(-11, 0);
        var before = new HashMap<>(counts);
        equal(-9, Reflect.call(g, "spotlightColour"), "spotlightColour musí vybrat nejmenší kladnou četnost; při shodě rozhoduje menší RGB klíč včetně znaménka.");
        equal(before, counts, "Volání spotlightColour nesmí změnit histogram barev.");
        counts.clear(); equal(0, Reflect.call(g, "spotlightColour"), "spotlightColour musí při prázdném histogramu vrátit 0.");
        counts.put(7, Integer.MAX_VALUE); equal(7, Reflect.call(g, "spotlightColour"), "Jediná barva s četností Integer.MAX_VALUE je stále přípustná a musí být vybrána.");
        Reflect.call(g, "restart");
        var result = (java.awt.image.BufferedImage) Reflect.get(g, "result");
        equal(java.awt.Color.CYAN.getRGB(), result.getRGB(35, 40), "Skutečný zvýrazněný obrázek musí ukázat skrytý klíč azurově včetně jeho bodu (35, 40).");
        expect(result.getRGB(200, 150) != java.awt.Color.CYAN.getRGB(), "Častější měsíc nesmí dostat azurové zvýraznění místo vzácného klíče.");
    }
}

@SuppressWarnings("unchecked")
class P30_Task_NonogramTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P30_Task_Nonogram");
        equal(new int[]{3}, ((int[]) Reflect.call(g, "runs", new int[]{0, 1, 1, 1})), "runs([0, 1, 1, 1]) musí vrátit [3] a zachovat černou skupinu sahající až do poslední buňky.");
        equal(new int[]{2, 1}, ((int[]) Reflect.call(g, "runs", new int[]{1, 1, 0, 1})), "runs([1, 1, 0, 1]) musí vrátit [2, 1] a zachovat obě oddělené černé skupiny.");
        equal(new int[]{}, ((int[]) Reflect.call(g, "runs", new int[]{0, 0, 0})), "runs musí pro celý bílý řádek vrátit prázdné pole, nikoli skupinu nulové délky.");
        equal(new int[]{1}, ((int[]) Reflect.call(g, "runs", new int[]{1})), "runs([1]) musí pro jedinou černou buňku vrátit [1].");
    }
}

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
            throw new IllegalStateException("Nelze načíst program. Ověř jeho soubor vedle Check.java: " + name, failure);
        }
    }

    static Object create(String name, Object... args) {
        var matches = new ArrayList<Constructor<?>>();
        for (var constructor : type(name).getDeclaredConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) matches.add(constructor);
        }
        var constructor = one(matches, "vytvoření objektu " + name);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze vytvořit objekt programu: " + name, failure);
        }
    }

    static Object get(Object target, String name) {
        try {
            return field(target, name).get(target instanceof Class<?> ? null : target);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze přečíst proměnnou: " + name, failure);
        }
    }

    static Object set(Object target, String name, Object value) {
        try {
            var field = field(target, name);
            Object receiver = target instanceof Class<?> ? null : target;
            field.set(receiver, value);
            return field.get(receiver);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze změnit proměnnou: " + name, failure);
        }
    }

    private static Field field(Object target, String name) throws NoSuchFieldException {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            try {
                var field = current.getDeclaredField(name);
                if (target instanceof Class<?> && !Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("Pro tuto proměnnou je potřeba objekt: " + name);
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
        var method = one(matches, "metodu " + owner.getName() + "." + name);
        try {
            method.setAccessible(true);
            return method.invoke(target instanceof Class<?> ? null : target, args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze zavolat metodu: " + owner.getName() + "." + name, failure);
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
            throw new IllegalStateException((matches.isEmpty() ? "Nenalezeno odpovídající " : "Nalezeno více možností pro ") + description);
        }
        return matches.getFirst();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException raise(Throwable failure) throws T {
        throw (T) failure;
    }

    static Object function(String typeName, java.util.function.Function<Object[], Object> body) {
        Class<?> role = type(typeName);
        return java.lang.reflect.Proxy.newProxyInstance(role.getClassLoader(), new Class<?>[]{role},
                (proxy, method, arguments) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" -> proxy == arguments[0];
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "toString" -> "Function(" + typeName + ")";
                            default -> throw new AssertionError(method.getName());
                        };
                    }
                    Object result = body.apply(arguments == null ? new Object[0] : arguments);
                    // All four course roles return double; a constant lambda may box an int.
                    if (method.getReturnType() == double.class) return ((Number) result).doubleValue();
                    return result;
                });
    }
}
