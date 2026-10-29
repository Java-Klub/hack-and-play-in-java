// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

/// # L12.05 · KnightTour
///
/// Projeď malou desku šachovým jezdcem a na každé políčko vstup nejvýše jednou. Platný tah má dojít na
/// místo kliknutí, ne na jeho odraz přes úhlopříčku.
///
/// **Tvoje mise:** Sestav apply jako celý command dispatcher (rozesílání příkazů). Move uvnitř desky
/// 5 na 5 volá move se svým sloupcem a řádkem; Move mimo desku vrací false. Restart obnoví desku a
/// yield vrátí true. Pravidlo jezdce i navštívená pole nech metodě move.
///
/// **Dnes trénuješ:** Sestavíš celý record-pattern dispatcher (směrování podle typu a složek). Rozliš
/// alternativu, vyjmi její data a zachovej hranici mezi směrováním a připraveným algoritmem.
///
/// Po spuštění programu klikni na zelený tah jezdce, například na sloupec 1 a řádek 2 při
/// číslování od nuly, a klávesou R začni cestu znovu.
boolean apply(Command command) {
    // Zde sestav úplný výběr příkazů pomocí switch.
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni jiného jezdce: legal-move predicate (podmínku tahu) změň na velblouda, který jde tři pole
/// po jedné ose a jedno po druhé. Dispatch (rozesílání) Move/Restart zachovej. Dokážeš na stejné
/// desce navštívit podobný počet různých polí?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F35">
/// ### Record patterns (vzory záznamů): vyber typ a rozbal data
///
/// ```java
/// sealed interface Badge permits Name, Stars {}
/// record Name(String text) implements Badge {}
/// record Stars(int count) implements Badge {}
/// String label(Badge badge) {
///     return switch (badge) {
///         case Name(var text) -> text;
///         case Stars(var n) when n > 0 -> n + " stars";
///         case Stars(var n) -> "none";
///     };
/// }
/// ```
///
/// `label(new Stars(2))` vrátí `"2 stars"`. Sealed uzavírá alternativy; permits je vyjmenovává.
/// Record pattern rozpozná typ záznamu a dá hodnotám složek místní jména v pořadí deklarace. `var`
/// odvodí jejich typy. Musí platit také when guard (dodatečná podmínka); jinak hledání pokračuje.
/// Poslední Stars větev zpracuje nekladné počty. Každá povolená alternativa zde potřebuje zpracování.
///
/// Předpověz: která větev zpracuje `new Stars(0)` a jaký text vznikne pro `new Name("Nova")`?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// ### Pattern matching (výběr podle tvaru) je směrování, ne druhý herní engine
///
/// Record Move ukládá (x, y) v pořadí deklarace. Jména v pattern (vzoru) obdrží složky ve stejném
/// pořadí; zápis xx a yy pořadí nemění. Guard (podmínka) po when filtruje nalezenou alternativu.
/// Exhaustive switch (úplný výběr) stále potřebuje case pro Move, kterému guard nevyhověl.
///
/// Neutrální příklad: `case Label(var text) when !text.isBlank() -> text.length(); case Label(var
/// text) -> 0;` vrací 3 pro Label("cat") a 0 pro Label(""). Předpověz Label("  "). Block case
/// (bloková větev) může provést akci a pomocí yield dodat výsledek switch.
///
/// Zde apply řeší dispatch (směrování) a meze desky; move řeší skoky i opakovanou návštěvu. Z (0,0)
/// přistane Move(1,2) ve sloupci 1, řádku 2; Move(2,1) je jiný legální cíl. Restart nemá složky, ale
/// má vlastní pattern.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Pořadí složek nese význam
///
/// Deklarace `Move(int x, int y)` určuje pořadí components (složek). Při rozpoznání Move vytvořeného
/// z hodnot 2 a 1 získá xx hodnotu 2 a yy hodnotu 1; místní jména tyto hodnoty neprohodí. Před
/// spuštěním předpověz požadované políčko a porovnej jej s políčkem předaným do move. Prohození
/// souřadnic zde může být také platným skokem jezdce, takže samotné přijetí tahu není dostatečný
/// důkaz. Trasuj i Move mimo desku: musí dojít k odmítající alternativě bez změny desky. Restart je
/// jiná alternativa, nikoli dvojice souřadnic.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Před psaním účinků vypiš všechny Command alternatives (možnosti příkazu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Move potřebuje platný guarded case (větev s podmínkou) i zbývající odmítnutí. Předávej nejdřív x,
/// potom y.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij switch expression (výběr s výsledkem), dvě větve Move a blok Restart volající restart a
/// yield true.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 520, HEIGHT = 420;

int[][] visited;
int x, y, count;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    visited = new int[5][5];
    x = y = 0;
    count = 1;
    visited[0][0] = 1;
}

boolean legal(int xx, int yy) {
    return xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && visited[yy][xx] == 0 && Math.abs(xx - x) * Math.abs(yy - y) == 2;
}

boolean move(int xx, int yy) {
    if (!legal(xx, yy)) {
        return false;
    }
    x = xx;
    y = yy;
    visited[y][x] = ++count;
    return true;
}

boolean won() {
    return count == 25;
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    for (int yy = 0; yy < 5; yy++) {
        for (int xx = 0; xx < 5; xx++) {
            int px = 100 + xx * 60, py = 35 + yy * 60;
            g.setColor((xx + yy) % 2 == 0 ? new Color(235, 225, 205) : new Color(135, 155, 145));
            g.fillRect(px, py, 60, 60);
            if (legal(xx, yy)) {
                g.setColor(new Color(75, 190, 120));
                g.fillOval(px + 22, py + 22, 16, 16);
            }
            if (visited[yy][xx]>0) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("" + visited[yy][xx], px + 23, py + 35);
            }
        }
    }
    g.setColor(new Color(65, 75, 200));
    g.drawOval(105 + x * 60, 40 + y * 60, 50, 50);
    g.setColor(Color.BLACK);
    boolean canContinue = false;
    for (int row = 0; row < 5; row++) {
        for (int column = 0; column < 5; column++) canContinue |= legal(column, row);
    }
    g.drawString(won() ? "All 25 squares visited! R: replay" : !canContinue ? "No legal moves remain. R: try a new tour" : "Visited " + count + " / 25. Green dots are legal next moves.", 20, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        apply(new Restart());
    }
}

void press(int x, int y, int button) {
    if (x >= 100 && y >= 35 && x < 400 && y < 335) {
        apply(new Move((x - 100) / 60, (y - 35) / 60));
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Knight Tour");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click a green legal move   Visit every square once   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

sealed interface Command permits Move, Restart {}
record Move(int x, int y) implements Command {}
record Restart() implements Command {}

void main() {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
    restart();
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `implements` — Říká, že record nebo class (třída) plní roli uvedeného interface (rozhraní).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `sealed` — Omezuje data type (datový typ) na výslovně uvedené alternativy.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
