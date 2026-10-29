// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
// </editor-fold>

/// # 14.04 · LightsOut
///
/// Zhasni všechna světla klikáním na kříže políček. Staň se testerem, který odhalí přesvědčivé, ale nefér
/// kopie pravidla přepínání.
///
///
/// **Tvoje mise:** Navrhni nejvýše dva experimenty, které odhalí všech šest padělaných pravidel
/// popsaných níže. Každý začíná zhasnutý, provede dva kliky a předpoví konečný počet svítících buněk.
/// Oba kliky mohou mířit na stejné pole. Důkazy musí zároveň přijmout skutečné křížové pravidlo.
/// Očekávání odvoď před spuštěním; hra už funguje.
///
/// **Dnes se učíš:** Functional check (funkční kontrola) může testovat sekvenci a konečný stav.
/// Překryté účinky se ruší; dva počty po jediném kliku proto nelze prostě sečíst.
///
/// Zahraj běžné křížové přepínání. Potom porovnej padělek přeživší Check se svým dvouklikovým
/// experimentem na nakreslené zhasnuté desce.
record CrossExample(int firstRow, int firstColumn, int secondRow, int secondColumn, int expected) {
}

CrossExample[] learnerChecks() {
    return new CrossExample[]{new CrossExample(2, 2, 2, 2, 0)};
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

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Toggle (přepnutí) obrací hodnotu, nikoli nastavuje svit. Dvakrát zasažená buňka zase zhasne. Na
/// jiném třípolíčkovém pruhu přepne akce A pozice 0 a 1; akce B pozice 1 a 2. A následované B nechá
/// svítit 0 a 2, tedy dvě buňky místo čtyř. Předpověz A následované A.
///
/// Skutečná hra přepíná stisknuté pole a existující sousedy nahoře/dole/vlevo/vpravo. Příklad spojuje
/// dvě dvojice zero-based (od nuly počítaných) souřadnic s jedním konečným počtem. Rozlišuj polohu,
/// okraj, překryv a skutečné provedení druhého kliku; hromada opakovaných příkladů nepřidává důkaz.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Šest padělaných pravidel k prozkoumání
///
/// Každý padělek zachová desku 5×5, ale změní jedno pravidlo:
///
/// - Mění se jen stisknuté pole.
/// - Mění se sousedé přes hranu, ale stisknuté pole ne.
/// - Mění se stisknuté pole a diagonální sousedé místo sousedů přes hranu.
/// - Kříž pokračuje přes okraj na protější stranu desky.
/// - Zasažená pole se vždy rozsvítí, i když už svítila.
/// - Druhý klik se úplně vynechá.
///
/// Užitečný experiment dá jiný konečný počet pro skutečné pravidlo a alespoň jeden padělek. Stejný
/// experiment jich může odhalit několik. Check pojmenuje přeživší pravidlo tímto popisem; není třeba
/// číst spouštěč kontrol.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Označ obě množiny zasažených buněk; před počítáním vymaž ty zasažené dvakrát.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// learnerChecks dodává CrossExample(firstRow, firstColumn, secondRow, secondColumn, expected). Check
/// jmenuje každé chybné pravidlo přijímané tvými příklady.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij překryv a hranici pro rozpor tvarů; zvaž i dvojí klik na totéž pole pro odhalení nastavování
/// svitu nebo vynechání druhého kliku. Dodej nejvýše dva platné nezávisle odvozené případy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Vyměň si se spolužákem vlastní pravidlo přepínání. Autor napíše jeden klamný program a druhý dostane
/// jen dva experimenty k odhalení. Pravidlo si dohodněte před hodnocením důkazů.
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
            int[] coordinates = {example.firstRow(), example.firstColumn(), example.secondRow(), example.secondColumn()};
            for (int coordinate : coordinates) {
                if (coordinate < 0 || coordinate >= SIZE) {
                    throw new IllegalArgumentException("Cross examples must use valid game cells 0..4");
                }
            }
            lights = new boolean[SIZE][SIZE];
            flip(example.firstRow(), example.firstColumn());
            flip(example.secondRow(), example.secondColumn());
            int count = 0;
            for (var row : lights) {
                for (boolean on : row) {
                    if (on) count++;
                }
            }
            if (count != example.expected()) {
                throw new IllegalStateException("Two-click example: expected " + example.expected() + " lit cells, actual " + count);
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
