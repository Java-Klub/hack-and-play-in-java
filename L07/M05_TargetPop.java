// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L07.05 · TargetPop
///
/// Praskej pohybující se terče proti třicetisekundovým hodinám. V dalším kole zkus překonat vlastní
/// přesnost i skóre.
///
/// **Tvoje mise:** Po vypršení času se hra vrací do připraveného stavu místo ukončení. Oprav afterTime(),
/// aby konečné skóre vydrželo až do úmyslného restartu.
///
/// **Dnes trénuješ:** Enum states (pojmenované stavy): přechod mění jen stavy, kterých se týká. Vypršení
/// RUNNING nesmí omylem obnovit dokončené ani dosud nezačaté kolo.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L07/M05_TargetPop.java`
/// - Kontrola této mise: `java L07/Check.java M05_TargetPop.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a kliknutím na kruh začni časované kolo, potom
/// zasahuj další terče až do vypršení času. Po opravě: ulož soubor, spusť uvedenou kontrolu a
/// zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F21 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
enum Phase {
    READY, RUNNING, FINISHED
}

Phase afterTime(long now) {
    if (phase == Phase.RUNNING && now >= deadline) {
        return Phase.FINISHED;
    }
    return phase;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F21">
/// ### Enum: jedna z pojmenovaných možností
///
/// Enum vymezí konečnou sadu hodnot jednoho type (typu). Jméno type a tečka určují konkrétní hodnotu.
/// Překlep v enum value (hodnotě enum) způsobí compiler error (chybu překladu), na rozdíl od
/// libovolného textu. Variable (proměnná) nese jednu volbu; `==` je pro tyto hodnoty vhodné.
///
/// ```java
/// enum Voice {
///     WHISPER, SING, SHOUT
/// }
///
/// Voice voice = Voice.SING;
/// IO.println(voice == Voice.SHOUT);
/// ```
///
/// Declaration (deklarace) patří vedle method (metod); příkazy můžeš číst uvnitř metody. Porovnání
/// vypíše false, protože SING a SHOUT jsou různé hodnoty. Předpověz je po přiřazení SHOUT. Může
/// jediná variable tvrdit současně SING i SHOUT? Vysvětli, jaké rozporné kombinace by dovolily
/// oddělené boolean (logické proměnné).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `enum Phase` — Definuje tři pojmenované fáze používané klikáním, časováním a kreslením.
/// - `long now` — Celočíselný čas předaný volajícím umožňuje otestovat chování přesně na konci limitu.
/// - `now >= deadline` — Za vypršení se počítá už samotný okamžik deadline (konce limitu), nejen pozdější
///   čas.
/// - `return phase` — Ponechá aktuální stav beze změny, když se žádný přechod neuplatní.
///
/// ### Co už obstarává připravená část
///
/// click() spustí připravené kolo a boduje zásahy jen během hry. Timer (časovač) při aktualizaci volá
/// afterTime(); kreslení i vstup čtou phase, aby se shodly na povolených akcích.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// afterTime() dostává čas výslovně, takže hranici ověříš bez čekání.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Vypršet může jen RUNNING; READY a FINISHED se nemění.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Při now >= deadline vrať FINISHED, nikoli READY.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 400, RADIUS = 26;
final long ROUND_NANOS = 30_000_000_000L, TARGET_NANOS = 1_200_000_000L;
final Random random = new Random();
int targetX, targetY, hits, misses, expired;
long deadline, targetDeadline;

Phase phase;

void spawn(long now) {
    targetX = RADIUS + random.nextInt(WIDTH - RADIUS * 2 + 1);
    targetY = RADIUS + random.nextInt(HEIGHT - RADIUS * 2 + 1);
    targetDeadline = now + TARGET_NANOS;
}

/// restart(): Obnovuje počáteční model Target Pop pro další pokus.
void restart() {
    hits = misses = expired = 0;
    deadline = 0;
    phase = Phase.READY;
    spawn(0);
}

void tick(long now) {
    phase = afterTime(now);
    if (phase != Phase.RUNNING) {
        return;
    }
    if (now >= targetDeadline) {
        expired++;
        spawn(now);
    }
}

/// click(): Spouští první kliknutí, ignoruje hotová kola a boduje kruhový zásah nebo chybu.
void click(int x, int y, long now) {
    tick(now);
    if (phase == Phase.FINISHED) {
        return;
    }
    if (phase == Phase.READY) {
        phase = Phase.RUNNING;
        deadline = now + ROUND_NANOS;
        targetDeadline = now + TARGET_NANOS;
    }
    int dx = x - targetX, dy = y - targetY;
    if (dx * dx + dy * dy <= RADIUS * RADIUS) {
        hits++;
        spawn(now);
    } else {
        misses++;
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(Graphics g) {
    g.setColor(new Color(25, 26, 47));
    g.fillRect(0, 0, WIDTH, HEIGHT + 58);
    if (!(phase == Phase.FINISHED)) {
        g.setColor(new Color(255, 128, 155));
        g.fillOval(targetX - RADIUS, targetY - RADIUS, RADIUS * 2, RADIUS * 2);
        g.setColor(new Color(255, 223, 149));
        g.fillOval(targetX - 10, targetY - 10, 20, 20);
        g.setColor(Color.WHITE);
        g.drawLine(targetX - 5, targetY, targetX + 5, targetY);
        g.drawLine(targetX, targetY - 5, targetX, targetY + 5);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    long seconds = (phase != Phase.READY) ? Math.max(0, (deadline - System.nanoTime() + 999_999_999) / 1_000_000_000) : 30;
    String state = (phase == Phase.FINISHED) ? "TIME!" : (phase != Phase.READY) ? seconds + " seconds" : "CLICK TO START";
    g.drawString(state + "    Hits: " + hits + "    Misses: " + misses, 14, HEIGHT + 24);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Click the circles before they disappear.    Expired: " + expired + "    R: restart", 14, HEIGHT + 47);
    if ((phase == Phase.FINISHED)) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        g.drawString("You popped " + hits + "!  Press R", 110, HEIGHT / 2);
    }
}

/// showWindow(): Dodané pozdější téma: Swing listeners předávají vstupy modelu; časovače končí při
/// zavření okna.
void showWindow() {
    var frame = new JFrame("Target Pop");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 58));
    panel.setFocusable(true);
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            panel.requestFocusInWindow();
            if (event.getY() < HEIGHT) {
                click(event.getX(), event.getY(), System.nanoTime());
            }
            panel.repaint();
        }
    });
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) {
                restart();
            }
        }
    });
    var timer = new Timer(25, event -> {
        tick(System.nanoTime());
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
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
/// - `enum` — Type (typ) s pevnou sadou pojmenovaných hodnot, například READY, PLAYING a FINISHED.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
