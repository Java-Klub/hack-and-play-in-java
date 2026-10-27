/// # L03.03 · BlackjackTwentyOne
///
/// Zkus skončit blíž k 21 než krupiér bez překročení. Můžeš si vzít další kartu nebo zůstat a esa mohou
/// mít hodnotu jedna či jedenáct.
///
/// **Tvoje mise:** Známý začátek 20 proti 19 chybně přisoudí výhru krupiérovi. Doplň compareTotals tak,
/// aby ve správném pořadí oddělilo překročení, výhru hráče, remízu a výhru krupiéra.
///
/// **Dnes trénuješ:** Uspořádané branch (větve): první vybraná větev může rozhodnout výsledek dřív, než
/// se posoudí další možnosti. Překročení hráče nad 21 se musí řešit před porovnáním velikosti součtů.
///
/// Použij Run File a zadáním s zůstaň na cvičném součtu 20, potom porovnej oznámený výsledek s
/// krupiérovou 19.
/// Po opravě: Save → Test File → zahraj si znovu.
String compareTotals(int human, int house) {
    if (human > 21) {
        return "You bust";
    } else if (house > 21 || human > house) {
        return "You win";
    } else if (human == house) {
        return "Draw";
    } else {
        return "Dealer wins";
    }
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `String compareTotals(int human, int house)` — Přijmi dva celočíselné součty a vrať jediný textový
///   výsledek; čárka odděluje oba input (vstupy).
/// - `human > 21` — Číselná condition (podmínka) překročení hráče; > nezahrnuje rovnost.
/// - `if` — Začni podmíněný výběr; následující závorky obsahují otázku.
/// - `return "You bust";` — Vrať tento literal (doslovný výsledek) a dokonči současné call (volání).
/// - `else` — Současná alternativa pokrývá vše, co nesplnilo první condition (podmínku).
/// - `house` — Druhý součet je dostupný jako input (vstup), i když ho nehotové tělo zatím nepoužívá.
///
/// ### Co už obstarává připravená část
///
/// value počítá připravené součty kombinací s esy. finish nechá krupiéra dobírat a potom se zeptá
/// compareTotals na výsledek. reset připraví známé první porovnání a skutečný zbývající balíček; input
/// (vstup) i zobrazení jsou hotové.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Nejdřív ověř překročení hráče a teprve potom velikost čísel.
/// 2. Podmínka výhry je house > 21 NEBO human > house.
/// 3. Pro výhru a remízu použij else if před posledním else.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
ArrayDeque<Integer> deck;
List<Integer> hand;
List<Integer> dealer;
String outcome;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int suit = 0; suit < 4; suit++) {
        for (int rank = 1; rank <= 13; rank++) {
            cards.add(rank);
        }
    }
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(10));
    cards.remove(Integer.valueOf(9));
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    hand = new ArrayList<>(List.of(10, 10));
    dealer = new ArrayList<>(List.of(10, 9));
    outcome = "";
}

int value(List<Integer> cards) {
    int sum = 0;
    int aces = 0;
    for (int rank : cards) {
        sum += rank == 1 ? 11 : Math.min(rank, 10);
        if (rank == 1) {
            aces++;
        }
    }
    while (sum > 21 && aces > 0) {
        sum -= 10;
        aces--;
    }
    return sum;
}

void finish() {
    while (value(dealer) < 17) {
        dealer.add(deck.remove());
    }
    int h = value(hand);
    int d = value(dealer);
    outcome = compareTotals(h, d);
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("h")) {
        hand.add(deck.remove());
        if (value(hand) > 21) {
            outcome = "You bust";
        }
    } else {
        require(input.equalsIgnoreCase("s"));
        finish();
    }
}

void display() {
    IO.println("Ranks: 1=Ace (1/11), 11-13=faces (10). Dealer stands on soft 17.");
    IO.println("Your hand " + hand + " = " + value(hand));
    IO.println(outcome.isEmpty() ? "Dealer: " + dealer.getFirst() + " + hidden" : "Dealer: " + dealer + " = " + value(dealer));
}

void main() {
    reset();
    IO.println("Blackjack / Twenty-One");
    IO.println("h hits, s stands. One hand, no bets or splitting. /r deals again; /q quits.");
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
            IO.println("Invalid move or command. h hits, s stands. One hand, no bets or splitting. /r deals again; /q quits.");
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
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

