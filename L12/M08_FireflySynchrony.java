// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L12.08 · FireflySynchrony
///
/// Sleduj pole blikajících světlušek, které postupně hledají společný rytmus. Měň coupling (sílu
/// propojení) a porovnej nezávislé záblesky s davem pulzujícím společně.
///
/// **Tvoje mise:** Vytvoř apply pro všechny control cards (ovládací karty): přijmi konečné Coupling v
/// 0..2, ostatní ignoruj, pro Toggle obrať running a pro Restart zavolej reset s jeho seed. Input
/// listeners (obsluhy vstupu) mají dál předávat karty do jediného dispatcher.
///
/// **Dnes trénuješ:** Sestavíš celý record-pattern dispatcher (směrování podle typu a složek). Rozliš
/// alternativu, vyjmi její data a zachovej hranici mezi směrováním a připraveným algoritmem.
///
/// Po spuštění programu posuň Coupling směrem k 2 a sleduj číslo sladění; pomocí Pause/Run či
/// New phases porovnej záblesky.
void apply(Control control) {
    switch (control) {
        case Coupling(var value) when Double.isFinite(value) && value >= 0 && value <= 2 -> coupling = value;
        case Coupling(var value) -> { }
        case Toggle() -> running = !running;
        case Restart(var seed) -> reset(seed);
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Přidej control card (ovládací kartu), která nastaví všem světluškám stejnou fázi. Nové tlačítko
/// zapoj přes apply, místo změny fází přímo v listener (obsluze události). Porovnej okamžité srovnání
/// s postupným coupling (vazbou): podobný obraz může vzniknout jinou cestou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F35">
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
/// ### Jedna vstupní cesta, čtyři pozorovatelné výsledky
///
/// Stejná control family (rodina ovládání) může přijít z posuvníků, tlačítek nebo kontroly. Switch
/// statement (výběr akcí) provádí účinky bez návratu hodnoty. Coupling potřebuje dvě větve: guarded
/// usable value (platnou hodnotu s podmínkou) a zbývající ignorovanou. Double.isFinite před mezemi
/// vyloučí NaN i nekonečno. Toggle nemá složky; Restart nese jeden seed.
///
/// Neutrální příklad: povolená hlasitost 0..10 změní předchozí 4 na 7 po Volume(7), ale po Volume(20)
/// nechá 4. Předpověz stav po Volume(7), Volume(-1) při počátku 4. Ignorovat chybnou hodnotu znamená
/// nechat starou, nikoli ji nahradit nulou.
///
/// Zde coupling řídí postupné ovlivňování fází, Toggle pozastaví čas a reset znovu zvolí fáze.
/// Zapojení všech účinků do apply dovoluje kontrolu bez okna. New phases neslibuje okamžitou
/// synchronizaci: sleduj změnu čísla order v čase.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Guard (podmínka) nemaže rozpoznanou hodnotu
///
/// Trasuj Coupling s hodnotou 1.5: typ záznamu souhlasí a všechny tři guard conditions (dodatečné
/// podmínky) platí. Hodnota je stále dostupná vybrané akci. Potom trasuj hodnotu -1: guard neprojde a
/// následující Coupling větev záměrně nic nezmění. Začni od přijatého nastavení a předpověz nastavení
/// po tomto odmítnutém příkazu. Toggle nemá žádné složky, zatímco Restart nese seed (počáteční
/// hodnotu); ani jeden nemá omylem použít sílu vazby. Proto jsou rozpoznání typu, ověření podmínky a
/// akce větve tři různé otázky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Před kódem napiš jednu odpovědnost pro každou record alternative (možnost).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Použij Coupling case s guard (podmínkou) a potom zbývající Coupling s prázdným tělem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Nastav coupling z nalezené hodnoty, obrať running a nalezený seed předej reset.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] phase = new double[40];
double[] frequency = new double[40];
double coupling = 1;
boolean running = true;

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
void reset(long seed) {
    var random = new Random(seed);
    for (int i = 0; i < phase.length; i++) {
        phase[i] = random.nextDouble() * Math.PI * 2;
        frequency[i] = 1 + (random.nextDouble() - 0.5) * 0.04;
    }
}

double order() {
    double x = 0;
    double y = 0;
    for (double value : phase) {
        x += Math.cos(value);
        y += Math.sin(value);
    }
    return Math.hypot(x, y) / phase.length;
}

/// Připravený omezený update (aktualizace): jedno volání znamená jeden krok; aktivní oprava je popsaná výše.
void step() {
    double[] next = new double[phase.length];
    for (int i = 0; i < phase.length; i++) {
        double pull = 0;
        for (double other : phase) {
            pull += Math.sin(other - phase[i]);
        }
        next[i] = (phase[i] + 0.03 * (frequency[i] + coupling * pull / phase.length) + Math.PI * 2) % (Math.PI * 2);
    }
    phase = next;
}

/// Připravené Swing UI (rozhraní): vytváří se na EDT; úklid timer (časovače) patří k oknu.
void showWindow() {
    var frame = new JFrame("Firefly Synchrony");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(new Color(10, 20, 35));
            g.fillRect(0, 0, 600, 400);
            for (int i = 0; i < phase.length; i++) {
                g.setColor(Math.cos(phase[i]) > 0.94 ? Color.YELLOW : new Color(50, 70, 35));
                g.fillOval(35 + i % 8 * 70, 30 + i / 8 * 70, 24, 24);
            }
            g.setColor(Color.WHITE);
            g.drawString("Synchrony: " + String.format(Locale.ROOT, "%.2f", order()), 15, 390);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var strength = new JSlider(0, 200, 100);
    strength.addChangeListener(event -> apply(new Coupling(strength.getValue() / 100.0)));
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> apply(new Toggle()));
    var reset = new JButton("New phases");
    reset.addActionListener(event -> {
        apply(new Restart(System.nanoTime()));
        canvas.repaint();
    });
    controls.add(new JLabel("Coupling"));
    controls.add(strength);
    controls.add(pause);
    controls.add(reset);
    var timer = new Timer(25, event -> {
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

sealed interface Control permits Coupling, Toggle, Restart {}
record Coupling(double value) implements Control {}
record Toggle() implements Control {}
record Restart(long seed) implements Control {}

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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `Graphics2D` — Kreslicí nástroj dodaný při vykreslování okna nebo obrázku.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
