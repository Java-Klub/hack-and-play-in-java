// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L12.04 · PendulumPainter
///
/// Nech kyvadlo kreslit vlastní světelnou stopu. Měň délku a gravitaci a objevuj, jak stejný počáteční
/// výkyv vytváří různý pohyb.
///
/// **Tvoje mise:** Implementuj makeAcceleration a výsledek zapoj do acceleration. Role přijímá angle
/// a length a vrací -gravity × sin(angle) / length s aktuálním field gravity. Připravené propojení
/// accelerationNow i integraci v step zachovej.
///
/// **Dnes trénuješ:** Napíšeš implementation (implementaci) malého interface (rozhraní) a zapojíš ji
/// do běžícího programu. Výpočet odděl od vstupu, aktualizace a kreslení.
///
/// Po spuštění programu měň Length a Gravity pro zkoumání stopy kyvadla; Pause/Run ovládá pohyb
/// a Reset stopu vymaže a obnoví pokus.
Acceleration makeAcceleration() {
    return (angle, length) -> 0;
}

Acceleration acceleration = (angle, length) -> 0;

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř mimozemské kyvadlo: sinusové vracení nahraď mírným lineárním pravidlem poblíž středu.
/// Porovnej stejný počáteční úhel, délku a gravitaci. Označ výsledek jako nové simulation rule
/// (simulační pravidlo), ne jako přesnější fyzikální model.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F34">
/// ### Interface (rozhraní): požádej vybranou roli o práci
///
/// Interface pojmenovává operaci, kterou dodají jeho implementations (implementace). Volající používá
/// tuto roli bez výběru jejího výpočtu.
///
/// ```java
/// interface Points {
///     int award(int n);
/// }
/// record DoublePoints() implements Points {
///     public int award(int n) {
///         return n * 2;
///     }
/// }
/// ```
///
/// Při `Points rule = new DoublePoints();` vrátí `rule.award(3)` hodnotu 6. `implements Points`
/// slibuje danou operaci; implementace musí dodat její public method (veřejnou metodu). Proměnná rule
/// označuje vybraný objekt, proto volej jeho operaci se současným vstupem místo opakování pevného
/// výpočtu ve volajícím. Jiná implementace může dodat jiná pravidla přes stejné rozhraní.
///
/// Předpověz: co tato implementace vrátí pro `rule.award(5)` a kde by se měnil kód pro trojnásobek
/// vstupu?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// ### Předávej měnící se stav; čti zvolené nastavení
///
/// Acceleration pojmenovává at(angle, length). Interface (rozhraní) odděluje fyzikální pravidlo od
/// integrace: tvoje implementace vrací číslo, zatímco step mění rychlost, úhel a stopu. Matematický
/// contract (dohoda) je zadaný; odvozování fyziky kyvadla není tímto úkolem. Math.sin přijímá radians
/// (radiány).
///
/// Příklad s gravity 200: pro úhel π/2 je sinus 1, proto length 100 dává -2. Při úhlu 0 je výsledek
/// nula. Předpověz výsledek pro -π/2 a délku 100. Větší délka velikost zrychlení zmenšuje.
///
/// Angle a length uvnitř lambda přicházejí s každým voláním. Gravity je field (uložená hodnota)
/// tohoto objektu a čte se při běhu pravidla, aby posuvník měnil už existující implementaci. Výpočet
/// jediného zrychlení při přípravě by zmrazil počáteční stav. Vrácenou roli zapoj místo tiché náhrady
/// v acceleration.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Pro angle a length použij parametry role; současnou gravity čti z field objektu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Vracej výpočet, ne volání step. Sinus dodá Math.sin(angle).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Sestav vracející člen, vyděl jej požadovanou délkou a acceleration inicializuj pomocí factory.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double accelerationNow() {
    return acceleration.at(angle, length);
}

double angle = 0.9;
double velocity;
double length = 180;
double gravity = 300;
boolean running = true;
java.awt.image.BufferedImage trace = new java.awt.image.BufferedImage(600, 450, java.awt.image.BufferedImage.TYPE_INT_RGB);
int ticks;

Point bob() {
    return new Point(300 + (int)(length * Math.sin(angle)), 70 + (int)(length * Math.cos(angle)));
}

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
void reset(long seed) {
    angle = 0.9;
    velocity = 0;
    ticks = 0;
    trace = new java.awt.image.BufferedImage(600, 450, java.awt.image.BufferedImage.TYPE_INT_RGB);
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    Point before = bob();
    velocity += accelerationNow() * 0.03;
    velocity *= 0.999;
    angle += velocity * 0.03;
    Point after = bob();
    var g = trace.createGraphics();
    try {
        g.setColor(Color.getHSBColor(ticks++ / 700.0F, 0.8F, 1));
        g.drawLine(before.x, before.y, after.x, after.y);
    } finally {
        g.dispose();
    }
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Pendulum Painter");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.drawImage(trace, 0, 0, null);
            Point point = bob();
            g.setColor(Color.GRAY);
            g.drawLine(300, 70, point.x, point.y);
            g.setColor(Color.WHITE);
            g.fillOval(point.x - 7, point.y - 7, 14, 14);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 450));
    canvas.setBackground(Color.BLACK);
    var controls = new JPanel();
    var lengthControl = new JSlider(80, 230, 180);
    lengthControl.setPreferredSize(new Dimension(110, 25));
    lengthControl.addChangeListener(event -> length = lengthControl.getValue());
    var gravityControl = new JSlider(50, 600, 300);
    gravityControl.setPreferredSize(new Dimension(110, 25));
    gravityControl.addChangeListener(event -> gravity = gravityControl.getValue());
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var reset = new JButton("Reset");
    reset.addActionListener(event -> {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
        reset(42);
        canvas.repaint();
    });
    controls.add(new JLabel("Length"));
    controls.add(lengthControl);
    controls.add(new JLabel("Gravity"));
    controls.add(gravityControl);
    controls.add(pause);
    controls.add(reset);
    var timer = new Timer(20, event -> {
        if (running) {
/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
            step();
        }
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter(){

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

interface Acceleration { double at(double angle, double length); }


void main() {
/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
    reset(42);
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
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `public` — Zpřístupňuje tuto declaration (deklaraci) dalšímu kódu.
/// - `protected` — Visibility keyword (klíčové slovo přístupnosti) používané připraveným library callback
///   (knihovním zpětným voláním), například paintComponent.
/// - `super` — V super.paintComponent(g) požádá připravenou parent implementation (nadřazenou
///   implementaci), aby nejprve vykreslila běžné pozadí.
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `null` — Žádný object (objekt) tu není.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
