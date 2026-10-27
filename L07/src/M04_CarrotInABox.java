// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
// </editor-fold>

/// # L07.04 · CarrotInABox
///
/// Jedna krabice skrývá mrkev a jeden hráč smí tajně nahlédnout. Blafuj, vyjednávej a rozhodni, zda stojí
/// za to krabice vyměnit.
///
/// **Tvoje mise:** Konečný majitel ceny ignoruje rozhodnutí o výměně. Oprav holderAfterSwap(), aby
/// odhalení odpovídalo ponechání či výměně krabic.
///
/// **Dnes trénuješ:** Enum states (pojmenované stavy): používej malou sadu srozumitelných výsledků místo
/// nevysvětlených čísel. holderAfterSwap() má vrátit správnou hodnotu Holder pro konečnou situaci.
///
/// Spusť Run File, klikni na Look, bluff, then choose, nech prvního hráče tajně nahlédnout a druhého
/// rozhodnout o ponechání či výměně.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F21 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
enum Holder {
    PLAYER_ONE, PLAYER_TWO
}

Holder holderAfterSwap(boolean swap) {
    return prizeA ? Holder.PLAYER_ONE : Holder.PLAYER_TWO;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F21">
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
/// - `enum Holder` — Deklaruje úplnou sadu pojmenovaných možných majitelů ceny.
/// - `PLAYER_ONE, PLAYER_TWO` — Jsou to alternativy stejného typu Holder, nikoli libovolné string values
///   (textové hodnoty).
/// - `boolean swap` — Volající předává, zda byly krabice vyměněny.
/// - `prizeA ? Holder.PLAYER_ONE : Holder.PLAYER_TWO` — Vybírá pojmenovaného majitele podle počáteční
///   polohy mrkve; zvaž, co mění konečné rozhodnutí.
///
/// ### Co už obstarává připravená část
///
/// Dialogy řeší tajné nahlédnutí i konečnou volbu. choose() volá holderAfterSwap() a sestaví veřejné
/// odhalení; prizeA už uchovává počáteční polohu mrkve.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// prizeA říká, kde mrkev začala; swap říká, zda si hráči krabice vyměnili.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Ponechání zachová držitele; výměna jej obrátí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// První hráč má cenu právě tehdy, když prizeA != swap.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean prizeA;
String outcome;
JFrame frame;
JLabel status;

/// reset(): Obnovuje počáteční kolo Carrot in a Box a maže stav patřící starému kolu.
void reset() {
    prizeA = new Random().nextBoolean();
    outcome = "";
}

/// choose(): Provede závěrečné odhalení podle hodnoty Holder.
void choose(boolean swap) {
    require(outcome.isEmpty());
    Holder holder = holderAfterSwap(swap);
    outcome = "Player " + (holder == Holder.PLAYER_ONE ? 1 : 2) + " wins the carrot!";
}

/// refresh(): Přenáší aktuální hodnoty modelu do popisků tlačítek, jejich povolení a stavového
/// textu.
void refresh() {
    status.setText("<html>One box holds a carrot. A looks privately and may bluff aloud.<br>B chooses keep or swap. " + outcome + "</html>");
}

/// show(): Dodané pozdější téma: Swing callbacks tlačítek volají model a obnovují jeho zobrazení.
void show() {
    frame = new JFrame("Carrot in a Box");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    status = new JLabel();
    var play = new JButton("Look, bluff, then choose");
    play.addActionListener((e) -> {
        if (!outcome.isEmpty()) {
            return;
        }
        if (JOptionPane.showConfirmDialog(frame, "Player B looks away. Pass to A.", "Handover", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        JOptionPane.showMessageDialog(frame, "Player A: your box " + (prizeA ? "HAS" : "does NOT have") + " the carrot.");
        JOptionPane.showMessageDialog(frame, "A may now bluff aloud. Then pass to B; A keeps their box closed.");
        int answer = JOptionPane.showOptionDialog(frame, "Player B: keep your box or swap with A?", "B chooses", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Keep", "Swap"}, "Keep");
        if (answer >= 0) {
            choose(answer == 1);
            refresh();
        }
    });
    var restart = new JButton("Restart");
    restart.addActionListener((e) -> {
        reset();
        refresh();
    });
    frame.add(play, BorderLayout.NORTH);
    frame.add(status, BorderLayout.CENTER);
    frame.add(restart, BorderLayout.SOUTH);
    frame.setSize(580, 180);
    refresh();
    frame.setVisible(true);
}

/// main(): Spouští model a potom plánuje dodané okno na Swing event thread.
void main() {
    reset();
    SwingUtilities.invokeLater(this::show);
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
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
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `enum` — Type (typ) s pevnou sadou pojmenovaných hodnot, například READY, PLAYING a FINISHED.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

