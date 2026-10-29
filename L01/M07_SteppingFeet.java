// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L01.07 · SteppingFeet
///
/// **Vrať obě nohy do obrázku.** Dva obdélníky mají společnou vodorovnou pozici `x`. Teď začínají
/// mimo levý okraj okna.
///
/// **Úkol:** V `startingPosition` níže změň druhý řádek tak, aby po provedení obou řádků bylo `x`
/// rovno `80`. První řádek `x = 20;` ponech. Číslo vpravo od `=` je hodnota, kterou řádek uloží
/// do `x`.
///
/// **Výsledek:** Při novém spuštění budou obě nohy vidět a horní popisek ukáže x = 80. Space
/// spustí jejich pohyb; potom zkus klávesou C skrýt pruhy a porovnej zdánlivé rychlosti.

void startingPosition() {
    x = 20;
    x = x + 60;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F03">
/// Variable (proměnná) pojmenuje uloženou hodnotu. Type (typ) popisuje přípustné hodnoty: `int`
/// uchovává celá čísla a `double` může uchovat desetinnou část. Initialization (počáteční přiřazení)
/// dodá první hodnotu; pozdější assignment (přiřazení) ji nahradí.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int charge = 4;
/// charge = charge + 3;
/// IO.println(charge);
/// ```
///
/// Předpověz output (výstup). Nejprve `charge` uchovává čtyřku. Na druhém řádku Java přečte starou
/// hodnotu pro výpočet pravého expression (výrazu) a potom pod stejným jménem uloží sedmičku. `=` je
/// assignment, nikoli matematické tvrzení, že obě strany mají navždy zůstat stejné. Další statement
/// (příkazy) čtoucí toto jméno nyní dostanou sedmičku. Jméno se neaktualizuje samo: hodnotu musí
/// změnit provedené assignment. Variable označenou `final` nelze znovu přiřadit.
///
/// Nyní předpověz konečné charge, pokud počáteční přiřazení použije šest místo čtyř.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `void startingPosition()` — Nastavovací akce bez input (vstupu); void znamená, že mění uloženou
///   pozici místo vrácení výsledku.
/// - `{` — Odpovídající složené závorky sdružují dva nastavovací statement (příkazy) v pořadí provedení.
/// - `x` — Jediná připravená vodorovná pozice sdílená oběma obdélníky.
/// - `x = 20;` — Ulož první pozici; to nebrání jejímu nahrazení pozdějším přiřazením.
/// - `x = -80;` — Pozdější statement (příkaz) nyní přepíše x zápornou pozicí vlevo od plátna.
/// - `=` — Nejdřív vyhodnoť pravou stranu a potom její výsledek ulož do x; nejde o trvalou matematickou
///   rovnost.
///
/// ### Co už obstarává připravená část
///
/// restart volá startingPosition a pozastaví scénu. step dodává stejný pohyb, draw čte stejné x dvakrát a
/// připravená klávesnice mění pruhy, značku a pauzu.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Dvě řešení mohou dát stejné x = 80: pevné assignment (přiřazení) i výpočet ze starého x. Pro tuto
/// misi jsou obě v pořádku. Později je rozlišíš pokusem s jinou počáteční hodnotou. Pravá strana
/// assignment se vyhodnotí před uložením nové hodnoty; obě nohy pak čtou stejné x.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Sleduj v pořadí `int score = 2; score = score + 5; score = 1;`. Uložené hodnoty jsou 2, potom 7 a
/// nakonec 1. Poslední assignment (přiřazení) si dřívější součet nepamatuje. Kreslení čte hodnotu v
/// okamžiku svého spuštění, nikoli historii všech přiřazení. Jaká hodnota by zůstala bez třetího
/// statement (příkazu)?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Porovnej dvě verze: nastav x přímo na 80, nebo k předchozímu x přičti 60. Nejprve obě spusť s
/// počátečním x = 20; potom první assignment (přiřazení) změň na 40. Před spuštěním předpověz obě
/// konečné pozice. Která verze zachovává cíl a která posun?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Čti oba assignment (přiřazení) v pořadí: druhé přepíše výsledek prvního.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Pravá strana může přečíst současné x, než uloží nové x.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Druhý assignment (přiřazení) zapiš x = x + 60;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 360;

double x;
boolean paused, stripes, marker;
void restart() {
    startingPosition();
    paused = true;
    stripes = true;
    marker = false;
}

void step() {
    if (!paused) {
        x += 1.6;
        if (x > WIDTH + 70) {
            x = -70;
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(145, 145, 145));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    if (stripes) {
        for (int i = 0; i < WIDTH; i += 40) {
            g.setColor((i / 40) % 2 == 0 ? Color.BLACK : Color.WHITE);
            g.fillRect(i, 0, 40, HEIGHT);
        }
    }
    g.setColor(Color.WHITE);
    g.fillRect((int)x, 110, 65, 35);
    g.setColor(Color.BLACK);
    g.fillRect((int)x, 230, 65, 35);
    if (marker) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawLine((int)x, 65, (int)x, 300);
    }
    g.setColor(new Color(255, 220, 90));
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString((paused ? "Paused" : "Moving") + " - both feet have x = " + (int)x + " and the same speed", 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_C -> stripes = !stripes;
            case KeyEvent.VK_M -> marker = !marker;
        }
    }
}

void showWindow() {
    var frame = new JFrame("Stepping Feet");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
    var timer = new Timer(20, e -> {
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
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   C: striped / plain   M: shared marker   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Připravené vstupní body: main, restart, draw, showWindow.
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
/// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
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
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
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
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
