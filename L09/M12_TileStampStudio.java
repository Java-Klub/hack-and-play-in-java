// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
// </editor-fold>

/// # L09.12 · TileStampStudio
///
/// Otiskni vlastní dlaždici po ploše a sleduj, jak z malého motivu vznikne vzor. Ovládání opakování má
/// dovolit řídký i hustý obrazec.
///
/// **Tvoje mise:** Vytvoř applyRepeats(text): přijmi po odstranění okolních mezer whole number (celé
/// číslo) 1–12 včetně, ulož přijatý candidate (návrh) do repeats a vrať true. Pro překlep i číslo mimo
/// bounds (hranice) vrať false a zachovej staré repeats. Náhled 288×288 musí viditelně měnit hustotu
/// vzoru.
///
/// **Dnes trénuješ:** Vytvoř přesné input boundary (rozhraní vstupu): odliš parsing (převod),
/// validation (ověření platnosti) a uložení živého nastavení.
///
/// Po spuštění programu klikáním zapínej či vypínej barvu políček; napiš 3 a klikni na Repeats
/// pro změnu hustoty vzoru. Clear obnoví dlaždici.
boolean applyRepeats(String text) {
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F31">
/// ### Parsing (převod textu) a platná hodnota jsou dvě otázky
///
/// Číselný vstup začíná jako text. Parsing může selhat, protože text není číslo; validation (ověření
/// platnosti) může odmítnout správně zapsané číslo. Obě pravidla rozhoduj zvlášť.
///
/// ```java
/// int readZoom(String text) {
///     try {
///         int zoom = Integer.parseInt(text.strip());
///         if (zoom < 1 || zoom > 4) throw new IllegalArgumentException("Zoom: 1..4");
///         return zoom;
///     } catch (NumberFormatException typo) {
///         return 2;
///     }
/// }
/// ```
///
/// Trace (průchod) pro `" 3 "`: `strip` odstraní okolní mezery, `parseInt` vrátí 3, bounds (hranice) je
/// přijmou. `"oops"` nejde převést, takže `catch` vrátí 2. `"7"` se převede, ale neprojde validation;
/// vyvolaná IllegalArgumentException tuto method (metodu) opustí. Úzký `catch` pro
/// NumberFormatException tyto cesty odliší.
///
/// Desetinný vstup používá `Double.parseDouble`; NaN a infinity (nekonečno) jsou čitelné, ale nejsou
/// finite (konečná čísla). U živého nastavení nejdřív vytvoř local candidate (místní návrh), teprve po
/// validation změň field (sdílenou proměnnou). Předpověz cestu pro `"0"`, `"two"` a `"4"`. Které vstupy
/// mají zachovat předchozí nastavení?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst tuto část
///
/// Live field (živá sdílená proměnná) repeats začíná na 6. Parsing (převod) proveď do local candidate
/// (místního návrhu), aby odmítnutí nepoškodilo nastavení. Zde format failure (chybný zápis) i range
/// failure (chybný rozsah) vracejí false; na rozdíl od stromu špatný rozsah nevyvolává exception
/// (výjimku). Připravené render(repeats) vměstná daný počet opakování do stejného náhledu, takže 3 a 9
/// skutečně vypadají jinak.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odděl navržené číslo od aktuálně zobrazeného nastavení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Do field (sdílené proměnné) repeats smí jen plně přijatý candidate (návrh).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Zkus parsing (převod); pro chybný rozsah či zápis vrať false, jinak ulož candidate a vrať true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Nakresli nesouměrný tile (dílek) 8×8 a porovnej tři a devět opakování. Potom zkus v render()
/// posunout každý druhý row (řádek) otisků do vzoru podobného cihlám.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] tile = new int[8][8];
int[] palette = {0xFFFFFF, 0x182030};
JButton[][] buttons = new JButton[8][8];

boolean paint(int row, int col, int colour) {
    if (row < 0 || row >= 8 || col < 0 || col >= 8 || colour < 0 || colour >= palette.length) {
        return false;
    }
    tile[row][col] = colour;
    return true;
}

/// Připravená tvorba obrazu: generování a kreslení zůstávají viditelné v tomto file (souboru).
BufferedImage render(int repeats) {
    if (repeats < 1 || repeats > 12) {
        throw new IllegalArgumentException("Repeats: 1..12.");
    }
    int size = 288;
    var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            image.setRGB(x, y, palette[tile[(y * repeats * 8 / size) % 8][(x * repeats * 8 / size) % 8]]);
        }
    }
    return image;
}

void refresh() {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            buttons[row][col].setBackground(new Color(palette[tile[row][col]]));
            buttons[row][col].setOpaque(true);
        }
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Tile Stamp Studio");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var preview = new JPanel(){

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(render(repeats), 0, 0, null);
        }
    };
    preview.setPreferredSize(new Dimension(288, 288));
    var grid = new JPanel(new GridLayout(8, 8, 1, 1));
    grid.setPreferredSize(new Dimension(288, 288));
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            int r = row;
            int c = col;
            var button = new JButton();
            buttons[row][col] = button;
            button.addActionListener(event -> {
                paint(r, c, tile[r][c] == 0 ? 1 : 0);
                refresh();
                preview.repaint();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var clear = new JButton("Clear");
    clear.addActionListener(event -> {
        tile = new int[8][8];
        refresh();
        preview.repaint();
    });
    var status = new JLabel("Repeated preview");
    var repeatText = new JTextField("6", 3);
    var apply = new JButton("Repeats");
    apply.addActionListener(event -> {
        boolean accepted = applyRepeats(repeatText.getText());
        status.setText(accepted ? "Repeats: " + repeats : "Use a whole number 1..12");
        preview.repaint();
    });
    controls.add(repeatText);
    controls.add(apply);
    controls.add(clear);
    frame.add(grid, BorderLayout.WEST);
    frame.add(preview, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.add(status, BorderLayout.NORTH);
    refresh();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

int repeats = 6;

void main() {
    SwingUtilities.invokeLater(this::showWindow);
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `null` — Žádný object (objekt) tu není.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
