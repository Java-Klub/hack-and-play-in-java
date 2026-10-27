/// # L01.06 · HigherOrLower
///
/// Hádej, zda bude příští karta vyšší nebo nižší, a snaž se udržet sérii, než ji ukončí chybný odhad.
/// Stejné karty ti dají další šanci bez trestu.
///
/// **Tvoje mise:** Automat slaví už po jediném správném odhadu. Oprav sdílený target tak, aby displej
/// skóre i pravidlo výhry popisovaly kolo s deseti správnými odhady.
///
/// **Dnes trénuješ:** Named value (pojmenovaná hodnota): target uchovává jediný údaj, který může číst
/// několik částí. Změna jedné initialization (inicializace) má udržet soulad vypsaného a skutečného cíle.
///
/// Použij Run File, zadej h pro vyšší nebo l pro nižší kartu a sleduj počet správných odhadů potřebných
/// pro vypsané kolo.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F03 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int target = 1;

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F03">
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
/// - `int` — Uložená hodnota je celé číslo vhodné pro počítání správných odhadů.
/// - `target` — Anglické jméno označuje sdílený cíl; liší se od correct, skóre měnícího se během hry.
/// - `=` — Ulož hodnotu vpravo pod jménem vlevo.
/// - `1` — Současný numeric literal (doslovný zápis čísla) je jedna, což vysvětluje příliš krátké kolo.
/// - `;` — Ukonči declaration (zavedení jména) a jeho initialization (výchozí přiřazení).
///
/// ### Co už obstarává připravená část
///
/// reset sestaví a zamíchá balíček. command provede odhad a čte target při rozhodnutí o výhře; display
/// čte stejný target pro popisek skóre. main dodává input (vstup) a opakování kola.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Toto je první hra čekající na psaný console input (vstup do konzole). Po Run File klikni v
/// **Output k aktuální výzvě**, napiš `h` nebo `l` a stiskem **Enter** odešli celý řádek. Piš do
/// Output, nikoli do Java editoru. Vypsané `/r` restartuje kolo a `/q` ukončí program. Změnu source
/// (zdrojového textu) ulož a načti novým spuštěním; restart již běžícího kola upravenou Javu nenačte.
/// Náhodné karty mohou kolo ukončit před deseti správnými tipy, proto sdílený target (cíl) ověř i
/// pomocí Test File.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi jedinou initialization (inicializaci) target nahoře.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Připravený displej i porovnání výhry už čtou target.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Změň int target = 1; na int target = 10;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
ArrayDeque<Integer> deck;
int current;
int correct;
String outcome;
String message;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int suit = 0; suit < 4; suit++) {
        for (int rank = 2; rank <= 14; rank++) {
            cards.add(rank);
        }
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    current = deck.remove();
    correct = 0;
    outcome = message = "";
}

void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[hl]"));
    require(!deck.isEmpty());
    int next = deck.remove();
    message = "Next card: " + next;
    if (next == current) {
        message += "; tie, no penalty.";
    } else {
        if (input.equalsIgnoreCase("h") == (next > current)) {
            correct++;
            message += "; correct!";
        } else {
            outcome = "Wrong prediction. Final score: " + correct;
        }
    }
    current = next;
    if (correct == target) {
        outcome = target + " correct: you win!";
    }
    if (deck.isEmpty() && outcome.isEmpty()) {
        outcome = "Deck exhausted. Final score: " + correct;
    }
}

void display() {
    IO.println(message);
    IO.println("Current card " + current + " (Jack11 Queen12 King13 Ace14). Correct " + correct + "/" + target + "; deck " + deck.size());
}

void main() {
    reset();
    IO.println("Higher or Lower");
    IO.println("h predicts higher, l lower. Ace is high; equal rank is a free tie. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. h predicts higher, l lower. Ace is high; equal rank is a free tie. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Připravené vstupní body: main, reset, command.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

