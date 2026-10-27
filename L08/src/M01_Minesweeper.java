// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # L08.01 · Minesweeper
///
/// Odkrývej bezpečná políčka a podle čísel se vyhýbej skrytým minám. Nápověda pomůže jen tehdy, když
/// započítá každé okolní nebezpečí.
///
/// **Tvoje mise:** V zobrazených počtech chybí miny šikmo. Oprav adjacent(), aby počítalo všechna platná
/// sousední políčka, ale nikdy samotné dotazované políčko.
///
/// **Dnes trénuješ:** Grid neighbours (sousedé v mřížce): čtvercové políčko může mít až osm sousedů.
/// Rozliš kontrolu mezí, vynechání středu a podmínku, kterou chceš počítat.
///
/// Spusť Run File a kliknutím odkryj políčko, potom podle čísla vybírej bezpečné sousedy nebo označuj
/// miny v režimu Flag mode.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F18 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int adjacent(int row, int col) {
    int count = 0;
    for (int r = row - 1; r <= row + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (inside(r, c) && (r != row || c != col) && mines[r][c]) {
                count++;
            }
        }
    }
    return count;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F18">
/// ### Grids: nejdřív řádek, potom sloupec
///
/// V array of rows (poli řádků) první index (pozice) vybírá řádek a druhý pozici v něm. Řádky mohou
/// mít různé délky. Neighbour (soused) je posun od buňky, nikoli další absolutní pozice. Před
/// kontrolou existence kandidáta urči povolené směry.
///
/// ```java
/// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
/// int row = 0;
/// int column = 1;
/// IO.println(mosaic[row][column + 1]);
/// ```
///
/// Výstup je devět: řádek nula a sloupec dva vyberou tento element (prvek). Pohyb dolů mění row;
/// doprava mění column. Který z těchto pohybů z pravé horní cell (buňky) zůstane uvnitř? U úhlopříčky
/// se mění obě souřadnice; bounds (meze) musíš ověřit před přístupem ke kandidátovi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `row - 1` — Začíná jeden řádek nad dotazovaným políčkem.
/// - `r <= row + 1` — Zahrnuje kromě středního řádku i řádek bezprostředně pod ním.
/// - `r != row || c != col` — Vynechá jen dotazovaný střed: nejméně jedna souřadnice se musí lišit.
/// - `mines[r][c]` — Čte, zda právě tento kandidátní soused obsahuje minu.
///
/// ### Co už obstarává připravená část
///
/// inside() poskytuje dotaz na meze. reveal() už odkrývá bezpečné oblasti pomocí recursive flood fill
/// (rekurzivního rozlévání); hodnoty nápověd získává z adjacent(). Vlajky, miny i okno jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Okolí 3 × 3 obsahuje střed a osm možných sousedů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// inside() zajišťuje bezpečné hranice rohů a okrajů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odeber omezení na stejný řádek/sloupec; ponech inside() a vynechání středu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int SIZE = 6;
final int MINE_COUNT = 6;
boolean[][] mines = new boolean[SIZE][SIZE];
boolean[][] revealed = new boolean[SIZE][SIZE];
boolean[][] flags = new boolean[SIZE][SIZE];
boolean lost;
int opened;
JButton[][] buttons = new JButton[SIZE][SIZE];
JLabel status = new JLabel();
JToggleButton flagMode = new JToggleButton("Flag mode");

boolean inside(int row, int col) {
    return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
}

/// reset(): Obnovuje počáteční kolo Minesweeper a maže stav patřící starému kolu.
void reset(long seed) {
    mines = new boolean[SIZE][SIZE];
    revealed = new boolean[SIZE][SIZE];
    flags = new boolean[SIZE][SIZE];
    var random = new Random(seed);
    int placed = 0;
    while (placed < MINE_COUNT) {
        int row = random.nextInt(SIZE);
        int col = random.nextInt(SIZE);
        if (!mines[row][col]) {
            mines[row][col] = true;
            placed++;
        }
    }
    lost = false;
    opened = 0;
}

boolean won() {
    return !lost && opened == SIZE * SIZE - MINE_COUNT;
}

/// reveal(): Dodané rekurzivní odkrývání označí pole před návštěvou sousedů a otevře bezpečné
/// nulové oblasti.
boolean reveal(int row, int col) {
    if (!inside(row, col) || revealed[row][col] || flags[row][col] || lost || won()) {
        return false;
    }
    revealed[row][col] = true;
    if (mines[row][col]) {
        lost = true;
    } else {
        opened++;
        if (adjacent(row, col) == 0) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    reveal(r, c);
                }
            }
        }
    }
    return true;
}

boolean toggleFlag(int row, int col) {
    if (!inside(row, col) || revealed[row][col] || lost || won()) {
        return false;
    }
    flags[row][col] = !flags[row][col];
    return true;
}

/// refresh(): Přenáší aktuální hodnoty modelu do popisků tlačítek, jejich povolení a stavového
/// textu.
void refresh() {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            String text = flags[r][c] ? "F" : "?";
            if (revealed[r][c] || (lost && mines[r][c])) {
                int count = adjacent(r, c);
                text = mines[r][c] ? "*" : count == 0 ? "" : Integer.toString(count);
            }
            buttons[r][c].setText(text);
            buttons[r][c].setEnabled(!revealed[r][c] && !lost && !won());
        }
    }
    status.setText(lost ? "Mine! Start a new board." : won() ? "All safe cells found!" : "Safe cells: " + opened + "/" + (SIZE * SIZE - MINE_COUNT) + ". First click can hit a mine.");
}

/// show(): Dodané pozdější téma: Swing callbacks tlačítek volají model a obnovují jeho zobrazení.
void show() {
    var frame = new JFrame("Minesweeper");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(SIZE, SIZE, 2, 2));
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                if (flagMode.isSelected()) {
                    toggleFlag(row, col);
                }
                else {
                    reveal(row, col);
                }
                refresh();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var restart = new JButton("New board");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        flagMode.setSelected(false);
        refresh();
    });
    controls.add(flagMode);
    controls.add(restart);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.setSize(520, 510);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

