import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

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
        run("P03_Task_MiniBattleshipHunt", () -> P03_Task_MiniBattleshipHuntTest.main(new String[0]));
        run("P04_Task_LoopBuilder", () -> P04_Task_LoopBuilderTest.main(new String[0]));
        run("P07_Task_TicTacToe", () -> P07_Task_TicTacToeTest.main(new String[0]));
        run("P08_Task_Chomp", () -> P08_Task_ChompTest.main(new String[0]));
        run("P09_Task_Sudoku", () -> P09_Task_SudokuTest.main(new String[0]));
        run("P12_Task_Hexapawn", () -> P12_Task_HexapawnTest.main(new String[0]));
        run("P13_Task_FourColourMap", () -> P13_Task_FourColourMapTest.main(new String[0]));
        run("P14_Task_Minesweeper", () -> P14_Task_MinesweeperTest.main(new String[0]));
        run("P15_Task_Net", () -> P15_Task_NetTest.main(new String[0]));
        run("P16_Task_BlackBox", () -> P16_Task_BlackBoxTest.main(new String[0]));
        run("P17_Task_AntHighway", () -> P17_Task_AntHighwayTest.main(new String[0]));
        run("P20_Task_Quarto", () -> P20_Task_QuartoTest.main(new String[0]));
        run("P21_Task_Reversi", () -> P21_Task_ReversiTest.main(new String[0]));
        run("P22_Task_LightUp", () -> P22_Task_LightUpTest.main(new String[0]));
        run("P25_Task_Slant", () -> P25_Task_SlantTest.main(new String[0]));
        run("P26_Task_BinaryBalance", () -> P26_Task_BinaryBalanceTest.main(new String[0]));
        run("P27_Task_RuleGarden", () -> P27_Task_RuleGardenTest.main(new String[0]));
        run("P28_Task_ForestFireSandbox", () -> P28_Task_ForestFireSandboxTest.main(new String[0]));
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
class P03_Task_MiniBattleshipHuntTest {
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
        var g = Reflect.create("P03_Task_MiniBattleshipHunt");
        for (int layout = 0; layout < 3; layout++) {
            Reflect.call(g, "layout", layout);
            int[] counts = new int[3];
            for (var row : ((int[][]) Reflect.get(g, "board"))) {
                for (int cell : row) {
                    counts[cell]++;
                }
            }
            equal(3, counts[2], "layout musí v každém rozestavení umístit všechny tři buňky lodi 2. Ověř, že smyčka navštíví všechny vybrané souřadnice.");
            equal(2, counts[1], "layout musí v každém rozestavení umístit přesně dvě buňky lodi 1.");
        }
        Reflect.call(g, "layout", 0);
        equal(2, ((int[][]) Reflect.get(g, "board"))[3][4], "Po layout(0) musí board[3][4] obsahovat loď 2. Poslední souřadnice se nesmí vynechat.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_LoopBuilderTest {
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
        var g = Reflect.create("P04_Task_LoopBuilder");
        equal(new int[]{0, 4}, ((int[]) Reflect.call(g, "vertices", 12)), "vertices(12) musí vrátit [0, 4]: první svislá hrana spojuje sousední řádky vrcholů.");
        equal(new int[]{11, 15}, ((int[]) Reflect.call(g, "vertices", 23)), "vertices(23) musí vrátit [11, 15]: poslední svislá hrana zůstává ve svém sloupci.");
        equal(new int[]{14, 15}, ((int[]) Reflect.call(g, "vertices", 11)), "vertices(11) musí vrátit [14, 15]: poslední vodorovná hrana spojuje sousední sloupce.");
        Reflect.call(g, "reset");
        for (int n : new int[]{0, 1, 2, 9, 10, 11, 12, 15, 16, 19, 20, 23}) {
            ((boolean[]) Reflect.get(g, "edges"))[n] = true;
        }
        expect(((boolean) Reflect.call(g, "oneLoop")), "oneLoop musí poznat dvanáct vnějších obvodových hran jako jeden uzavřený okruh. Ověř koncové body vracené vertices.");
        equal(new int[]{0, 1}, Reflect.call(g, "vertices", 0), "vertices(0) musí pro první vodorovnou hranu vrátit [0, 1].");
        equal(new int[]{14, 15}, Reflect.call(g, "vertices", 11), "vertices(11) musí pro poslední vodorovnou hranu vrátit [14, 15].");
        boolean rejected = false;
        try { Reflect.call(g, "vertices", 24); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "vertices musí odmítnout čísla hran mimo 0–23, ne pro ně vymýšlet koncové body.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_TicTacToeTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_TicTacToe");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "play", 9)) && ((char[]) Reflect.get(game, "board"))[8] == 'X', "play(9) musí přijmout prázdné poslední políčko a položit tam X. Ověř platný rozsah v mayPlay.");
        check(!((boolean) Reflect.call(game, "play", 9)) && ((int) Reflect.get(game, "moves")) == 1, "Opakovaný tah na políčko 9 se musí odmítnout bez zvýšení počtu tahů.");
        check(!((boolean) Reflect.call(game, "play", 0)) && !((boolean) Reflect.call(game, "play", 10)), "play musí odmítnout políčka 0 a 10 před čtením desky. Platná hráčská čísla jsou 1–9.");
        check(((char) Reflect.get(game, "player")) == 'O', "Odmítnuté tahy musejí po jednom tahu X ponechat na tahu O.");
        Reflect.set(game, "winner", 'X');
        check(!((boolean) Reflect.call(game, "play", 1)) && ((char[]) Reflect.get(game, "board"))[0] == '.', "Po skončení kola musí play(1) odmítnout i prázdné políčko a ponechat je prázdné.");
        check(((int) Reflect.get(game, "moves")) == 1 && ((char) Reflect.get(game, "player")) == 'O', "Odmítnutí tahu po konci kola musí zachovat počet tahů i nynějšího hráče.");
    }
}

@SuppressWarnings("unchecked")
class P08_Task_ChompTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Task_Chomp");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 4, 5)) && ((int[]) Reflect.get(game, "widths"))[3] == 4, "bite(4, 5) musí přijmout zbývající čokoládu ve čtvrtém řádku a zkrátit tento řádek na čtyři buňky.");
        int[] before = ((int[]) Reflect.get(game, "widths")).clone();
        check(!((boolean) Reflect.call(game, "bite", 5, 1)) && Arrays.equals(before, ((int[]) Reflect.get(game, "widths"))), "bite(5, 1) musí odmítnout neexistující pátý řádek bez změny čokolády.");
        check(!((boolean) Reflect.call(game, "bite", 4, 5)), "Druhé bite(4, 5) musí odmítnout dílek snědený prvním soustem.");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 1, 1)) && ((int) Reflect.get(game, "winner")) == 2, "bite(1, 1) musí nadále dovolit sníst jedovatý dílek a přiznat vítězství druhému hráči.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_SudokuTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Task_Sudoku");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "place", 0, 1, 2)) && ((int[][]) Reflect.get(game, "board"))[0][1] == 2, "place(0, 1, 2) musí přijmout 2 do tohoto původně prázdného a povoleného políčka.");
        check(!((boolean) Reflect.call(game, "place", 0, 0, 0)) && ((int[][]) Reflect.get(game, "board"))[0][0] == 1, "place(0, 0, 0) nesmí vymazat pevnou jedničku v levém horním políčku.");
        check(!((boolean) Reflect.call(game, "place", -1, 0, 1)) && !((boolean) Reflect.call(game, "place", 0, 4, 1)), "place musí odmítnout řádek -1 a sloupec 4 před čtením nápověd nebo zápisem na desku.");
        check(!((boolean) Reflect.call(game, "place", 0, 2, 2)) && ((int[][]) Reflect.get(game, "board"))[0][2] == 0, "place(0, 2, 2) musí odmítnout opakovanou dvojku a ponechat políčko prázdné.");
    }
}

@SuppressWarnings("unchecked")
class P12_Task_HexapawnTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Task_Hexapawn");
        check(((int) Reflect.call(game, "goalRow", 2)) == 2, "goalRow(2) musí označit dolní řádek s vnitřním indexem 2.");
        check(((int) Reflect.call(game, "goalRow", 1)) == 0, "goalRow(1) musí označit horní řádek s vnitřním indexem 0.");
        Reflect.call(game, "reset");
        Reflect.set(game, "player", 2);
        Reflect.set(game, "board", new int[][]{{0,0,0},{2,0,0},{0,0,1}});
        Reflect.call(game, "command", "2 1 3 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).contains("2 wins"), "Když hráč 2 při skutečné hře dojde na dolní řádek, hra musí oznámit jeho vítězství.");
        check(((int) Reflect.call(game, "forwardStep", 1)) == -1 && ((int) Reflect.call(game, "forwardStep", 2)) == 1, "forwardStep musí hráče 1 posouvat o řádek nahoru a hráče 2 o řádek dolů.");
    }
}

@SuppressWarnings("unchecked")
class P13_Task_FourColourMapTest {
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
        var game = Reflect.create("P13_Task_FourColourMap");
        Reflect.call(game, "reset");
        Reflect.set(game, "map", new String[]{"AABCCDEE"});
        expect(!((boolean) Reflect.call(game, "touches", 0, 3)), "touches musí číst změněnou mapu: A a D už nesdílejí stranu.");
        expect((boolean) Reflect.call(game, "touches", 0, 1), "touches musí poznat stranu, kterou vedle sebe sdílejí A a B.");
        expect((boolean) Reflect.call(game, "touches", 1, 0), "touches musí pro B–A dát stejnou odpověď jako pro A–B.");
        expect(!((boolean) Reflect.call(game, "touches", 0, 0)), "touches(A, A) musí být false: oblast není svým vlastním sousedem.");
        Reflect.set(game, "map", new String[]{"AB", "CD"});
        expect((boolean) Reflect.call(game, "touches", 0, 2), "touches musí v této mapě poznat, že A leží přímo nad C.");
        expect((boolean) Reflect.call(game, "touches", 1, 3), "touches musí ve změněné mapě poznat, že B leží přímo nad D.");
        expect(!((boolean) Reflect.call(game, "touches", 0, 3)), "A a D se zde dotýkají jen rohem, proto touches musí vrátit false.");
        Reflect.set(game, "map", new String[]{"AA", "BB"});
        int[] colours = {1, 1, 0, 0, 0};
        Reflect.set(game, "colours", colours.clone());
        equal(1, Reflect.call(game, "conflicts"), "conflicts musí dvojici stejně obarvených sousedů započítat jednou, i když je jejich hranice dlouhá dvě buňky.");
        equal(colours, Reflect.get(game, "colours"), "Zjišťování sousedů a konfliktů nesmí změnit hráčovy barvy.");
        Reflect.set(game, "map", new String[]{"ABCDE", "ABCDE"});
        Reflect.set(game, "colours", new int[]{2, 2, 2, 2, 2});
        equal(4, Reflect.call(game, "conflicts"), "Když mají všechny oblasti stejnou barvu, má tato mapa přesně čtyři konfliktní sousední dvojice.");
        Reflect.set(game, "colours", new int[]{1, 2, 1, 2, 1});
        equal(true, Reflect.call(game, "solved"), "solved musí při rozhodování o dokončeném obarvení použít skutečné sousedy změněné mapy.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_MinesweeperTest {
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
        var g = Reflect.create("P14_Task_Minesweeper");
        boolean[][] mines = new boolean[6][6];
        mines[1][1] = true; mines[2][2] = true; mines[3][3] = true; mines[5][5] = true;
        Reflect.set(g, "mines", (Object) mines);
        Reflect.call(g, "inspect", 2, 2);
        boolean[][] lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[1][1] && lens[2][2] && lens[3][3] && !lens[5][5], "inspect(2, 2) musí zahrnout miny ve středu i na úhlopříčkách svého okolí 3×3, ale ne vzdálenou minu na (5, 5).");
        var buttons = (javax.swing.JButton[][]) Reflect.get(g, "buttons");
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) buttons[r][c] = new javax.swing.JButton();
        }
        Reflect.call(g, "refresh");
        equal("*", buttons[1][1].getText(), "refresh musí na tlačítku úhlopříčné miny nalezené pomocí Inspect ukázat *.");
        expect((int) Reflect.get(g, "opened") == 0 && !(boolean) Reflect.get(g, "lost"), "Inspect nesmí zvýšit opened ani nastavit lost; prohlížení není odehraný tah.");
        equal(new boolean[6][6], Reflect.get(g, "revealed"), "Inspect musí zachovat všechna již odkrytá políčka beze změny.");
        Reflect.call(g, "inspect", 5, 5);
        lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[5][5] && !lens[1][1], "Nová prohlídka musí nahradit předchozí pohled, ne ponechat viditelné vzdálené miny.");
        Reflect.call(g, "inspect", -1, 0);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "Inspect mimo desku musí bezpečně vymazat dodatečný pohled bez čtení neplatného políčka.");
        equal(2, Reflect.call(g, "adjacent", 2, 2), "adjacent(2, 2) musí nadále napočítat dvě sousední miny a vynechat samotný střed.");
        Reflect.call(g, "reset", 42L);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "Nová deska musí skrýt všechny miny ukazované pouze pomocí Inspect.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_NetTest {
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
        var g = Reflect.create("P15_Task_Net");
        Reflect.set(g, "tiles", new int[4][4]);
        ((int[][]) Reflect.get(g, "tiles"))[0][0] = 2;
        expect(!((boolean[][]) Reflect.call(g, "connected"))[0][1], "linked musí odmítnout sousední dílek, který nemá vývod otočený zpět k nynějšímu dílku.");
        ((int[][]) Reflect.get(g, "tiles"))[0][1] = 8;
        expect(((boolean[][]) Reflect.call(g, "connected"))[0][1], "Pravý vývod a levý vývod sousedního dílku se musejí spojit a přenášet energii.");
        expect(!((boolean[][]) Reflect.call(g, "connected"))[1][1], "Úhlopříčný dílek nesmí získat energii jen proto, že se zdroje dotýká rohem.");
        Reflect.call(g, "solvedBoard");
        expect(((boolean) Reflect.call(g, "won")), "Připravená správně spojená síť musí splnit won. Ověř oba konce každého spojení linked.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_BlackBoxTest {
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
        var g = Reflect.create("P16_Task_BlackBox");
        Reflect.call(g, "reset");
        boolean[][] box = new boolean[4][4];
        box[1][1] = true;
        equal("W1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "trace z N1 musí skončit ve W1, když kulička leží šikmo vpravo od paprsku. Ověř dva různé boční sousedy.");
        box = new boolean[4][4];
        equal("S1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "trace z N1 přes prázdnou krabici musí pokračovat rovně do S1.");
        box[0][0] = true;
        equal("HIT", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "Kulička přímo před paprskem musí dát HIT před jakýmkoli bočním vychýlením.");
        equal("REFLECT", ((java.lang.String) Reflect.call(g, "trace", box, "N2")), "Úhlopříčná kulička u vstupu musí způsobit, že paprsek N2 vrátí REFLECT.");
        Reflect.call(g, "command", "mark 1 1");
        Reflect.call(g, "command", "mark 1 2");
        Reflect.call(g, "command", "check");
        expect(((java.lang.String) Reflect.get(g, "message")).contains("disagree"), "Kontrola chybného rozmístění musí oznámit nesoulad jeho paprsků s pozorováními.");
        Reflect.call(g, "command", "ray N1");
        equal("Probe recorded", ((java.lang.String) Reflect.get(g, "message")), "Nové vyslání paprsku musí nahradit starou zprávu o nesouladu textem Probe recorded.");
        Reflect.call(g, "command", "mark 1 1");
        equal("Guess removed", ((java.lang.String) Reflect.get(g, "message")), "Odebrání tipu musí oznámit Guess removed místo opakování předchozí události.");
    }
}

@SuppressWarnings("unchecked")
class P17_Task_AntHighwayTest {
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
        var g = Reflect.create("P17_Task_AntHighway");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 2);
        Reflect.call(g, "step");
        equal(80, ((int) Reflect.get(g, "x")), "Krok na západ ze sloupce 0 musí přejít do sloupce 80, ne na východ.");
        equal(0, ((int) Reflect.get(g, "y")), "Vodorovný krok mravence musí zachovat souřadnici řádku.");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 1);
        ((boolean[][]) Reflect.get(g, "black"))[0][0] = true;
        Reflect.call(g, "step");
        equal(60, ((int) Reflect.get(g, "y")), "Krok na sever z řádku 0 musí přejít do řádku 60.");
        expect(!((boolean[][]) Reflect.get(g, "black"))[0][0] && ((long) Reflect.get(g, "steps")) == 1, "Jeden krok musí jednou přebarvit původní buňku a zvýšit steps přesně o jedna.");
    }
}

@SuppressWarnings("unchecked")
class P20_Task_QuartoTest {
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
        var g = Reflect.create("P20_Task_Quarto");
        Reflect.call(g, "reset");
        int[] diagonal = {0, 5, 10, 15};
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[diagonal[n]] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "won musí poznat hotovou hlavní úhlopříčku s figurkami se společnou vlastností.");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[1 + n * 4] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "won musí poznat hotový sloupec, nikoli jen vodorovné řádky.");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[n] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "Existující vítězný vodorovný řádek musí nadále splnit won.");
        Reflect.call(g, "reset");
        expect(!((boolean) Reflect.call(g, "won")), "Prázdná deska nesmí splnit won; prázdná políčka netvoří vítěznou linii.");
    }
}

@SuppressWarnings("unchecked")
class P21_Task_ReversiTest {
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
        var game = Reflect.create("P21_Task_Reversi");
        Reflect.call(game, "reset");
        equal(2, Reflect.call(game, "score", 1), "score musí každému hráči přiznat 2 body za jeho dva běžné úvodní kameny.");
        int[][] board = new int[6][6];
        board[0][0] = 1;
        board[2][2] = board[2][3] = 2;
        Reflect.set(game, "board", board);
        equal(3, Reflect.call(game, "score", 1), "score musí hráči 1 přiznat tři body za jediný obsazený roh.");
        equal(2, Reflect.call(game, "score", 2), "score musí hráči 2 přiznat dva body za dva běžné kameny.");
        expect(!((boolean) Reflect.call(game, "hasMove", 1)) && !((boolean) Reflect.call(game, "hasMove", 2)), "V této koncové pozici nemá žádný hráč platný tah. Při změně bodování zachovej pravidla zajímání.");
        Reflect.call(game, "command", "pass");
        Reflect.call(game, "command", "pass");
        equal("Player 1 wins", Reflect.get(game, "outcome"), "Po vynechání obou hráčů musí roh hráče 1 porazit dva běžné kameny hráče 2. Vítěze určuj podle váženého skóre.");
        var output = new java.io.ByteArrayOutputStream();
        var before = System.out;
        try (var capture = new java.io.PrintStream(output)) {
            System.setOut(capture);
            Reflect.call(game, "display");
        } finally {
            System.setOut(before);
        }
        expect(output.toString().contains("Score P1: 3  P2: 2"), "Průběžný výpis musí pro tuto pozici ukázat Score P1: 3  P2: 2.");
        board = new int[6][6];
        board[0][0] = board[0][5] = board[5][0] = board[5][5] = 2;
        board[0][3] = board[4][5] = 1;
        Reflect.set(game, "board", board);
        int[][] snapshot = new int[6][];
        for (int row = 0; row < board.length; row++) snapshot[row] = board[row].clone();
        equal(12, Reflect.call(game, "score", 2), "Čtyři rohy hráče 2 musejí dohromady dát 12 bodů.");
        equal(2, Reflect.call(game, "score", 1), "Dva kameny na hraně mimo rohy stále mají každý hodnotu jednoho bodu.");
        equal(snapshot, Reflect.get(game, "board"), "Volání score pro kteréhokoli hráče musí zachovat každou buňku desky.");
        equal(2, board[5][5], "Bodování musí zachovat stávající kámen hráče 2 na board[5][5].");
        equal(0, board[3][3], "Bodování musí ponechat board[3][3] prázdné, nikoli tam vytvořit kámen.");
    }
}

@SuppressWarnings("unchecked")
class P22_Task_LightUpTest {
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
        var g = Reflect.create("P22_Task_LightUp");
        Reflect.call(g, "reset");
        ((boolean[][]) Reflect.get(g, "bulbs"))[0][2] = true;
        equal(1, ((int) Reflect.call(g, "visible", 0, 0)), "visible(0, 0) musí poznat žárovku dále ve volné chodbě, ne skončit u sousedního políčka.");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[1][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 1, 0)), "visible musí skončit u číslované zdi; žárovka za ní není viditelná.");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[2][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 0, 0)), "visible nesmí započítat úhlopříčnou žárovku. Světlo vede jen přímou chodbou.");
    }
}

@SuppressWarnings("unchecked")
class P25_Task_SlantTest {
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
        var g = Reflect.create("P25_Task_Slant");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "undo");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[0][0], "Undo musí po položení lomítka obnovit prázdnou první buňku. Řádky snímku musejí zůstat nezávislé na živé desce.");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "2 2 /");
        Reflect.call(g, "command", "undo");
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "Vrácení nejnovějšího tahu musí zachovat starší lomítko na cells[0][0].");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[1][1], "Undo musí odstranit poslední lomítko na cells[1][1] a ponechat buňku prázdnou.");
        char[][] copy = ((char[][]) Reflect.call(g, "snapshot"));
        copy[0][0] = '.';
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "Úprava vráceného snímku nesmí změnit nynější lomítko na cells[0][0].");
        Reflect.call(g, "reset");
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                Reflect.call(g, "command", r + " " + c + " /");
            }
        }
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Připravená deska lomítek musí dokončit hádanku a oznámit výsledek.");
        Reflect.call(g, "command", "undo");
        expect(!((boolean) Reflect.call(g, "solved")) && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Undo po vítězném tahu musí obnovit nedokončenou desku a vymazat zprávu o dokončení kola.");
        Reflect.call(g, "command", "3 3 /");
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Tah vrácený po vítězství musí jít znovu zahrát a znovu dokončit hádanku.");
        StringBuilder input = new StringBuilder();
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                input.append(r + " " + c + " /\n");
            }
        }
        input.append("undo\n/q\n");
        var originalIn = System.in;
        var originalOut = System.out;
        var captured = new java.io.ByteArrayOutputStream();
        try (var redirected = new java.io.PrintStream(captured, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setIn(new java.io.ByteArrayInputStream(input.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            System.setOut(redirected);
            Reflect.call(Reflect.create("P25_Task_Slant"), "main");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        String output = captured.toString(java.nio.charset.StandardCharsets.UTF_8);
        int last = output.lastIndexOf("Cell diagonals");
        expect(last >= 0 && output.substring(last).contains("3 [/, /, .]"), "Skutečná konzole musí přijmout undo i po dokončení a ukázat odstraněné poslední lomítko.");
    }
}

@SuppressWarnings("unchecked")
class P26_Task_BinaryBalanceTest {
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
        var g = Reflect.create("P26_Task_BinaryBalance");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 1, 0);
        equal(0, ((int[][]) Reflect.get(g, "givens"))[0][1], "Úprava cells[0][1] nesmí změnit jeho původně prázdnou položku v givens.");
        Reflect.call(g, "restart");
        equal(0, ((int[][]) Reflect.get(g, "cells"))[0][1], "Reset musí obnovit cells[0][1] na původně prázdnou hodnotu 0.");
        int[][] copy = ((int[][]) Reflect.call(g, "copyBoard", (Object) ((int[][]) Reflect.get(g, "givens"))));
        copy[2][1] = 2;
        equal(0, ((int[][]) Reflect.get(g, "givens"))[2][1], "Změna pozdějšího kopírovaného řádku nesmí změnit givens[2][1]. Samotná kopie vnějšího pole nestačí.");
        equal(1, copy[0][0], "copyBoard musí zachovat původní hodnotu nápovědy na [0][0].");
    }
}

@SuppressWarnings("unchecked")
class P27_Task_RuleGardenTest {
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
        var g = Reflect.create("P27_Task_RuleGarden");
        equal(java.util.List.of("...#...", "..#.#..", ".#...#."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 7, 3)), "grow s pravidlem 90 musí vytvořit ...#..., potom ..#.#.. a .#...#. Každá nová buňka čte stejný předchozí řádek.");
        boolean[] before = {false, true, false, false, false};
        boolean[] saved = before.clone();
        Reflect.call(g, "nextRow", before, 90);
        equal(saved, before, "nextRow musí zachovat všechny hodnoty vstupního řádku.");
        equal(java.util.List.of(".#.", "#.#", "..."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 3, 3)), "Při šířce 3 musí pravidlo 90 dát .#., potom #.# a ...; buňky mimo řádek zůstávají zhasnuté.");
        var random = new java.util.Random(3);
        for (int sample = 0; sample < 10; sample++) {
            boolean[] row = new boolean[13];
            for (int c = 0; c < 13; c++) {
                row[c] = random.nextBoolean();
            }
            boolean[] reverseOracle = new boolean[13];
            int rule = random.nextInt(256);
            for (int c = 12; c >= 0; c--) {
                int pattern = row[c] ? 2 : 0;
                if (c > 0 && row[c - 1]) {
                    pattern += 4;
                }
                if (c < 12 && row[c + 1]) {
                    pattern++;
                }
                reverseOracle[c] = ((rule>>pattern)&1) != 0;
            }
            equal(reverseOracle, ((boolean[]) Reflect.call(g, "nextRow", row, rule)), "nextRow musí dát stejnou generaci při výpočtu buněk zleva i zprava.");
        }
    }
}

@SuppressWarnings("unchecked")
class P28_Task_ForestFireSandboxTest {
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
        var g = Reflect.create("P28_Task_ForestFireSandbox");
        int[][] old = new int[40][60];
        old[20][10] = 2; old[20][9] = 1; old[20][11] = 1; old[20][12] = 1;
        old[19][10] = 1; old[21][10] = 1; old[21][11] = 1;
        int[][] snapshot = java.util.Arrays.stream(old).map(int[]::clone).toArray(int[][]::new);
        int[][] next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[20][11] == 2 && next[20][12] == 1, "nextForest musí zapálit strom bezprostředně východně od starého ohně, ale ne další strom za ním.");
        expect(next[20][9] == 1 && next[19][10] == 1 && next[21][10] == 1 && next[21][11] == 1, "Východní vítr nesmí zapálit stromy západně, severně, jižně ani šikmo od starého ohně.");
        equal(0, next[20][10], "Buňka, která už hořela, musí být v další generaci prázdná.");
        equal(snapshot, old, "Výpočet dalšího lesa nesmí přepsat žádnou buňku původního lesa.");
        Reflect.set(g, "cells", (Object) old); Reflect.call(g, "step");
        equal(next, Reflect.get(g, "cells"), "Skutečný krok Step musí zobrazit les vytvořený novým pravidlem větru.");
        Reflect.call(g, "step");
        equal(2, ((int[][]) Reflect.get(g, "cells"))[20][12], "Druhý krok Step musí posunout oheň o další buňku východně s použitím výsledku předchozího kroku.");
        old = new int[40][60]; old[0][59] = 2; old[0][0] = 1;
        next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[0][0] == 1 && next[0][59] == 0, "Oheň na pravém okraji nesmí zapálit levý okraj; původní okrajový oheň přesto zhasne.");
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
