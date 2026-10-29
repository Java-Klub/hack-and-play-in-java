// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # 11.05 · SlidingTiles
///
/// Posouvej očíslované dílky kolem jedné mezery do správného pořadí. Dejte si s kamarádem stejný hlavolam
/// a porovnejte počet tahů.
///
/// **Tvoje mise:** Nový hlavolam už má započítané tahy použité při míchání. Oprav závěrečné nulování
/// počítadla a zachovej způsob tvorby řešitelných desek.
///
/// **Dnes trénuješ:** Scope and state lifetime (platnost jména a životnost stavu): míchání záměrně
/// používá běžný tah. Jeho účinky patří field (proměnné programu), kterou je třeba po přípravě obnovit,
/// nikoli zastínit.
///
/// Klikni na očíslovaný dílek hned vedle mezery, aby se přesunul na prázdné
/// místo.
void reset(long seed) {
    for (int i = 0; i < 9; i++) {
        tiles[i] = (i + 1) % 9;
    }
    var random = new Random(seed);
    for (int step = 0; step < 100; step++) {
        var choices = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (canMove(i)) {
                choices.add(i);
            }
        }
        slide(choices.get(random.nextInt(choices.size())));
    }
    if (solved()) {
        slide(7);
    }
    int moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F14">
/// ### Scope: které úložiště jméno označuje?
///
/// Field (položka objektu) patří aktuálnímu objektu a může přežít více volání. Local variable (místní
/// proměnná) patří jedinému call (volání) či bloku. Declaration (deklarace) obsahuje type (typ);
/// assignment (přiřazení) do existujícího jména nevytváří další proměnnou. Místní jméno může zastínit
/// stejně napsaný field.
///
/// ```java
/// int volume = 7;
///
/// void whisper() {
///     int volume = 2;
///     IO.println(volume);
/// }
/// ```
///
/// `whisper()` vypíše dvě, zatímco field zůstane sedm: local declaration (místní deklarace) vytvořila
/// jiné úložiště. Předpověz pozdější čtení field; `this.volume` podle potřeby výslovně označí field.
/// Která hodnota má přežít několik volání a která patří pouze dočasnému výpočtu?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `new Random(seed)` — Začíná opakovatelné míchání podle dodaného seed (počáteční hodnoty).
/// - `choices.add(i)` — Přidá právě povolenou pozici dílku mezi kandidáty pro míchání.
/// - `slide(` — Volá skutečnou method (metodu) tahu včetně běžného zvýšení počítadla.
/// - `int moves = 0` — Lokální deklarace, jejíž životnost skončí s tímto voláním metody.
///
/// ### Co už obstarává připravená část
///
/// canMove() hledá dílky vedle mezery a slide() dílek přesune i započítá. reset() míchá legálními tahy,
/// aby hlavolam zůstal řešitelný; reakce tlačítek a kreslení jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Seed řídí pořadí dílků, nikoli počítadlo tahů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Počítadlo zobrazené refresh() je field nad methods.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// U resetovacího přiřazení odeber lokální deklaraci int.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Nahraď náhodné míchání pěti zvolenými platnými posuny přes existující slide. Spolužákovi dej
/// výslednou desku a opačné řešení si nech. Příprava smí měnit dílky, ale zobrazené počítadlo hráčových
/// tahů musí začít nulou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] tiles = new int[9];
int moves;
JButton[] buttons = new JButton[9];
JLabel status = new JLabel();

int gap() {
    for (int i = 0; i < tiles.length; i++) {
        if (tiles[i] == 0) {
            return i;
        }
    }
    throw new IllegalStateException("The board needs a gap.");
}

/// canMove(): Přijme dílek vzdálený právě jeden kolmý krok od mezery bez přechodu mezi konci řádků.
boolean canMove(int index) {
    if (index < 0 || index >= tiles.length) {
        return false;
    }
    int empty = gap();
    return Math.abs(index / 3 - empty / 3) + Math.abs(index % 3 - empty % 3) == 1;
}

void slide(int index) {
    int empty = gap();
    tiles[empty] = tiles[index];
    tiles[index] = 0;
}

boolean solved() {
    for (int i = 0; i < 8; i++) {
        if (tiles[i] != i + 1) {
            return false;
        }
    }
    return tiles[8] == 0;
}

boolean play(int index) {
    if (!canMove(index) || solved()) {
        return false;
    }
    slide(index);
    moves++;
    return true;
}

/// refresh(): Přenáší aktuální hodnoty modelu do popisků tlačítek, jejich povolení a stavového
/// textu.
void refresh() {
    for (int i = 0; i < 9; i++) {
        buttons[i].setText(tiles[i] == 0 ? "" : Integer.toString(tiles[i]));
        buttons[i].setEnabled(tiles[i] != 0 && !solved());
    }
    status.setText((solved() ? "Solved! " : "Click a tile next to the gap. ") + "Moves: " + moves);
}

/// show(): Dodané pozdější téma: Swing callbacks tlačítek volají model a obnovují jeho zobrazení.
void show() {
    var frame = new JFrame("Sliding Tiles");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(3, 3, 4, 4));
    for (int i = 0; i < 9; i++) {
        int index = i;
        var button = new JButton();
        buttons[i] = button;
        button.addActionListener(event -> {
            play(index);
            refresh();
        });
        grid.add(button);
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
    frame.setSize(380, 390);
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
