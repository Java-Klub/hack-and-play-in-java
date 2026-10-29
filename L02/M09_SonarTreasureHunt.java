// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
// </editor-fold>

/// # L02.09 · SonarTreasureHunt
///
/// Prohledej moře 8×8 a najdi tři ukryté poklady pouze osmi sondami. Každé minutí oznámí vzdálenost tehdy
/// nejbližšího zbývajícího pokladu, proto před dalším pokusem porovnávej stopy.
///
/// **Tvoje mise:** Program přijme sondu o řádek nebo sloupec za mapou. Oprav insideSea tak, aby do
/// vnitřní mapy patřily pouze index (indexy) 0 až 7.
///
/// **Dnes trénuješ:** Boolean range (logický rozsah): několik comparison (porovnání) může popsat jedinou
/// obdélníkovou oblast. Řádek i sloupec musejí být mezi svými dolními a horními hranicemi; osm pozic
/// začínajících nulou končí sedmičkou.
///
/// Program otevře moře. Kliknutím prozkoumej políčko. Zkus obě tlačítka Try outside: ani jedno nemá
/// spotřebovat sondu. Po opravě obou mezí začne New sea nový lov pokladů.
boolean insideSea(int row, int column) {
    return row >= 0 && row < 8 && column >= 0 && column < 8;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F06">
/// Boolean expression (logický výraz) dává `true` nebo `false`. `&&` vyžaduje obě condition
/// (podmínky), `||` alespoň jednu a `!` obrátí logický výsledek. Comparison (porovnání) jako `>=`
/// zahrnuje rovnost; `>` ji nezahrnuje.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int age = 14;
/// boolean hasTicket = false;
/// boolean enter = age >= 12 && hasTicket;
/// boolean recognised = "open".equals("OPEN");
/// ```
///
/// Předpověz oba výsledky: každý je `false`. Věk stačí, ale podmínka vstupenky ne. `equals` porovnává
/// obsah String (řetězců) a rozlišuje velikost písmen; `equalsIgnoreCase` ji záměrně ignoruje. U
/// object (objektů) se `==` ptá, zda dva reference (odkazy) označují tentýž objekt, nikoli zda mají
/// stejný text. Porovnání obsahu a rozsahu odpovídají na různé otázky. Než condition uvěříš, ověř
/// hodnotu přesně na hranici a těsně za ní.
///
/// Nyní předpověz enter pro věk dvanáct s platnou vstupenkou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `insideSea(int row, int column)` — Přijmi vnitřní row a column index (indexy řádku a sloupce), už
///   převedené z hráčových souřadnic 1–8.
/// - `row >= 0` — Řádek je alespoň nula; >= zahrnuje rovnost.
/// - `row <= 8` — Současné horní porovnání zahrnuje samotnou osmičku, která je mimo osm pozic počítaných
///   od nuly.
/// - `column <= 8` — Sloupec má vlastní horní hranici; oprava jen řádku by ponechala další cestu ven.
/// - `&&` — Logical AND (logické a): každé spojené porovnání musí být true, aby byl true celý expression
///   (výraz).
/// - `return` — Vrať spojené rozhodnutí; toto pravidlo samo neodebírá sondu.
///
/// ### Co už obstarává připravená část
///
/// command přečte hráčovy souřadnice, odečte od nich jednu a volá insideSea před záznamem sondy. distance
/// vypočítá stopu a připravená historie uchovává každou starou stopu spojenou s původním okamžikem.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Klikni na políčko moře. Číslo označí vzdálenost k nejbližšímu pokladu, který byl v tu chvíli
/// ještě schovaný. Počítej kroky po řádcích a sloupcích, nikoli šikmo: jeden krok nahoru a dva doprava
/// znamenají vzdálenost tři. Clue log (záznam měření) uchová původní výsledek; po nalezení jiného pokladu
/// se stará čísla nepřepočítají. FOUND znamená nález, po osmi sondách X odhalí přehlédnuté poklady.
/// New sea začne nové kolo.
///
/// Obě tlačítka Try outside záměrně žádají řádek nebo sloupec devět. Sleduj počítadlo: chybný guard
/// (kontrola mezí) je přijme, správný je odmítne bez spotřebování sondy. Tlačítka volají stejnou command
/// a insideSea jako běžná kliknutí. Souřadnice hráče začínají jedničkou; command od nich před voláním
/// tvého pravidla odečte jedničku. Platný index (pořadové číslo) tak sahá od nuly do sedmi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vypsané souřadnice 1–8 se před tímto pravidlem převedou na 0–7.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Všechny čtyři hranice musejí platit současně.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij row < 8 a column < 8; zachovej obě kontroly >= 0.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
Set<Integer> treasures;
Set<Integer> probed;
List<String> history;
int probes;
String outcome;

void reset() {
    var cells = new ArrayList<Integer>();
    for (int i = 0; i < 64; i++) {
        cells.add(i);
    }
    Collections.shuffle(cells);
    treasures = new HashSet<>(cells.subList(0, 3));
    probed = new HashSet<>();
    history = new ArrayList<>();
    probes = 0;
    outcome = "";
}

int distance(int cell) {
    int best = 99;
    for (int t : treasures) {
        best = Math.min(best, Math.abs(t / 8 - cell / 8) + Math.abs(t % 8 - cell % 8));
    }
    return best;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(insideSea(r, c) && !probed.contains(r * 8 + c));
    int cell = r * 8 + c;
    probed.add(cell);
    probes++;
    boolean hit = treasures.remove(cell);
    history.add("Probe " + probes + " at " + (r + 1) + "," + (c + 1) + ": " + (hit ? "treasure found" : "distance " + distance(cell)));
    if (treasures.isEmpty()) {
        outcome = "All treasures found!";
    } else {
        if (probes == 8) {
            outcome = "No probes left. Remaining treasures: " + treasures.stream().map((t)->(t / 8 + 1) + "," + (t % 8 + 1)).toList();
        }
    }
}

// Připravené Swing controls (ovládací prvky) volají stejnou command a insideSea jako testy.
JButton[][] seaButtons = new JButton[8][8];
JLabel scoreLabel = new JLabel();
JLabel feedbackLabel = new JLabel("Click any sea square to send your first probe.");
JTextArea clueLog = new JTextArea(5, 44);

void refreshSea() {
    scoreLabel.setText("Probes: " + probes + " / 8     Treasures left: " + treasures.size());
    clueLog.setText(String.join("\n", history));
    clueLog.setCaretPosition(clueLog.getDocument().getLength());
    for (int row = 0; row < 8; row++) {
        for (int column = 0; column < 8; column++) {
            int cell = row * 8 + column;
            var button = seaButtons[row][column];
            button.setEnabled(outcome.isEmpty() && !probed.contains(cell));
            if (!outcome.isEmpty() && treasures.contains(cell)) {
                button.setText("X");
                button.setToolTipText("A treasure you did not find.");
                button.setBackground(new Color(255, 210, 130));
            }
        }
    }
}

void probeAt(int row, int column) {
    if (!outcome.isEmpty()) {
        feedbackLabel.setText("Round finished. Click New sea to play again.");
        return;
    }
    boolean onMap = row >= 1 && row <= 8 && column >= 1 && column <= 8;
    int cell = (row - 1) * 8 + column - 1;
    if (onMap && probed.contains(cell)) {
        feedbackLabel.setText("You already explored that square; no probe spent.");
        return;
    }
    boolean hit = treasures.contains(cell);
    try {
        command(row + " " + column);
        if (onMap) {
            var button = seaButtons[row - 1][column - 1];
            button.setText(hit ? "FOUND" : "" + distance(cell));
            button.setBackground(hit ? new Color(255, 220, 100) : new Color(210, 230, 245));
            button.setToolTipText(history.getLast());
            feedbackLabel.setText(hit ? "Treasure found! Old distances still describe the earlier probes."
                : "Distance " + distance(cell) + ": count row steps + column steps to the nearest hidden treasure.");
        } else {
            feedbackLabel.setText("BUG: (" + row + ", " + column + ") is outside the sea, but spent a probe.");
        }
        if (!outcome.isEmpty()) feedbackLabel.setText(treasures.isEmpty()
            ? "You found all three treasures! Click New sea for another round."
            : "Eight probes used. X marks the missed treasures. Click New sea to retry.");
    } catch (IllegalArgumentException invalid) {
        feedbackLabel.setText(onMap ? "That probe was refused; no probe spent."
            : "Outside the sea: refused correctly; no probe spent.");
    }
    refreshSea();
}

void showWindow() {
    var frame = new JFrame("Sonar Treasure Hunt");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var board = new JPanel(new GridLayout(9, 9, 3, 3));
    board.add(new JLabel("Row / Col"));
    for (int column = 1; column <= 8; column++) {
        board.add(new JLabel("" + column, SwingConstants.CENTER));
    }
    for (int row = 0; row < 8; row++) {
        board.add(new JLabel("" + (row + 1), SwingConstants.CENTER));
        for (int column = 0; column < 8; column++) {
            int r = row + 1, c = column + 1;
            var button = new JButton("~");
            button.setPreferredSize(new Dimension(54, 40));
            button.setFont(button.getFont().deriveFont(12f));
            button.setToolTipText("Probe row " + r + ", column " + c);
            button.addActionListener(event -> probeAt(r, c));
            seaButtons[row][column] = button;
            board.add(button);
        }
    }
    var instructions = new JLabel("<html><b>Find 3 treasures with 8 probes.</b> Click a sea square.<br>"
        + "A number is the distance at that time: row steps + column steps.<br>"
        + "For example: one square up and two right is distance 3.</html>");
    var top = new JPanel(new BorderLayout(0, 8));
    top.add(instructions, BorderLayout.NORTH);
    top.add(scoreLabel, BorderLayout.SOUTH);
    var controls = new JPanel();
    var outsideRow = new JButton("Try outside: row 9, col 1");
    outsideRow.addActionListener(event -> probeAt(9, 1));
    var outsideColumn = new JButton("Try outside: row 1, col 9");
    outsideColumn.addActionListener(event -> probeAt(1, 9));
    var restart = new JButton("New sea");
    restart.addActionListener(event -> {
        reset();
        for (var row : seaButtons) for (var button : row) {
            button.setText("~");
            button.setToolTipText("Unexplored sea square");
            button.setBackground(null);
        }
        feedbackLabel.setText("New sea. Click a square; the treasures have moved.");
        refreshSea();
    });
    controls.add(outsideRow);
    controls.add(outsideColumn);
    controls.add(restart);
    clueLog.setEditable(false);
    var bottom = new JPanel(new BorderLayout(0, 8));
    bottom.add(feedbackLabel, BorderLayout.NORTH);
    bottom.add(new JScrollPane(clueLog), BorderLayout.CENTER);
    bottom.add(controls, BorderLayout.SOUTH);
    var content = new JPanel(new BorderLayout(8, 8));
    content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    content.add(top, BorderLayout.NORTH);
    content.add(board, BorderLayout.CENTER);
    content.add(bottom, BorderLayout.SOUTH);
    frame.add(content);
    refreshSea();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Sonar Treasure Hunt needs a graphical display. Run java L02/Check.java M09_SonarTreasureHunt.java to check insideSea.");
        return;
    }
    SwingUtilities.invokeLater(this::showWindow);
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Připravené vstupní body: main, reset, command, probeAt, showWindow.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
/// - `;` — Ukončuje jeden statement (příkaz), podobně jako tečka větu. V záhlaví for odděluje jeho tři
///   části.
/// - `()` — Za jménem: volání akce nebo seznam parameter (parametrů) při jejím popisu. Prázdné závorky
///   znamenají bez předávaných argument (argumentů).
/// - `{}` — Složené závorky ohraničují block (blok kódu). Otevírací a uzavírací závorka patří k sobě.
/// - `.` — Zpřístupňuje něco patřícího hodnotě nebo type (typu) vlevo, například IO.println.
/// - `void` — Tato method (metoda) provede akci a nevrací výslednou hodnotu.
/// - `main` — Tady Java program zahajuje. Ostatní method declaration (popisy metod) se neprovedou jen
///   proto, že jsou v souboru výše.
/// - `int` — Whole-number type (typ pro celá čísla) s omezeným rozsahem.
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `null` — Žádný object (objekt) tu není.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek.
// </editor-fold>
