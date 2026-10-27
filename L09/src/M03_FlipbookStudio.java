// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;
// </editor-fold>

/// # L09.03 · FlipbookStudio
///
/// Nakresli několik malých snímků a sleduj, jak z nich vzniká pohyb. Undo (vrácení změny) má dovolit
/// odvážné pokusy a vracet nejnovější tah jako první.
///
/// **Tvoje mise:** Undo nyní obnovuje nejstarší změnu místo nejnovější. Oprav výběr odebrané změny a
/// zachovej chování pro prázdnou historii.
///
/// **Dnes trénuješ:** Mutable List (měnitelný seznam) může fungovat jako stack (zásobník): změny
/// přicházejí v časovém pořadí a vracejí se opačně. Každý Edit uchovává původní data pro obnovení jednoho
/// pixel (obrazového bodu).
///
/// Po Run File namaluj políčko, změň barvu klávesou C, namaluj je znovu a stiskni Z pro vrácení;
/// klávesami 1–4 přepínej snímky animace.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean undo() {
    if (history.isEmpty()) return false;
    Edit edit = history.removeLast();
    frames[edit.frame()][edit.y()][edit.x()] = edit.oldColour();
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `history.isEmpty()` — Před odebráním ověří, zda existuje změna k vrácení.
/// - `history.removeFirst()` — Vrátí a odebere nejdříve uložený Edit; zvaž, zda Undo potřebuje právě
///   tento konec.
/// - `edit.frame()` — Čte record component (složku record), která určuje obnovovaný snímek.
/// - `frames[edit.frame()][edit.y()][edit.x()]` — Vybere snímek, potom řádek a v něm konkrétní pixel
///   (obrazový bod).
///
/// ### Co už obstarává připravená část
///
/// Kreslicí akce uloží Edit před změnou pixel (obrazového bodu). Undo obnoví původní barvu; přepínání
/// snímků, přehrávání náhledu i místní uložení obrázku jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Další Edit se přidává na konec history.
/// 2. Undo potřebuje poslední element (prvek) a musí jej také odstranit.
/// 3. Obnov uložené frame/x/y/colour, nikoli právě zvolený snímek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 430;

int[][][] frames;
int edited, played, colour, paintColour;
boolean playing, drawing;
Color[] palette = {Color.WHITE, Color.BLACK, new Color(225, 75, 75), new Color(60, 130, 220), new Color(55, 175, 105)};
String message;
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    frames = new int[4][16][16];
    history.clear();
    edited = played = 0;
    colour = 1;
    playing = drawing = false;
    message = "Draw four frames; Space previews them";
}

void paintCell(int x, int y, int value) {
    if (x >= 0 && y >= 0 && x < 16 && y < 16) {
        history.add(new Edit(edited, x, y, frames[edited][y][x]));
        frames[edited][y][x] = value;
    }
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    if (playing) {
        played = (played + 1) % 4;
    }
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save(File file) throws IOException {
    var strip = new BufferedImage(64, 16, BufferedImage.TYPE_INT_RGB);
    for (int frame = 0; frame < 4; frame++) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                strip.setRGB(frame * 16 + x, y, palette[frames[frame][y][x]].getRGB());
            }
        }
    }
    ImageIO.write(strip, "png", file);
}

/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
void save() {
    try {
/// Připravená export boundary (hranice ukládání): používá místní cestu; encoding (kódování) zajišťuje JDK.
        save(new File("flipbook.png"));
        message = "Saved flipbook.png (64 x 16 sprite strip)";
    } catch (IOException e) {
        message = "Save failed: " + e.getMessage();
    }
}

void paintAt(int x, int y) {
    if (x >= 25 && x < 281 && y >= 55 && y < 311) {
        paintCell((x - 25) / 16, (y - 55) / 16, paintColour);
    }
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics2D g) {
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
            g.setColor(palette[frames[edited][y][x]]);
            g.fillRect(25 + x * 16, 55 + y * 16, 16, 16);
            g.setColor(new Color(205, 205, 205));
            g.drawRect(25 + x * 16, 55 + y * 16, 16, 16);
        }
    }
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
            g.setColor(palette[frames[played][y][x]]);
            g.fillRect(350 + x * 12, 75 + y * 12, 12, 12);
        }
    }
    g.setColor(Color.DARK_GRAY);
    g.drawRect(350, 75, 192, 192);
    g.drawString("Editing frame " + (edited + 1), 25, 35);
    g.drawString("Preview frame " + (played + 1) + (playing ? " - playing" : " - paused"), 350, 45);
    for (int i = 0; i < 4; i++) {
        g.setColor(edited == i ? Color.ORANGE : Color.LIGHT_GRAY);
        g.fillRect(25 + i * 65, 335, 55, 32);
        g.setColor(Color.BLACK);
        g.drawString("" + (i + 1), 48 + i * 65, 356);
    }
    g.setColor(palette[colour]);
    g.fillRect(360, 310, 30, 30);
    g.setColor(Color.BLACK);
    g.drawRect(360, 310, 30, 30);
    g.drawString("Current ink", 400, 330);
    g.drawString(message, 20, 405);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
        edited = code - KeyEvent.VK_1;
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_Z -> undo();
            case KeyEvent.VK_C -> colour = colour % 4 + 1;
            case KeyEvent.VK_SPACE -> playing = !playing;
            case KeyEvent.VK_S -> save();
        }
    }
}

void press(int x, int y, int button) {
    if (x >= 25 && x < 285 && y >= 335 && y < 367) {
        edited = (x - 25) / 65;
    }
    else {
        drawing = true;
        paintColour = button == MouseEvent.BUTTON3 ? 0 : colour;
        paintAt(x, y);
    }
}

void drag(int x, int y) {
    if (drawing) {
        paintAt(x, y);
    }
}

void release(int x, int y) {
    drawing = false;
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Flipbook Studio");
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
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    var timer = new Timer(180, e -> {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Draw: left mouse   Erase: right mouse   1-4: frame   C: ink   Space: play   S: PNG<br>R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

record Edit(int frame, int x, int y, int oldColour) {}
List<Edit> history = new ArrayList<>();

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// PaintCell uloží starou barvu; přidaná klávesa Z volá undo.
/// Step posune jen náhled; draw ukáže editor i náhled; save uloží připravený sprite strip (pruh snímků).
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
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
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throws` — V method declaration (popisu metody) říká volajícím, jaké selhání mohou potřebovat
///   zpracovat.
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

