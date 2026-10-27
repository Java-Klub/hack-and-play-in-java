// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L10.10 · Breakout
///
/// Odrážej míček pálkou ovládanou myší a rozeber zeď cihel. Výhra či ztráta posledního života mají nechat
/// stabilní výsledek, který můžeš oslavit nebo zkusit znovu.
///
/// **Tvoje mise:** Aktualizaci zastaví jen PAUSE; dokončená kola se mohou dál hýbat a ztrácet životy.
/// Omez pohyb na skutečně rozehraný state (stav).
///
/// **Dnes trénuješ:** Lifecycle guard (podmínka životního cyklu) má popsat povolení aktualizace včetně
/// všech zastavených stavů. Tick (časový krok) nesmí měnit hru označenou jako vyhranou či ukončenou.
///
/// Po Run File pohybem myši nastavuj pálku a odrážej míček do cihel; pomocí R spusť nové kolo.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F30 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void step() {
    if (state.equals("PAUSE")) return;
    double previousX = ballX, previousY = ballY;
    ballX += velocityX;
    ballY += velocityY;
    if (ballX < RADIUS || ballX > WIDTH - RADIUS) {
        ballX = Math.max(RADIUS, Math.min(WIDTH - RADIUS, ballX));
        velocityX = -velocityX;
    }
    if (ballY < RADIUS) {
        ballY = RADIUS;
        velocityY = Math.abs(velocityY);
    }
    if (velocityY > 0 && previousY + RADIUS <= BAT_Y && ballY + RADIUS >= BAT_Y
            && ballX + RADIUS >= batX && ballX - RADIUS <= batX + BAT_WIDTH) {
        ballY = BAT_Y - RADIUS;
        velocityY = -Math.abs(velocityY);
        velocityX = (ballX - batX - BAT_WIDTH / 2.0) / (BAT_WIDTH / 2.0) * 4;
    }
    hit: for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 6; column++) {
            int x = 32 + column * 96, y = 40 + row * 28;
            if (!bricks[row][column] || ballX + RADIUS < x || ballX - RADIUS > x + 88
                    || ballY + RADIUS < y || ballY - RADIUS > y + 20) continue;
            bricks[row][column] = false;
            score++;
            if (previousY + RADIUS <= y || previousY - RADIUS >= y + 20) {
                velocityY = -velocityY;
                ballY = previousY < y ? y - RADIUS : y + 20 + RADIUS;
            } else {
                velocityX = -velocityX;
                ballX = previousX < x ? x - RADIUS : x + 88 + RADIUS;
            }
            break hit;
        }
    }
    if (score == 18) state = "YOU WIN";
    if (ballY > HEIGHT + RADIUS) {
        lives--;
        if (lives == 0) state = "GAME OVER";
        else serve();
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F30">
/// ### Input (vstup) požádá; tick (krok) posune čas
///
/// Event (událost) může zaznamenat záměr bez okamžité změny celé simulace. Tick provede jednu
/// omezenou aktualizaci. Kreslení drž oddělené od obou činností.
///
/// ```java
/// int height = 0;
/// int pending = 0;
/// void press() {
///     pending = 2;
/// }
/// void tick() {
///     height += pending;
///     pending = 0;
/// }
/// ```
///
/// Po `press()` je height stále 0. Jeden `tick()` jej změní na 2 a požadavek spotřebuje; další tick
/// jej ponechá na 2. Opakovaný vstup před tímto krokem stále zaznamená jeden požadavek místo dvou
/// aktualizací. Připravený timer (časovač) může tick volat, ale pozastavená nebo dokončená hra nemá
/// pokračovat. Restart musí obnovit počáteční stav.
///
/// Předpověz: co od počátečního stavu udělá pořadí press, tick, press, tick?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `state.equals("PAUSE")` — Rozpozná jen jeden zastavený state (stav), další nerozehrané stavy
///   nepokrývá.
/// - `return;` — Přeskočí pohyb, srážky i změny životů, pokud guard (podmínka) zvolí zastavení.
/// - `if (score == 18) state = "YOU WIN";` — Označí dokončenou desku; další tick (časový krok) musí tento
///   state (stav) respektovat.
///
/// ### Co už obstarává připravená část
///
/// Dlouhý zbytek step už řeší omezené srážky se stěnou, pálkou a cihlami. Myš nastavuje pozici pálky,
/// restart založí novou desku a kreslení zobrazí body, životy i state (stav).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Hra používá PLAY, YOU WIN a GAME OVER.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Povol přesně PLAY místo hádání jednoho zakázaného popisku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Guard (podmínku) nech před všemi update (aktualizacemi), včetně ztráty života.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 400, RADIUS = 7, BAT_WIDTH = 90, BAT_Y = 370;
final boolean[][] bricks = new boolean[3][6];
double ballX, ballY, velocityX, velocityY;
int batX, lives, score;
String state;

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    for (var row : bricks) java.util.Arrays.fill(row, true);
    batX = (WIDTH - BAT_WIDTH) / 2;
    lives = 3;
    score = 0;
    state = "PLAY";
    serve();
}

void serve() {
    ballX = WIDTH / 2.0;
    ballY = 270;
    velocityX = 3.2;
    velocityY = -3.5;
}

void aim(int mouseX) {
    batX = Math.max(0, Math.min(WIDTH - BAT_WIDTH, mouseX - BAT_WIDTH / 2));
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics g) {
    g.setColor(new Color(21, 22, 42));
    g.fillRect(0, 0, WIDTH, HEIGHT + 42);
    Color[] colours = {new Color(255, 113, 133), new Color(255, 191, 92), new Color(93, 217, 211)};
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 6; column++) {
            if (!bricks[row][column]) continue;
            g.setColor(colours[row]);
            g.fillRoundRect(32 + column * 96, 40 + row * 28, 88, 20, 6, 6);
        }
    }
    g.setColor(Color.WHITE);
    g.fillRoundRect(batX, BAT_Y, BAT_WIDTH, 12, 8, 8);
    g.fillOval((int) ballX - RADIUS, (int) ballY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Bricks: " + score + "/18    Attempts: " + lives + "    " + state, 16, HEIGHT + 18);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Mouse: move paddle     R: restart", 16, HEIGHT + 36);
    if (!state.equals("PLAY")) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g.drawString(state + " - press R", 160, 230);
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Breakout - Brick Bash");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 42));
    panel.setFocusable(true);
    panel.addMouseMotionListener(new MouseMotionAdapter() {
        @Override public void mouseMoved(MouseEvent event) { aim(event.getX()); }
        @Override public void mouseDragged(MouseEvent event) { aim(event.getX()); }
    });
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) restart();
        }
    });
    var timer = new Timer(16, event -> {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) { timer.stop(); }
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// step je aktivní hranice; model (model pravidel) ji zpřístupňuje pro opakovatelné check (kontroly) bez okna.
/// Restart obnoví počáteční field (položky objektu); draw čte state (stav); showWindow dodává listener (obsluhy), jeden timer (časovač) a úklid po zavření.
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
/// - `double` — Numeric type (číselný typ) pro přibližná desetinná čísla. Java používá desetinnou tečku.
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

