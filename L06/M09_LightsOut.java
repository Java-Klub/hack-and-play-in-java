// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
// </editor-fold>

/// # L06.09 · LightsOut
///
/// Zhasni všechna světla klikáním na kříže políček. Staň se testerem, který odhalí přesvědčivé, ale nefér
/// kopie pravidla přepínání.
///
/// **Tvoje mise:** Samotný příklad ze středu neodhalí všechna chybná pravidla. Rozšiř learnerChecks() o
/// platné příklady z desky, které rozliší různé tvary kříže.
///
/// **Dnes trénuješ:** Writing functional checks (psaní funkčních kontrol): vyber vstupy a očekávané
/// výsledky, které vyvrátí chybná pravidla. Tvou prací jsou příklady, nikoli náhradní algoritmus flip().
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L06/M09_LightsOut.java`
/// - Kontrola této mise: `java L06/Check.java M09_LightsOut.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a zkus klikáním zhasnout všechna světla, potom
/// pomocí kontroly hry zjisti, které chybné pravidlo tvé příklady ještě přehlížejí. Po opravě: ulož
/// soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F33 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
record CrossExample(int row, int column, int expected) {
}

CrossExample[] learnerChecks() {
    return new CrossExample[]{new CrossExample(2, 2, 5), new CrossExample(0, 0, 3), new CrossExample(0, 2, 4)};
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F33">
/// ### Checks: vyber příklad, který může nesouhlasit
///
/// Functional check (funkční kontrola) potřebuje input (vstup) a nezávisle předpovězený výsledek.
/// Úspěšný příklad je slabý, pokud uvěřitelné chybné pravidlo odpoví stejně. Přečti samostatný
/// výpočet a jeho příklad; comparison (porovnání) dává boolean (logickou hodnotu) oznamující shodu.
///
/// ```java
/// int twice(int value) {
///     return value + value;
/// }
///
/// void main() {
///     boolean agrees = twice(0) == 0;
///     IO.println(agrees);
/// }
/// ```
///
/// Výpis je true: dvojnásobek nuly je nula. Projde také implementace vracející vždy nulu. Vyber jiný
/// input a očekávaný výsledek odvoď sám. Jakou chybu nový příklad odhalí? Počáteční data každé check
/// (kontroly) udržuj nezávislá, aby jeden příklad omylem nepřipravil další.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `record CrossExample(int row, int column, int expected)` — Jeden example (příklad) spojuje místo
///   kliknutí s očekávaným počtem rozsvícených políček na prázdné desce.
/// - `CrossExample[]` — Array (pole) dovoluje kontrole spustit několik samostatných příkladů.
/// - `new CrossExample(2, 2, 5)` — Existující příklad vybírá platné středové políčko a jeho očekávaný
///   výsledek.
/// - `return` — Předá tvá data příkladů úvodní kontrole i kontrole hry.
///
/// ### Co už obstarává připravená část
///
/// flip() je správně, takže command pro hraní zůstává hratelné. Start ověří příklady proti tomuto
/// pravidlu; Kontrola hry zkouší také pojmenovaná chybná pravidla a hlásí, které tvé příklady
/// neodmítly.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Očekávání odvoď před sestavením check (kontroly)
///
/// Nacvič jinou hračku: na panelu se třemi řádky a dvěma sloupci rozsvítí stisk vlastní cell (buňku)
/// a případnou buňku přímo pod ní.
///
/// ```java
/// record Probe(int row, int column, int expectedLit) {}
/// Probe sample = new Probe(0, 1, 2);
/// ```
///
/// Souřadnice jsou vnitřní index (pozice). Dvě buňky příkladu vypíšeš na papír nezávisle na
/// implementaci. Vrať se ke skutečnému kříži. Každý CrossExample začíná na nové zhasnuté desce pět
/// krát pět a provede jediné kliknutí; `expected` počítá rozsvícené buňky. Element (položky) array
/// oddělují čárky. Nakresli vlastní kliknutí u hranice a vypiš odlišné zasažené buňky. Který chybný
/// kříž by nesouhlasil? Před kontrolou hry dodej a vysvětli vlastní counterexample (protipříklad).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nakresli zhasnutou desku a označ přesně buňky přepnuté jediným vybraným kliknutím. Očekávaný počet
/// odvoď ze značek před voláním implementace.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Původní kliknutí je daleko od všech okrajů. Vyber jiné platné místo, kde hranice změní tvar;
/// pojmenuj chybné pravidlo, které nové očekávání odmítne.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zkus roh a započti stisknutou buňku i pouze existující sousedy ve čtyřech směrech. Sestav
/// CrossExample z vlastních souřadnic a počtu, potom odvoď jiný hraniční případ, který samotný roh
/// nenahradí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int SIZE = 5;
boolean[][] lights = new boolean[SIZE][SIZE];
int moves;
JButton[][] buttons = new JButton[SIZE][SIZE];
JLabel status = new JLabel();

/// flip(): Přepíná střed a platné kolmé sousedy; dvojí použití obnoví desku.
void flip(int row, int col) {
    int[][] offsets = {{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    for (var offset : offsets) {
        int r = row + offset[0];
        int c = col + offset[1];
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
            lights[r][c] = !lights[r][c];
        }
    }
}

boolean solved() {
    for (var row : lights) {
        for (boolean light : row) {
            if (light) {
                return false;
            }
        }
    }
    return true;
}

/// reset(): Obnovuje počáteční kolo Lights Out a maže stav patřící starému kolu.
void reset(long seed) {
    lights = new boolean[SIZE][SIZE];
    var random = new Random(seed);
    for (int i = 0; i < 40; i++) {
        flip(random.nextInt(SIZE), random.nextInt(SIZE));
    }
    if (solved()) {
        flip(2, 2);
    }
    moves = 0;
}

boolean play(int row, int col) {
    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || solved()) {
        return false;
    }
    flip(row, col);
    moves++;
    return true;
}

/// refresh(): Přenáší aktuální hodnoty modelu do popisků tlačítek, jejich povolení a stavového
/// textu.
void refresh() {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            buttons[r][c].setText(lights[r][c] ? "ON" : "off");
            buttons[r][c].setBackground(lights[r][c] ? Color.YELLOW : Color.DARK_GRAY);
            buttons[r][c].setForeground(lights[r][c] ? Color.BLACK : Color.WHITE);
            buttons[r][c].setOpaque(true);
        }
    }
    status.setText((solved() ? "All lights out! " : "Turn every light off. ") + "Moves: " + moves);
}

/// show(): Dodané pozdější téma: Swing callbacks tlačítek volají model a obnovují jeho zobrazení.
void show() {
    var frame = new JFrame("Lights Out");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(SIZE, SIZE, 3, 3));
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                play(row, col);
                refresh();
            });
            grid.add(button);
        }
    }
    var restart = new JButton("New puzzle");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        refresh();
    });
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(restart, BorderLayout.SOUTH);
    refresh();
    frame.setSize(440, 480);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
    verifyExamples();
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}

/// verifyExamples(): Start zkouší tvé příklady proti skutečnému pravidlu hry; dodané
/// uložení/obnovení ponechá kolo beze změny.
void verifyExamples() {
    boolean[][] saved = lights;
    try {
        for (var example : learnerChecks()) {
            if (example.row()<0 || example.row() >= SIZE || example.column()<0 || example.column() >= SIZE) {
                throw new IllegalArgumentException("Cross examples must use valid game cells 0..4");
            }
            lights = new boolean[SIZE][SIZE];
            flip(example.row(), example.column());
            int count = 0;
            for (var row : lights) {
                for (boolean on : row) {
                    if (on) {
                        count++;
                    }
                }
            }
            if (count != example.expected()) {
                throw new IllegalStateException("Cross example has wrong expected result");
            }
        }
    } finally {
        lights = saved;
    }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
/// - `import` — Zpřístupní v souboru jména z knihovny; knihovnu tím nespustí.
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
