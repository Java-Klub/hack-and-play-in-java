// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L10.09 · Pong
///
/// Vyzvi kamaráda na hru s pálkami do pěti bodů. Míček se má pohybovat při rozehrané výměně a zastavit,
/// jakmile je znám vítěz.
///
/// **Tvoje mise:** Hra se dál pohybuje i po určení vítěze, protože zastavovací podmínka neodpovídá
/// žádnému běžnému state (stavu) hry. Oprav povolení aktualizace včetně hranice dokončeného zápasu.
///
/// **Dnes trénuješ:** Tick (časový krok) závisí na lifecycle state (stavu životního cyklu) hry. Značka
/// winner rozhoduje, zda krok smí posunout pálky a míček; kreslení má zůstat možné i po konci hry.
///
/// Po Run File ovládej levou pálku pomocí W/S a pravou pomocí Up/Down, hraj do pěti bodů a klávesou R
/// začni znovu.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F30 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void step() {
    if (winner == -1) return;
    leftY = clampPaddle(leftY + (leftDown ? 5 : 0) - (leftUp ? 5 : 0));
    rightY = clampPaddle(rightY + (rightDown ? 5 : 0) - (rightUp ? 5 : 0));
    ballX += velocityX;
    ballY += velocityY;
    if (ballY < RADIUS) {
        ballY = RADIUS;
        velocityY = Math.abs(velocityY);
    }
    if (ballY > HEIGHT - RADIUS) {
        ballY = HEIGHT - RADIUS;
        velocityY = -Math.abs(velocityY);
    }
    if (velocityX < 0 && ballX - RADIUS <= 36 && ballX + RADIUS >= 24
            && ballY + RADIUS >= leftY && ballY - RADIUS <= leftY + PADDLE) {
        bounce(1, leftY);
    }
    if (velocityX > 0 && ballX + RADIUS >= WIDTH - 36 && ballX - RADIUS <= WIDTH - 24
            && ballY + RADIUS >= rightY && ballY - RADIUS <= rightY + PADDLE) {
        bounce(-1, rightY);
    }
    if (ballX < -RADIUS) {
        rightScore++;
        serve(-1);
    } else if (ballX > WIDTH + RADIUS) {
        leftScore++;
        serve(1);
    }
    if (leftScore >= 5) winner = 1;
    if (rightScore >= 5) winner = 2;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F30">
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
/// - `winner == -1` — Žádný běžný state (stav) zde nepoužívá -1: nula znamená neurčeného vítěze, jedna či
///   dvě určuje vítěze.
/// - `return;` — Předčasně opustí celý update (aktualizaci), takže vše pod ním přeskočí.
/// - `leftScore >= 5` — Počet bodů alespoň pět ukončí zápas vítězstvím levého hráče.
///
/// ### Co už obstarává připravená část
///
/// Připravená kontrola srážek odráží míček od stěn a pálek; serve po bodu zahájí novou výměnu. Keyboard
/// flag (příznaky kláves) posouvají pálky; restart obnoví skóre i značku neurčeného vítěze.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Winner má hodnoty 0, 1 a 2.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Stop guard (podmínka zastavení) patří před pálky, míč i skóre.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Dokončený tick (časový krok) nezmění ani pálku s drženou klávesou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 400, PADDLE = 80, RADIUS = 7;
double leftY, rightY, ballX, ballY, velocityX, velocityY;
int leftScore, rightScore, winner;
boolean leftUp, leftDown, rightUp, rightDown;

/// Připravený restart: obnoví počáteční model (model pravidel) před použitím v UI (rozhraní).
void restart() {
    leftY = rightY = (HEIGHT - PADDLE) / 2.0;
    leftScore = rightScore = winner = 0;
    clearKeys();
    serve(1);
}

void clearKeys() {
    leftUp = leftDown = rightUp = rightDown = false;
}

void serve(int direction) {
    ballX = WIDTH / 2.0;
    ballY = HEIGHT / 2.0;
    velocityX = direction * 4;
    velocityY = 2.5;
}

double clampPaddle(double y) {
    return Math.max(0, Math.min(HEIGHT - PADDLE, y));
}

void bounce(int direction, double paddleY) {
    ballX = direction > 0 ? 36 + RADIUS : WIDTH - 36 - RADIUS;
    velocityX = direction * 4;
    velocityY = (ballY - paddleY - PADDLE / 2.0) / (PADDLE / 2.0) * 4;
    if (Math.abs(velocityY) < 0.8) velocityY = 0.8;
}

/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
void draw(Graphics g) {
    g.setColor(new Color(16, 23, 40));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(54, 65, 84));
    for (int y = 0; y < HEIGHT; y += 24) g.fillRect(WIDTH / 2 - 1, y, 2, 12);
    g.setColor(new Color(71, 230, 202));
    g.fillRect(24, (int) leftY, 12, PADDLE);
    g.setColor(new Color(255, 172, 91));
    g.fillRect(WIDTH - 36, (int) rightY, 12, PADDLE);
    g.setColor(Color.WHITE);
    g.fillOval((int) ballX - RADIUS, (int) ballY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
    g.drawString(leftScore + "    " + rightScore, WIDTH / 2 - 42, 38);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString("Left: W / S     Right: Up / Down     First to 5     R: restart", 98, HEIGHT - 12);
    if (winner != 0) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g.drawString((winner == 1 ? "LEFT" : "RIGHT") + " WINS!  Press R", 150, HEIGHT / 2);
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Pong - Paddle Duel");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
/// Připravené kreslení: čte hodnoty model (modelu pravidel); repaint (překreslení) není další tah.
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_W -> leftUp = pressed;
                case KeyEvent.VK_S -> leftDown = pressed;
                case KeyEvent.VK_UP -> rightUp = pressed;
                case KeyEvent.VK_DOWN -> rightDown = pressed;
                case KeyEvent.VK_R -> { if (pressed) restart(); }
            }
        }
        @Override public void keyPressed(KeyEvent event) { change(event, true); }
        @Override public void keyReleased(KeyEvent event) { change(event, false); }
    });
    var timer = new Timer(16, event -> {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) { clearKeys(); }
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
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `final` — Pojmenované hodnotě už nelze přiřadit jinou. Odkazovaný mutable object (měnitelný objekt)
///   se ale dál měnit může.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
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
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `@Override` — Označuje dodanou náhradu knihovní akce, například kreslení.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

