// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L02.01 · CannonPuzzle
///
/// Zamiř dělem na oranžový cíl a sleduj zakřivený let střely v gravitaci. Každý pokus ti umožní viditelně
/// porovnat směr a sílu výstřelu.
///
/// **Tvoje mise:** Výchozí verze nechá střelu spadnout u hlavně, protože vypočtená rychlost je nula.
/// Oprav launchSpeed tak, aby natažení 100 dalo rychlost 9 a natažení vzhůru zachovalo záporné znaménko.
///
/// **Dnes trénuješ:** Arithmetic (aritmetika) a numeric type (číselné typy): záleží i na mezivýpočtech.
/// Například celočíselné 5 / 2 dá 2, zatímco desetinný operand může zachovat 2.5; výsledný návratový typ
/// už ztracenou část neobnoví.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L02/M01_CannonPuzzle.java`
/// - Kontrola této mise: `java L02/Check.java M01_CannonPuzzle.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, táhni od děla směrem vpravo nahoru a pusť myš pro
/// výstřel na oranžový cíl; R připraví další pokus. Po opravě: ulož soubor, spusť uvedenou kontrolu
/// a zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F04 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
double launchSpeed(double drag) {
    return drag * 0.09;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F04">
/// Arithmetic (aritmetika) používá operand type (typy operandů) v okamžiku provedení operace. Jsou-li
/// oba operand u `/` celočíselné, Java zahodí desetinnou část. Pozdější uložení výsledku do `double`
/// ji nevrátí.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int pages = 7;
/// double first = pages / 2;
/// double second = pages / 2.0;
/// double third = (double) pages / 2;
/// ```
///
/// Předpověz tři hodnoty: `3.0`, `3.5`, `3.5`. Cast (převod) `(double)` převede toto použití `pages`
/// před dělením; nezmění deklarovaný type (typ) proměnné. Závorky seskupují výpočty, `*` násobí a `/`
/// dělí. Kromě kladného input (vstupu) ověř i záporný a nulu. Pojmenuj jednotky: vzdálenost a
/// rychlost jsou různé veličiny, i když obě používají `double`. O výsledku rozhoduje celý expression
/// (výraz), nikoli jen type napsaný před jménem method (metody).
///
/// Nyní předpověz všechny tři výsledky, pokud pages obsahuje devítku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `double launchSpeed(double drag)` — Pojmenovaný výpočet přijme vzdálenost natažení se znaménkem a
///   vrátí případně desetinnou rychlost.
/// - `return` — Pošli vypočtenou rychlost zpět do launch místo jejího vypsání.
/// - `drag` — Vzdálenost dodaná při tomto call (volání); vodorovné a svislé volání mohou dostat různé
///   hodnoty.
/// - `*` — Vynásob natažení výsledkem měřítka.
/// - `(9 / 100)` — Závorky vynutí toto dělení jako první; dva celočíselné literal (doslovné zápisy) nyní
///   dají nulu.
/// - `;` — Ukonči vrácený expression (výraz) po jeho vyhodnocení.
///
/// ### Co už obstarává připravená část
///
/// aim omezuje směr a délku natažení. launch volá tvůj výpočet pro obě rychlosti, step přidává gravitaci
/// a rozpoznává zásahy a zbývající část okna dodává myš a timer (časovač).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Spočítej samostatně 9 / 100: obě čísla jsou celá.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Použij desetinný násobek místo zkráceného celočíselného výsledku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať drag * 0.09 nebo drag * 9.0 / 100.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

double x, y, vx, vy, aimX, aimY;
boolean flying, hit, spent;
void restart() {
    x = 60;
    y = 350;
    vx = vy = 0;
    aimX = 150;
    aimY = 270;
    flying = hit = spent = false;
}

void aim(int xx, int yy) {
    if (flying || spent) {
        return;
    }
    double dx = xx - 60, dy = yy - 350, length = Math.hypot(dx, dy);
    if (length > 160) {
        dx *= 160 / length;
        dy *= 160 / length;
    }
    aimX = 60 + Math.max(0, dx);
    aimY = 350 + Math.min(0, dy);
}

void launch() {
    if (flying || spent) {
        return;
    }
    vx = launchSpeed(aimX - 60);
    vy = launchSpeed(aimY - 350);
    flying = true;
}

void step() {
    if (!flying) {
        return;
    }
    x += vx;
    y += vy;
    vy += .24;
    double xx = Math.max(490, Math.min(545, x)), yy = Math.max(275, Math.min(350, y));
    if (Math.hypot(x - xx, y - yy) <= 6) {
        hit = true;
        flying = false;
        spent = true;
    }
    else {
        if (y >= 350 || x > WIDTH + 6 || x<-6) {
            flying = false;
            spent = true;
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(230, 242, 255));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(70, 135, 80));
    g.fillRect(0, 350, WIDTH, 70);
    g.setColor(hit ? Color.GREEN : Color.ORANGE);
    g.fillRect(490, 275, 55, 75);
    g.setColor(Color.DARK_GRAY);
    g.setStroke(new BasicStroke(12));
    g.drawLine(60, 350, (int)(60 + (aimX - 60) * .3), (int)(350 + (aimY - 350) * .3));
    g.fillOval(44, 334, 32, 32);
    if (!flying && !spent) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.GRAY);
        g.drawLine(60, 350, (int)aimX, (int)aimY);
    }
    g.setColor(Color.RED);
    g.fillOval((int)x - 6, (int)y - 6, 12, 12);
    g.setColor(Color.BLACK);
    g.drawString(hit ? "Target hit! R: replay" : spent ? "Missed - R: retry" : "Drag upward/right to aim; release to launch", 15, 25);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    aim(x, y);
}

void drag(int x, int y) {
    aim(x, y);
}

void release(int x, int y) {
    aim(x, y);
    launch();
}

void showWindow() {
    var frame = new JFrame("Cannon Puzzle");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag from the cannon toward your aim; release: fire   R: reset</div></html>", SwingConstants.CENTER);
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
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
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

