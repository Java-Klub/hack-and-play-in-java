// <editor-fold defaultstate="collapsed" desc="Připravené nástroje">
import javax.swing.*;
import java.awt.BorderLayout;
// </editor-fold>

/// # L07.02 · CooperateOrDefect
///
/// Tajně slib spolupráci, nebo zvol zradu, a potom oba rozhodnutí odhalte. Pět krátkých kol je dobrá
/// záminka k debatě o důvěře.
///
/// **Tvoje mise:** Dva spolupracující hráči dostanou špatnou odměnu. Oprav reward() a zachovej výsledky
/// ostatních dvojic rozhodnutí.
///
/// **Dnes trénuješ:** Switch expressions (výrazy s výběrem větve): spoj dvě rozhodnutí ano/ne do malého
/// čísla případu a vrať jeho odměnu. Každou větev čti z pohledu jednoho hráče.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L07/M02_CooperateOrDefect.java`
/// - Kontrola této mise: `java L07/Check.java M02_CooperateOrDefect.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, klikni na Private choices for next round a před
/// odhalením obou voleb si mezi soukromými dialogy předejte počítač. Po opravě: ulož soubor, spusť
/// uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F09 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int reward(boolean cooperate, boolean otherCooperates) {
    int choice = (cooperate ? 2 : 0) + (otherCooperates ? 1 : 0);
    return switch (choice) {
        case 3 -> 0;
        case 2 -> 0;
        case 1 -> 5;
        default -> 1;
    };
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F09">
/// ### Switch: vyber jednu hodnotu
///
/// Switch expression (výraz s výběrem) vybírá výsledek podle selector (výběrové hodnoty). Každá arrow
/// case (větev se šipkou) obslouží odpovídající hodnotu; nepokračuje do další větve. `default`
/// pokrývá zbývající hodnoty. Koncový středník patří okolnímu assignment (přiřazení) či return
/// statement (příkazu návratu).
///
/// ```java
/// String weather = "mist";
/// String colour = switch (weather) {
///     case "mist" -> "silver";
///     case "sun" -> "gold";
///     default -> "indigo";
/// };
/// IO.println(colour);
/// ```
///
/// Mist case (větev pro mlhu) vybere `silver`. Předpověz výstup pro `"sun"` a potom `"snow"`.
/// Selector se porovnává s case label (označením větve); text za šipkou je výsledek. Změna jednoho
/// zvláštního případu má zachovat význam náhradní větve.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `cooperate ? 2 : 0` — Conditional expression (podmíněný výraz) zakóduje rozhodnutí tohoto hráče do
///   části čísla případu.
/// - `otherCooperates ? 1 : 0` — Použije jinou váhu pro druhého hráče, aby se čtyři dvojice rozhodnutí
///   rozlišily.
/// - `case 3 -> 0` — Větev přiřazuje jedné zakódované dvojici odměnu tohoto hráče; tento výsledek je
///   potřeba prověřit.
/// - `default -> 1` — Zbývající zakódovaná dvojice má vlastní odměnu v náhradní větvi.
///
/// ### Co už obstarává připravená část
///
/// Soukromé dialogy sbírají rozhodnutí odděleně. play() volá reward() z obou pohledů, obnoví součty a
/// odhalí výsledek kola; opravuješ pouze společnou bodovací tabulku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Selector (výběrové číslo) není odměna
///
/// Dvě boolean choice (logické volby) přispívají samostatně k číslu větve. Než přiřadíš odměnu, napiš
/// čtyři dvojice vstupů a vypočítej selector. Zobrazený contract (smlouva hry) dává tři body oběma
/// při spolupráci, jeden oběma při zradě a pět proti nule při jediné zradě. `reward` vrací pohled
/// prvního argument (argumentu); caller (volající část) se ptá také z opačného pohledu. Předpověz obě
/// volání při jednom smíšeném kole.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// choice používá dva bity: tvoje spolupráce přidá 2, soupeřova 1.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Před úpravou vypiš možné součty 0,1,2,3.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Větev pro 3 znamená vzájemnou spolupráci a má vracet 3.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] scores;
int round;
String outcome;
String message;
JFrame frame;
JLabel status;

/// reset(): Obnovuje počáteční kolo Cooperate or Defect a maže stav patřící starému kolu.
void reset() {
    scores = new int[2];
    round = 0;
    outcome = "";
    message = "Pass privately for each choice; reveal only after both choose.";
}

/// play(): Boduje oba pohledy, posune jedno z pěti kol a vytvoří zveřejněný souhrn.
void play(boolean a, boolean b) {
    require(outcome.isEmpty());
    scores[0] += reward(a, b);
    scores[1] += reward(b, a);
    round++;
    message = "P1 " + (a ? "cooperated" : "defected") + ", P2 " + (b ? "cooperated" : "defected");
    if (round == 5) {
        outcome = scores[0] == scores[1] ? "Draw" : "Player " + (scores[0] > scores[1] ? 1 : 2) + " wins";
    }
}

int privateChoice(int who) {
    int handover = JOptionPane.showConfirmDialog(frame, "Pass to Player " + who + ". Other player looks away. Ready?", "Private handover", JOptionPane.OK_CANCEL_OPTION);
    if (handover != JOptionPane.OK_OPTION) {
        return -1;
    }
    return JOptionPane.showOptionDialog(frame, "Player " + who + ": choose privately", "Private choice", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Cooperate", "Defect"}, "Cooperate");
}

/// refresh(): Přenáší aktuální hodnoty modelu do popisků tlačítek, jejich povolení a stavového
/// textu.
void refresh() {
    status.setText("<html>Round " + round + "/5; P1 " + scores[0] + ", P2 " + scores[1] + "<br>" + message + "<br>" + outcome + "<br>CC=3/3, DD=1/1, lone defector=5, other=0.</html>");
}

/// show(): Dodané pozdější téma: Swing callbacks tlačítek volají model a obnovují jeho zobrazení.
void show() {
    frame = new JFrame("Cooperate or Defect");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    status = new JLabel();
    var play = new JButton("Private choices for next round");
    play.addActionListener((e) -> {
        if (!outcome.isEmpty()) {
            return;
        }
        int a = privateChoice(1);
        if (a < 0) {
            return;
        }
        int b = privateChoice(2);
        if (b < 0) {
            return;
        }
        play(a == 0, b == 0);
        refresh();
        play.setEnabled(outcome.isEmpty());
    });
    var restart = new JButton("Restart");
    restart.addActionListener((e) -> {
        reset();
        play.setEnabled(true);
        refresh();
    });
    frame.add(status, BorderLayout.CENTER);
    frame.add(play, BorderLayout.NORTH);
    frame.add(restart, BorderLayout.SOUTH);
    frame.setSize(540, 220);
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
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
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
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `::` — Method reference (odkaz na metodu): předá akci, aniž ji hned zavolá.
/// - `this` — Aktuální game object (objekt hry): právě tento běh a jeho uložený state (stav).
/// - `null` — Žádný object (objekt) tu není.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `JFrame` — Okno dodané JDK knihovnou Swing.
/// - `SwingUtilities.invokeLater` — Naplánuje práci s oknem na Swing event thread (vlákno událostí); akce
///   se provede později.
// </editor-fold>

