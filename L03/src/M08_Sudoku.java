// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # L03.08 · Sudoku
///
/// Doplň malé Sudoku 4×4 s čísly 1–4 právě jednou v každém řádku, sloupci a bloku 2×2. Pevná čísla dávají
/// výchozí bod a prázdná políčka můžeš doplňovat.
///
/// **Tvoje mise:** Zámek chrání prázdná políčka místo předtištěných čísel. Oprav place tak, aby platné
/// upravitelné políčko přijalo hodnotu a pevná čísla i neplatné volby zůstaly nedotčené.
///
/// **Dnes trénuješ:** Chraň zápis časným return (návratem): spoj důvody odmítnutí a jediný zápis proveď
/// až tehdy, když žádný neplatí. Souřadnice musí být ověřené před čtením tabulky pevných čísel.
///
/// Použij Run File, ve výběru čísla zvol 2 a klikni na prázdné políčko bezprostředně vpravo od levé horní
/// 1.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F08 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean place(int row, int col, int value) {
    if (complete() || !valid(row, col, value) || clues[row][col] != 0) return false;
    board[row][col] = value;
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F08">
/// Guard (ochranná podmínka) odmítne požadavek před chráněnou prací. Nejdřív přečti odmítané případy
/// a potom se ptej, co zůstává povolené. Short-circuit (zkrácené vyhodnocení) je důležité: u `A || B`
/// Java přeskočí B, pokud A už platí.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Předpověz výpis a vrácenou hodnotu pro `-1`, `3` a `4`. Jen trojka dojde k výpisu. Odmítnutí musí
/// nastat před jakoukoli state change (změnou stavu), která se nemá provést. Přístup na desku sám
/// může potřebovat kontrolu rozsahu, než jej lze bezpečně vyhodnotit. `return false` ukončí toto call
/// (volání metody); automaticky nevrací zpět práci již provedenou předtím.
///
/// `useSlot(3)` předá trojku do `slot`; `boolean` před názvem říká, že výsledkem je true/false. Nyní
/// předpověz slot nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `complete()` — Připravená kontrola ano/ne pro už dokončenou hádanku.
/// - `!valid(row, col, value)` — ! obrací připravené rozhodnutí o platnosti: tato část je true, když je
///   navržené položení neplatné.
/// - `clues[row][col] == 0` — Přečti tabulku pevných čísel; nula znamená, že toto políčko původně nemělo
///   předtištěné číslo.
/// - `return false;` — Dokonči call (volání) beze změny desky.
/// - `board[row][col] = value;` — Chráněný zápis uloží zvolenou hodnotu do přijatého řádku a sloupce.
/// - `return true;` — Oznam volající části, že položení bylo přijato.
///
/// ### Co už obstarává připravená část
///
/// valid už ověřuje hranice, řádky, sloupce a bloky. complete kontroluje dokončenou desku a připravená
/// tlačítka volají place a obnovují své popisky. Upravované pravidlo určuje, které požadavky se smějí
/// dostat k zápisu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nula v clues znamená, že zde nebyla pevná nápověda.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// valid zachovej před přístupem do clues: ověřuje i souřadnice.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odmítající condition (podmínka) pro nápovědu je clues[row][col] != 0.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int[][] clues = {{1, 0, 0, 4}, {0, 4, 1, 0}, {2, 0, 4, 0}, {0, 3, 0, 1}};
int[][] board = new int[4][4];
JButton[][] buttons = new JButton[4][4];
JLabel status = new JLabel();
JComboBox<Integer> number = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4});

void reset() {
    for (int row = 0; row < 4; row++) board[row] = clues[row].clone();
}

boolean valid(int row, int col, int value) {
    if (row < 0 || row >= 4 || col < 0 || col >= 4 || value < 0 || value > 4) return false;
    if (value == 0) return true;
    for (int i = 0; i < 4; i++) {
        if (i != col && board[row][i] == value) return false;
        if (i != row && board[i][col] == value) return false;
    }
    int top = row / 2 * 2;
    int left = col / 2 * 2;
    for (int r = top; r < top + 2; r++) {
        for (int c = left; c < left + 2; c++) {
            if ((r != row || c != col) && board[r][c] == value) return false;
        }
    }
    return true;
}

boolean complete() {
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
            if (board[row][col] == 0 || !valid(row, col, board[row][col])) return false;
        }
    }
    return true;
}

void refresh() {
    boolean finished = complete();
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
            buttons[row][col].setText(board[row][col] == 0 ? "." : Integer.toString(board[row][col]));
            buttons[row][col].setEnabled(clues[row][col] == 0 && !finished);
        }
    }
    status.setText(finished ? "Solved! Restart for another round." : "Rows, columns and 2x2 boxes need 1, 2, 3, 4.");
}

void show() {
    var frame = new JFrame("Mini Sudoku");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(4, 4, 4, 4));
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                boolean accepted = place(row, col, (Integer) number.getSelectedItem());
                refresh();
                if (!accepted) status.setText("Move rejected. Check the row, column and box; the starter guard may also need repair.");
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var restart = new JButton("Restart");
    restart.addActionListener(event -> {
        reset();
        refresh();
    });
    number.setSelectedItem(1);
    controls.add(new JLabel("Number (0 clears):"));
    controls.add(number);
    controls.add(restart);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.setSize(480, 390);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    reset();
    SwingUtilities.invokeLater(this::show);
}

/// Připravené vstupní body: main, reset, show.
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
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

