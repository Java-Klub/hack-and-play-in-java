// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
// </editor-fold>

/// # L09.12 · TileStampStudio
///
/// Otiskni vlastní dlaždici po ploše a sleduj, jak z malého motivu vznikne vzor. Ovládání opakování má
/// dovolit řídký i hustý obrazec.
///
/// **Tvoje mise:** Každé přijaté číslo se změní na šest opakování. Nech platnou volbu dojít do živého
/// vzoru a při odmítnutém textu zachovej předchozí nastavení.
///
/// **Dnes trénuješ:** Nejprve proveď parsing (rozbor) a ověření candidate (kandidáta), teprve potom změň
/// sdílený state (stav). Úspěch má znamenat skutečné použití požadované volby, ne pouhý zdařený převod.
///
/// Po Run File namaluj dlaždici, napiš 3 do políčka Repeats a klikni na Repeats pro změnu hustoty vzoru.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean applyRepeats(String text) {
    try {
        int candidate = Integer.parseInt(text.strip());
        if (candidate < 1 || candidate > 12) return false;
        repeats = 6;
        return true;
    } catch (NumberFormatException invalid) {
        return false;
    }
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int candidate` — Odděluje navrženou hodnotu od aktuálního repeats field (položky objektu).
/// - `candidate < 1 || candidate > 12` — Odmítne integer (celé číslo) mimo povolené opakování před změnou
///   model (modelu pravidel).
/// - `repeats = 6` — Potvrdí konstantu místo převedeného návrhu.
/// - `return false;` — Oznámí odmítnutí, aby caller (volající část) mohl zachovat předchozí nastavení
///   vzoru.
///
/// ### Co už obstarává připravená část
///
/// Input (vstup) opakování volá applyRepeats a náhled potom čte repeats field (položku objektu). Kreslení
/// dlaždice, skládání vzoru i PNG export jsou připravené; oprava řeší jednu hranici nastavení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Parsing (rozbor) míří do dočasného candidate, nikoli přímo do field (položky objektu).
/// 2. Obě hranice ověř před assignment (přiřazením).
/// 3. Při úspěchu přiřaď candidate místo původní výchozí hodnoty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] tile = new int[8][8];
int[] palette = {0xFFFFFF, 0x182030, 0xE94B8A, 0x36BDD6, 0xF7CB43};
int brush = 1;
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
    int size = repeats * 8 * 6;
    var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            image.setRGB(x, y, palette[tile[y / 6 % 8][x / 6 % 8]]);
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
                paint(r, c, brush);
                refresh();
                preview.repaint();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var colours = new JComboBox<>(new String[]{"White", "Ink", "Pink", "Cyan", "Gold"});
    colours.setSelectedIndex(1);
    colours.addActionListener(event -> brush = colours.getSelectedIndex());
    var clear = new JButton("Clear");
    clear.addActionListener(event -> {
        tile = new int[8][8];
        refresh();
        preview.repaint();
    });
    var status = new JLabel("Repeated preview");
    var save = new JButton("Export PNG");
    save.addActionListener(event -> {
        try {
            ImageIO.write(render(repeats), "png", Path.of("tile-stamp.png").toFile());
            status.setText("Saved tile-stamp.png");
        } catch (Exception failure) {
            status.setText(failure.getMessage());
        }
    });
    var repeatText = new JTextField("6", 3);
    var apply = new JButton("Repeats");
    apply.addActionListener(event -> {
        boolean accepted = applyRepeats(repeatText.getText());
        status.setText(accepted ? "Repeats: " + repeats : "Use a whole number 1..12");
        preview.repaint();
    });
    controls.add(repeatText);
    controls.add(apply);
    controls.add(colours);
    controls.add(clear);
    controls.add(save);
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Paint mění jednu cell (buňku) dlaždice; render ji opakovaně čte.
/// Nové políčko Repeats volá applyRepeats; stejná hodnota řídí náhled i PNG save (uložení).
void main(String[] args) {
    if (args.length > 0 && args[0].equalsIgnoreCase("q")) {
        return;
    }
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Path` — Umístění v souborech, nikoli obsah souboru.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

