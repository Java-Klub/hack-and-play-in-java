/// # L07.07 · GoFish
///
/// Žádej soupeře o hodnoty, při neúspěchu lízej a sbírej čtyřkaretní sady. Dokončená sada má své karty
/// stáhnout ze hry.
///
/// **Tvoje mise:** Po vytvoření sady zůstane v ruce přebytečná karta. Oprav collect(), aby převod úplné
/// hodnoty na sadu zachoval celkový počet karet.
///
/// **Dnes trénuješ:** Invariants (vždy platné podmínky): některé veličiny musí souhlasit po každém tahu.
/// Karty v rukou a balíčku spolu s kartami v hotových sadách musí stále odpovídat původnímu balíčku.
///
/// Spusť Run File a požádej o hodnotu, kterou máš v ruce, zadáním jejího čísla od 2 do 14 potvrzeného
/// Enter.
/// Po opravě: Save → Test File → zahraj si znovu.
void collect(int who) {
    for (int rank = 2; rank <= 14; rank++) {
        if (Collections.frequency(hands.get(who), rank) == 4) {
            for (int n = 0; n < 4; n++) {
                hands.get(who).remove(Integer.valueOf(rank));
            }
            books[who]++;
        }
    }
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Collections.frequency(hands.get(who), rank)` — Spočítá výskyty jedné hodnoty v ruce vybraného
///   hráče.
/// - `== 4` — Sadu lze vytvořit jen při přítomnosti všech čtyř karet stejné hodnoty.
/// - `remove(Integer.valueOf(rank))` — Odebere kartu podle hodnoty, místo aby rank vyložilo jako pozici v
///   list (seznamu).
/// - `books[who]++` — Zapíše jednu novou sadu, která nyní zastupuje odebrané karty.
///
/// ### Co už obstarává připravená část
///
/// ask() řeší předávání, lízání i pokračování tahu a potom volá collect(). Připravená logika balíčku a
/// rukou je hotová; upravuješ převod na hotové sady.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Před i po sběru počítej ruce + balíček + čtyřnásobek čtveřic.
/// 2. Loop nyní odstraňuje jen tři odpovídající karty.
/// 3. Odeber čtyři kopie a jednou zvyš books; ostatní hodnoty ponech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int[] books;
int player;
String outcome;
String message;
Random random = new Random();

void refill(int who) {
    if (hands.get(who).isEmpty() && !stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
    collect(who);
}

void finish() {
    if (books[0] + books[1] == 13) {
        outcome = books[0] == books[1] ? "Draw" : books[0] > books[1] ? "You win" : "Bot wins";
    }
}

/// reset(): Obnovuje počáteční kolo Go Fish a maže stav patřící starému kolu.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int rank = 2; rank <= 14; rank++) {
        for (int i = 0; i < 4; i++) {
            cards.add(rank);
        }
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    books = new int[2];
    player = 0;
    outcome = message = "";
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    collect(0);
    collect(1);
    refill(0);
}

/// ask(): Přesouvá odpovídající hodnoty nebo lízne jednu kartu; pak sbírá čtveřice a řídí
/// pokračování tahu.
void ask(int who, int rank) {
    require(hands.get(who).contains(rank));
    int received = 0;
    while (hands.get(1 - who).remove(Integer.valueOf(rank))) {
        hands.get(who).add(rank);
        received++;
    }
    boolean again = received > 0;
    message = (who == 0 ? "You" : "Bot") + " asked for " + rank + " and received " + received + ".";
    if (!again && !stock.isEmpty()) {
        int card = stock.remove();
        hands.get(who).add(card);
        again = card == rank;
    }
    collect(who);
    finish();
    player = again ? who : 1 - who;
    refill(player);
    if (hands.get(player).isEmpty()) {
        player = 1 - player;
    }
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    require(player == 0);
    ask(0, Integer.parseInt(input));
    while (outcome.isEmpty() && player == 1) {
        refill(1);
        if (hands.get(1).isEmpty()) {
            player = 0;
            break;
        }
        var hand = hands.get(1);
        ask(1, hand.get(random.nextInt(hand.size())));
    }
    refill(0);
    finish();
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println(message);
    IO.println("Your ranks: " + hands.get(0) + " (2-14, Ace14)");
    IO.println("Books: you " + books[0] + ", bot " + books[1] + "; stock " + stock.size() + ", bot cards " + hands.get(1).size());
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
void main() {
    reset();
    IO.println("Go Fish");
    IO.println("Ask for a rank you hold: 2-14 (Ace14). Four equal ranks make a book; thirteen books ends play. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Ask for a rank you hold: 2-14 (Ace14). Four equal ranks make a book; thirteen books ends play. /r restarts; /q quits.");
        }
    }
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
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

