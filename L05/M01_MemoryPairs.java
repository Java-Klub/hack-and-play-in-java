// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # L05.01 · MemoryPairs
///
/// Otoč dvě karty a zapamatuj si, kde se skrývají jejich dvojice. Hra není fér, když různé karty ukazují
/// stejný obrázek.
///
/// **Tvoje mise:** Žádná vybraná karta neodhalí líc. Sestav visible(index): vrať kartu na dané pozici,
/// pokud je matched, first nebo second; jinak vrať skrytou značku nula. Různé pozice musí číst své
/// vlastní hodnoty.
///
/// **Dnes trénuješ:** Arrays (pole): související pole popisují stejné očíslované pozice. Přečti
/// požadovanou pozici a zachovej pravidlo, které rozhoduje o jejím odkrytí.
///
/// Klikni na zakrytou kartu. Nehotové zobrazení ji stále skrývá, přestože už jde o první vybranou
/// pozici. Nejdřív sestav pravidlo odhalení a potom zkoušej paměť.
int visible(int index) {
    if (matched[index] || index == first || index == second) {
        return cards[index];
    }
    return 0;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F16">
/// ### Arrays: záleží na pozici
///
/// Array (pole) uchovává pevný počet hodnot jednoho element type (typu prvku). Hranaté závorky
/// vybírají pozici: první index (pozice) je nula a `length` je počet. Čtení jednoho element (prvku)
/// nemění ostatní. Použitý index musí existovat; prázdné array nemá první element.
///
/// ```java
/// String[] tools = {"pen", "tape", "chalk"};
/// int chosen = 1;
/// IO.println(tools[chosen]);
/// IO.println(tools.length);
/// ```
///
/// Tyto statement (příkazy) patří dovnitř method (metody). Index jedna vybere `tape`; počet je tři.
/// Co se změní při `chosen` rovném dvěma? Který index už bude první neplatný?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Parallel arrays (související pole) používají stejné číslo pozice: cards[index] je líc,
/// matched[index] říká, zda už byla nalezena dvojice. first a second ukládají právě vybrané pozice.
///
/// | cards | first | žádaný index | matched zde | odpověď |
/// | --- | --- | --- | --- | --- |
/// | 2, 5, 2, 5 | 1 | 0 | false | 0 |
/// | 2, 5, 2, 5 | 1 | 1 | false | 5 |
/// | 2, 5, 2, 5 | 1 | 2 | true | 2 |
///
/// Jedním rozhodnutím odliš veřejné líce od skrytých. Při odhalení musí žádaný index vybrat oba
/// související údaje. Předpověz poslední pozici při second 3. Dvojice a tahy obstarává připravený kód;
/// visible pouze odpovídá, co lze ukázat.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vypiš tři nezávislé důvody pro viditelnou kartu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Platí-li některý důvod, čti cards na žádaném indexu; jinak ponech nulu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Spoj matched[index], first a second pomocí OR a vrať vybranou kartu nebo skrytou značku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Vytvoř beacon card (orientační kartu): jedna zvolená pozice zůstane viditelná i před výběrem. Přidej
/// tuto podmínku k odhalování a zvol místo, které pomůže začátečníkovi bez odkrytí všech dvojic.
/// Vysvětli, které karty zůstávají soukromé.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] cards = new int[12];
boolean[] matched = new boolean[12];
int first = -1;
int second = -1;
int attempts;
int pairs;
JButton[] buttons = new JButton[12];
JLabel status = new JLabel();

/// reset(): Obnovuje počáteční kolo Memory Pairs a maže stav patřící starému kolu.
void reset(long seed) {
    var values = new ArrayList<Integer>();
    for (int value = 1; value <= 6; value++) {
        values.add(value);
        values.add(value);
    }
    Collections.shuffle(values, new Random(seed));
    for (int i = 0; i < cards.length; i++) {
        cards[i] = values.get(i);
    }
    matched = new boolean[12];
    first = -1;
    second = -1;
    attempts = 0;
    pairs = 0;
}

/// play(): Sleduje dvě otočení, ponechá neshodu viditelnou a shodnou dvojici trvale označí.
boolean play(int index) {
    if (index < 0 || index >= cards.length || matched[index] || index == first || index == second || pairs == 6) {
        return false;
    }
    if (second != -1) {
        first = -1;
        second = -1;
    }
    if (first == -1) {
        first = index;
    } else {
        second = index;
        attempts++;
        if (cards[first] == cards[second]) {
            matched[first] = true;
            matched[second] = true;
            pairs++;
            first = -1;
            second = -1;
        }
    }
    return true;
}

/// refresh(): Přenáší aktuální hodnoty modelu do popisků tlačítek, jejich povolení a stavového
/// textu.
void refresh() {
    for (int i = 0; i < cards.length; i++) {
        int value = visible(i);
        buttons[i].setText(value == 0 ? "?" : Integer.toString(value));
        buttons[i].setEnabled(!matched[i] && pairs < 6);
    }
    String guidance = pairs == 6 ? "All pairs found!" : second != -1
            ? "Not a match: click another covered card." : first != -1
            ? "Choose a second card." : "Find matching pairs.";
    status.setText("<html>" + guidance + "<br>Pairs: " + pairs + "/6. Attempts: " + attempts + "</html>");
}

/// show(): Dodané pozdější téma: Swing callbacks tlačítek volají model a obnovují jeho zobrazení.
void show() {
    var frame = new JFrame("Memory Pairs");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(3, 4, 4, 4));
    for (int i = 0; i < cards.length; i++) {
        int index = i;
        var button = new JButton();
        buttons[i] = button;
        button.addActionListener(event -> {
            play(index);
            refresh();
        });
        grid.add(button);
    }
    var restart = new JButton("New cards");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        refresh();
    });
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(restart, BorderLayout.SOUTH);
    refresh();
    frame.setSize(470, 350);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
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
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `JPanel` — Plocha okna, která může zobrazit kresbu hry či ovládací prvky.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>
