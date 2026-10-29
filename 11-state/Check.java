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
        run("P03_Task_Pig", () -> P03_Task_PigTest.main(new String[0]));
        run("P04_Task_MancalaKalah", () -> P04_Task_MancalaKalahTest.main(new String[0]));
        run("P05_Task_SlidingTiles", () -> P05_Task_SlidingTilesTest.main(new String[0]));
        run("P06_Task_MiniCrossword", () -> P06_Task_MiniCrosswordTest.main(new String[0]));
        run("P09_Task_ConnectFour", () -> P09_Task_ConnectFourTest.main(new String[0]));
        run("P12_Task_MemoryPairs", () -> P12_Task_MemoryPairsTest.main(new String[0]));
        run("P13_Task_Sokoban", () -> P13_Task_SokobanTest.main(new String[0]));
        run("P14_Task_TrafficJamEscape", () -> P14_Task_TrafficJamEscapeTest.main(new String[0]));
        run("P15_Task_FallingBlocks", () -> P15_Task_FallingBlocksTest.main(new String[0]));
        run("P16_Task_GoFish", () -> P16_Task_GoFishTest.main(new String[0]));
        run("P19_Task_ChaosGame", () -> P19_Task_ChaosGameTest.main(new String[0]));
        run("P22_Task_VoronoiStainedGlass", () -> P22_Task_VoronoiStainedGlassTest.main(new String[0]));
        run("P23_Task_MandelbrotPostcards", () -> P23_Task_MandelbrotPostcardsTest.main(new String[0]));
        run("P24_Task_ColourFilterSecrets", () -> P24_Task_ColourFilterSecretsTest.main(new String[0]));
        run("P25_Task_TwoNoisyPictures", () -> P25_Task_TwoNoisyPicturesTest.main(new String[0]));
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
class P03_Task_PigTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Task_Pig");
        Reflect.call(game, "reset");
        Reflect.set(game, "turnTotal", 9);
        Reflect.call(game, "roll", 1);
        check(((int) Reflect.get(game, "turnTotal")) == 0 && ((int) Reflect.get(game, "player")) == 1, "roll(1) musí zahodit součet nynějšího tahu a předat kostku druhému hráči.");
        Reflect.call(game, "reset");
        Reflect.call(game, "roll", 6);
        check(((int) Reflect.get(game, "turnTotal")) == 6 && ((int) Reflect.get(game, "player")) == 0, "roll(6) musí přičíst šest k součtu nynějšího tahu bez předání hry.");
        check(!((boolean) Reflect.call(game, "roll", 0)) && ((int) Reflect.get(game, "turnTotal")) == 6, "roll(0) musí odmítnout neplatnou kostku beze změny nasbíraných šesti bodů.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_MancalaKalahTest {
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
        var g = Reflect.create("P04_Task_MancalaKalah");
        Reflect.call(g, "reset");
        Reflect.set(g, "player", 1);
        ((int[]) Reflect.get(g, "pits"))[6] = 20;
        Reflect.call(g, "reset");
        equal(0, ((int) Reflect.get(g, "player")), "reset musí znovu začít hráčem 1, i když byl naposledy na tahu hráč 2.");
        equal(0, ((int[]) Reflect.get(g, "pits"))[6], "reset musí vyprázdnit bodovací zásobníky; jejich body patří předchozímu kolu.");
        equal(24, ((int) Reflect.call(g, "side", 0)), "reset musí vrátit čtyři kamínky do každé ze šesti malých jamek hráče 1, celkem 24.");
        Reflect.call(g, "command", "3");
        equal(0, ((int) Reflect.get(g, "player")), "Tah končící ve vlastním zásobníku musí nadále přiznat stejnému hráči další tah.");
    }
}

@SuppressWarnings("unchecked")
class P05_Task_SlidingTilesTest {
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
        var g = Reflect.create("P05_Task_SlidingTiles");
        Reflect.set(g, "moves", 17);
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "Po vytvoření a zamíchání nové hádanky musí reset ponechat počet hráčových tahů na 0.");
        int[] sorted = ((int[]) Reflect.get(g, "tiles")).clone();
        java.util.Arrays.sort(sorted);
        equal(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, sorted, "Míchání musí zachovat každou hodnotu kamenů 0–8 právě jednou včetně prázdného místa.");
        int chosen = -1;
        for (int n = 0; n < 9; n++) {
            if (((boolean) Reflect.call(g, "canMove", n))) {
                chosen = n;
                break;
            }
        }
        expect(((boolean) Reflect.call(g, "play", chosen)) && ((int) Reflect.get(g, "moves")) == 1, "První platný posun po přípravě musí zvýšit počet tahů z 0 na 1.");
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "Každý nový reset musí znovu vynulovat počet tahů včetně tahů provedených při jeho míchání.");
    }
}

@SuppressWarnings("unchecked")
class P06_Task_MiniCrosswordTest {
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
        var g = Reflect.create("P06_Task_MiniCrossword");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "2 ARE");
        Reflect.call(g, "command", "3 TEN");
        expect(((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Vyplnění pouze tří nápověd nesmí dokončit křížovku se šesti nápovědami. command musí znovu spočítat nynější odpovědi, ne přičítat staré počty.");
        Reflect.call(g, "command", "4 CAT");
        Reflect.call(g, "command", "5 ARE");
        Reflect.call(g, "command", "6 TEN");
        expect(!((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Vyplnění všech šesti správných odpovědí musí dokončit křížovku.");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "erase 1");
        expect(!((boolean[]) Reflect.get(g, "solved"))[0] && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Vymazání první odpovědi musí zrušit její příznak solved a ponechat křížovku nedokončenou.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_ConnectFourTest {
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
        var game = Reflect.create("P09_Task_ConnectFour");
        char expectedOpener = 'X';
        for (int length : new int[]{0, 1, 4, 2, 0}) {
            Reflect.call(game, "reset");
            equal(expectedOpener, Reflect.get(game, "player"), "Začínající hráči kol se musejí střídat X, O, X, O bez ohledu na počet tahů předchozího kola.");
            equal(0, Reflect.get(game, "moves"), "Zahájení odvety musí nastavit moves na 0.");
            equal('.', Reflect.get(game, "winner"), "Zahájení odvety musí vymazat předchozího vítěze.");
            for (char[] row : (char[][]) Reflect.get(game, "board")) {
                for (char cell : row) equal('.', cell, "Zahájení odvety musí odstranit všechny kameny z předchozí desky.");
            }
            for (int n = 0; n < length; n++) {
                expect((boolean) Reflect.call(game, "drop", n + 1), "Platné spouštění kamenů musí zůstat povolené i po změně střídání začínajícího hráče.");
            }
            expectedOpener = expectedOpener == 'X' ? 'O' : 'X';
        }
        Reflect.call(game, "reset");
        equal(expectedOpener, Reflect.get(game, "player"), "Další reset musí dodržet střídání začínajícího hráče, ne převzít hráče zbývajícího na tahu.");
        for (int column : new int[]{1, 2, 1, 2, 1, 2, 1}) Reflect.call(game, "drop", column);
        equal(expectedOpener, Reflect.get(game, "winner"), "Určený začínající hráč musí nadále umět vyhrát vytvořením řady čtyř kamenů.");
        Reflect.call(game, "reset");
        char next = expectedOpener == 'X' ? 'O' : 'X';
        equal(next, Reflect.get(game, "player"), "Po dokončeném vítězství musí odvetu začít druhý hráč podle určeného střídání.");
        expect(!((boolean) Reflect.call(game, "drop", 0)), "drop(0) musí odmítnout neplatný sloupec. Hráčské sloupce začínají jedničkou.");
        equal(next, Reflect.get(game, "player"), "Odmítnutí sloupce 0 musí zachovat hráče, který má být nyní na tahu.");
        Reflect.call(game, "drop", 7);
        equal(next, ((char[][]) Reflect.get(game, "board"))[5][6], "První kámen nového kola musí patřit určenému začínajícímu hráči, a to i v posledním sloupci.");
    }
}

@SuppressWarnings("unchecked")
class P12_Task_MemoryPairsTest {
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
        var game = Reflect.create("P12_Task_MemoryPairs");
        Reflect.call(game, "reset", 12L);
        int[] cards = {4, 2, 6, 3, 1, 5, 3, 6, 2, 5, 4, 1};
        Reflect.set(game, "cards", cards.clone());
        Reflect.set(game, "xray", true);
        equal(0, Reflect.call(game, "visible", 0), "Při zapnutém rentgenu bez vybrané karty nesmí visible odhalit nesouvisející skryté karty.");
        Reflect.call(game, "play", 4);
        equal(false, Reflect.get(game, "xray"), "Přijatý výběr karty musí vypnout předchozí dodatečný rentgenový pohled.");
        Reflect.set(game, "xray", true);
        equal(1, Reflect.call(game, "visible", 11), "Při vybrané kartě s indexem 4 musí rentgen odhalit její shodný protějšek na indexu 11.");
        equal(0, Reflect.call(game, "visible", 0), "Rentgen musí ponechat karty s jinou hodnotou skryté.");
        equal(1, Reflect.call(game, "visible", 4), "Samotná vybraná karta musí zůstat viditelná, když rentgen ukazuje její protějšek.");
        Reflect.set(game, "first", 2);
        equal(6, Reflect.call(game, "visible", 7), "Výběr karty s hodnotou 6 musí odhalit protějšek této hodnoty, ne pevně vybranou oblíbenou kartu.");
        equal(0, Reflect.call(game, "visible", 11), "Po výběru jiné hodnoty musí být protějšek předchozí hodnoty znovu skrytý.");
        equal(cards, Reflect.get(game, "cards"), "Volání visible musí zachovat pořadí a hodnoty všech karet.");
        equal(0, Reflect.get(game, "attempts"), "Volání visible nesmí spotřebovat pokus.");
        equal(0, Reflect.get(game, "pairs"), "Volání visible nesmí přiznat nalezenou dvojici.");
        equal(new boolean[12], Reflect.get(game, "matched"), "Volání visible nesmí označit skryté karty za spárované.");
        equal(2, Reflect.get(game, "first"), "Volání visible musí zachovat index první vybrané karty.");
        expect(!((boolean) Reflect.call(game, "play", -1)), "play(-1) musí odmítnout neplatný index karty.");
        equal(true, Reflect.get(game, "xray"), "Odmítnutý výběr karty musí zachovat nynější nastavení rentgenu.");
        Reflect.set(game, "xray", false);
        equal(0, Reflect.call(game, "visible", 7), "Vypnutí rentgenu musí skrýt protějšek ukazovaný pouze tímto dodatečným pohledem.");
        ((boolean[]) Reflect.get(game, "matched"))[0] = true;
        equal(4, Reflect.call(game, "visible", 0), "Spárované karty musejí zůstat viditelné i při vypnutém rentgenu.");
        Reflect.set(game, "second", 8);
        equal(2, Reflect.call(game, "visible", 8), "Běžná druhá vybraná karta musí zůstat viditelná i bez rentgenu.");
        Reflect.call(game, "reset", 99L);
        equal(false, Reflect.get(game, "xray"), "Nové rozložení karet musí vypnout rentgenové nastavení předchozího kola.");
    }
}

@SuppressWarnings("unchecked")
class P13_Task_SokobanTest {
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
        var game = Reflect.create("P13_Task_Sokoban");
        Reflect.call(game, "reset");
        expect((boolean) Reflect.call(game, "canMove", 1, 0), "canMove(1, 0) musí povolit tlačení bedny doprava, pokud je buňka za ní volná.");
        Reflect.call(game, "command", "d");
        Reflect.call(game, "command", "d");
        int x = (int) Reflect.get(game, "x");
        int y = (int) Reflect.get(game, "y");
        int bx = (int) Reflect.get(game, "bx");
        int by = (int) Reflect.get(game, "by");
        String outcome = (String) Reflect.get(game, "outcome");
        String[] terrain = ((String[]) Reflect.get(game, "terrain")).clone();
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "canMove(1, 0) musí odmítnout tlačení bedny do pravé zdi. Ověř buňku za bednou.");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "Opakování stejného blokovaného dotazu canMove musí stále vrátit false bez změny pozice.");
        expect((boolean) Reflect.call(game, "canMove", -1, 0), "canMove(-1, 0) musí nadále povolit chůzi doleva od bedny, pokud je buňka volná.");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 1)), "canMove(1, 1) musí odmítnout úhlopříčný pohyb; povolený je jen jeden krok v hlavním směru.");
        expect(!((boolean) Reflect.call(game, "canMove", 0, 0)), "canMove(0, 0) musí odmítnout stání na místě jako navržený tah.");
        expect(!((boolean) Reflect.call(game, "canMove", 2, 0)), "canMove(2, 0) musí odmítnout skok o dvě buňky.");
        equal(x, Reflect.get(game, "x"), "Dotaz canMove musí zachovat souřadnici x hráče.");
        equal(y, Reflect.get(game, "y"), "Dotaz canMove musí zachovat souřadnici y hráče.");
        equal(bx, Reflect.get(game, "bx"), "Dotaz canMove musí zachovat souřadnici x bedny.");
        equal(by, Reflect.get(game, "by"), "Dotaz canMove musí zachovat souřadnici y bedny.");
        equal(outcome, Reflect.get(game, "outcome"), "Dotaz canMove musí zachovat zprávu o výsledku kola.");
        equal(terrain, Reflect.get(game, "terrain"), "Dotaz canMove musí zachovat každý řádek mapy.");
        Reflect.set(game, "x", 1);
        Reflect.set(game, "y", 1);
        expect(!((boolean) Reflect.call(game, "canMove", 0, -1)), "canMove(0, -1) musí odmítnout chůzi do zdi nad hráčem.");
        Reflect.set(game, "outcome", "finished");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "Po dokončení kola musí canMove odmítat další tahy, i když je místo volné.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_TrafficJamEscapeTest {
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
        var g = Reflect.create("P14_Task_TrafficJamEscape");
        Reflect.call(g, "reset");
        int[] oldX = ((int[]) Reflect.get(g, "xs")).clone(), oldY = ((int[]) Reflect.get(g, "ys")).clone();
        expect(!((boolean) Reflect.call(g, "fits", 0, 1, 2)), "fits(0, 1, 2) musí odmítnout navrženou pozici, protože ji blokuje jiné auto.");
        equal(oldX, ((int[]) Reflect.get(g, "xs")), "Odmítnutý dotaz fits musí zachovat souřadnici x každého auta.");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "Dotaz fits musí zachovat souřadnici y každého auta.");
        expect(((boolean) Reflect.call(g, "fits", 1, 2, 3)), "fits(1, 2, 3) musí přijmout připravenou volnou pozici směrem dolů.");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "I přijatý dotaz fits musí ponechat auta na původních souřadnicích y.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_FallingBlocksTest {
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
        var g = Reflect.create("P15_Task_FallingBlocks");
        Reflect.call(g, "restart");
        int oldX = ((int) Reflect.get(g, "px")), oldY = ((int) Reflect.get(g, "py"));
        expect(((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "fits musí přijmout nynější dílek na volné zkušební pozici (6, 5).");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "Dotaz fits na (6, 5) nesmí skutečný padající dílek na toto místo přesunout.");
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), -1, 0)), "fits musí odmítnout zkušební pozici (-1, 0) mimo desku.");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "Odmítnutí dotazu fits musí zachovat skutečné x a y padajícího dílku.");
        ((int[][]) Reflect.get(g, "board"))[5][6] = 1;
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "fits musí odmítnout zkušební pozici, pokud je některá její buňka již obsazená.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_GoFishTest {
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
        var g = Reflect.create("P16_Task_GoFish");
        Reflect.set(g, "hands", java.util.List.of(new java.util.ArrayList<>(java.util.List.of(7, 7, 7, 7)), new java.util.ArrayList<>()));
        Reflect.set(g, "books", new int[2]);
        Reflect.call(g, "collect", 0);
        equal(0, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size(), "collect musí odebrat z ruky všechny čtyři karty dokončené hodnoty a neponechat žádnou navíc.");
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "Sebrání čtyř karet stejné hodnoty musí přiznat právě jednu čtveřici.");
        equal(4, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size() + 4 * ((int[]) Reflect.get(g, "books"))[0], "Po sebrání musejí karty v ruce plus čtyři za každou přiznanou čtveřici stále odpovídat původním čtyřem kartám.");
        Reflect.call(g, "collect", 0);
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "Další volání collect bez nových karet nesmí přiznat stejnou čtveřici podruhé.");
    }
}

@SuppressWarnings("unchecked")
class P19_Task_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "chooseVertex s count 3 musí umět vybrat každý index 0, 1 a 2 včetně posledního vrcholu.",
        "Stejná semena a stejná volání chooseVertex musejí zopakovat stejnou posloupnost indexů.",
        "chooseVertex s jedním kandidátem musí vrátit 0; count 0 se musí odmítnout."
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
            var p = Reflect.create("P19_Task_ChaosGame");
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(((int) Reflect.call(p, "chooseVertex", random, 3)));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(((int) Reflect.call(p, "chooseVertex", a, 3))==((int) Reflect.call(p, "chooseVertex", b, 3)),1,n);
            check(((int) Reflect.call(p, "chooseVertex", new Random(42), 1))==0,2,"single");reject(()->Reflect.call(p, "chooseVertex", new Random(42), 0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P22_Task_VoronoiStainedGlassTest {
    static int checks;
    static final String[] CASES = {
        "seedList musí vrátit požadovaný počet postupných výsledků nextSeed v pořadí vytváření a se souřadnicemi uvnitř obrázku.",
        "seedList musí vrátit měnitelný seznam: odebrání jednoho zárodku musí být povolené.",
        "seedList musí přijímat počty 0–100 včetně prázdného výsledku pro 0 a odmítat počty mimo tento rozsah."
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
            var p = Reflect.create("P22_Task_VoronoiStainedGlass");
            var seeds=((java.util.List<Object>) Reflect.call(p, "seedList", new Random(42), 3));
            check(seeds.size()==3,0,seeds.size());
            var first=seeds.getFirst();check(((int) Reflect.call(first, "x"))>=0&&((int) Reflect.call(first, "x"))<400&&((int) Reflect.call(first, "y"))>=0&&((int) Reflect.call(first, "y"))<300,0,first);
            seeds.removeLast();check(seeds.size()==2,1,seeds.size());
            check(((java.util.List<Object>) Reflect.call(p, "seedList", new Random(42), 0)).isEmpty(),2,"empty");
            reject(()->Reflect.call(p, "seedList", new Random(42), -1),2);
        var expected = new ArrayList<>();
        var sameRandom = new Random(43);
        for (int n = 0; n < 3; n++) expected.add(Reflect.call(p, "nextSeed", sameRandom));
        check(expected.equals(Reflect.call(p, "seedList", new Random(43), 3)), 0, "successive seeds in generation order");
        check(((List<?>) Reflect.call(p, "seedList", new Random(42), 100)).size() == 100, 2, "upper bound");
        reject(() -> Reflect.call(p, "seedList", new Random(42), 101), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P23_Task_MandelbrotPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "matchingSeeds musí vrátit všechna odpovídající semena od 0 do 255 ve vzestupném pořadí, ne jen první shodu.",
        "matchingSeeds musí porovnat celý dodaný začátek palety, zachovat tento vstup a při chybějící shodě vrátit prázdný seznam.",
        "Zachovej původní tvorbu palety ze semene i výpočet fraktálu: opakování stejných vstupů musí vytvořit stejný obrázek."
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
        var p = Reflect.create("P23_Task_MandelbrotPostcards");
        int[] palette = (int[]) Reflect.call(p, "palette", 7L);
        int[] sample = Arrays.copyOf(palette, 4);
        int[] before = sample.clone();
        var found = (java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) sample);
        check(found.equals(java.util.List.of(7L)), 0, found);
        check(Arrays.equals(sample, before), 1, "sample unchanged");
        var edge = (java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) (int[]) Reflect.call(p, "palette", 255L));
        check(edge.equals(java.util.List.of(255L)), 0, edge);
        var expected = new java.util.ArrayList<Long>();
        for (long seed = 0; seed <= 255; seed++) {
            var random = new Random(seed);
            int colour = java.awt.Color.HSBtoRGB(random.nextInt(360) / 360.0F, .8F, 1);
            if (colour == palette[0]) expected.add(seed);
        }
        check(Reflect.call(p, "matchingSeeds", (Object) new int[]{palette[0]}).equals(expected), 1, "all prefix candidates");
        check(((java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) new int[]{0})).isEmpty(), 1, "impossible colour");
        reject(() -> Reflect.call(p, "matchingSeeds", (Object) new int[0]), 1);
        reject(() -> Reflect.call(p, "matchingSeeds", (Object) new int[17]), 1);
        var random = new Random(7);
        for (int colour : palette) check(colour == java.awt.Color.HSBtoRGB(random.nextInt(360) / 360.0F, .8F, 1), 2, "unchanged palette recipe");
        var a = (BufferedImage) Reflect.call(p, "render", 42L, 3.2);
        check(hash(a) == hash((BufferedImage) Reflect.call(p, "render", 42L, 3.2)), 2, "repeatable picture");
        check((int) Reflect.call(p, "escape", 0.0, 0.0) == 160, 2, "fractal geometry");
    }
}

@SuppressWarnings("unchecked")
class P24_Task_ColourFilterSecretsTest {
    static int checks;
    static final String[] CASES = {
        "hide musí pro zkoušená semena 42 a 43 vytvořit odlišné maskování. Použij požadované semeno místo pevného.",
        "Další volání hide se stejnou maskou a semenem musí zopakovat každý pixel.",
        "reveal musí z obou semen šumu získat stejnou skrytou zprávu. Změna maskování musí zachovat kanál zprávy."
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
            var p = Reflect.create("P24_Task_ColourFilterSecrets");
            var mask=((java.awt.image.BufferedImage) Reflect.call(p, "mask", "HI"));var a=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42));var b=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 43));check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42))),1,hash(a));check(hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a)))==hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", b))),2,hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P25_Task_TwoNoisyPicturesTest {
    static int checks;
    static final String[] CASES = {
        "shares musí v prázdných blocích používat různé vzory teček místo opakování jednoho promíchání všude.",
        "Opakování semene 42 musí zopakovat první díl obrázku, zatímco zkoušené semeno 43 jej musí změnit.",
        "Každý malý blok v obou dílech obrázku musí nadále obsahovat přesně dvě černé tečky."
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
            var p = Reflect.create("P25_Task_TwoNoisyPictures");
            var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42));var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
            check(hash(a[0])==hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42))[0]),1,hash(a[0]));check(hash(a[0])!=hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 43))[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
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
