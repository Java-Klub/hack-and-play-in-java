/// # L07.01 · CrazyEightsSimpleMauMau
///
/// Schovej si osmičku na chvíli, kdy tě nezachrání běžná karta. Hraj proti druhému člověku a po únikovém
/// tahu si vyber barvu.
///
/// **Tvoje mise:** Jako univerzální karta se chová špatná hodnota. Oprav legal(int card), aby speciální i
/// běžné karty dodržovaly oznámená pravidla.
///
/// **Dnes trénuješ:** Switch expressions (výrazy s výběrem větve): vyber jednu vracenou hodnotu podle
/// případů a náhradní větve. Speciální případ nemá nahradit běžnou podmínku stejné hodnoty či barvy.
///
/// Spusť Run File, prohlédni si očíslované karty a zahraj zadáním p a indexu karty; při osmičce přidej
/// také číslo barvy.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean legal(int card) {
    return switch (rank(card)) {
        case 1 -> true;
        default -> rank(card) == rank(top) || card / 13 == suit;
    };
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `return switch (rank(card))` — Celý switch expression (výraz s výběrem) vytvoří boolean (ano/ne)
///   vrácený z legal().
/// - `case 1 -> true` — Šipková větev dává jeden výsledek pro jednu hodnotu karty; prověř, která hodnota
///   jej má mít.
/// - `default ->` — Obslouží všechny hodnoty karet bez vlastní větve case.
/// - `||` — Logical OR (logické nebo): v běžné větvi stačí stejná hodnota nebo stejná barva.
///
/// ### Co už obstarává připravená část
///
/// rank() přečte hodnotu karty. play() se ptá legal(), odebere přijatou kartu, obnoví zvolenou barvu a
/// hledá prázdnou ruku; rozdávání i konzolový vstup jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. rank(card) převádí zakódovanou kartu na hodnotu 1–13.
/// 2. Switch expression vrací jednu boolean hodnotu.
/// 3. Použij case 8 -> true; default ponech dodanou podmínku hodnoty/barvy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int top;
int suit;
String outcome;
String message;

/// reset(): Obnovuje počáteční kolo Crazy Eights / Simple Mau-Mau a maže stav patřící starému kolu.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 52; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    do {
        top = stock.remove();
        if (rank(top) == 8) {
            stock.add(top);
        }
    }
    while (rank(top) == 8);
    suit = top / 13;
    outcome = message = "";
}

int rank(int card) {
    return card % 13 + 1;
}

int firstLegal(int who) {
    for (int i = 0; i < hands.get(who).size(); i++) {
        if (legal(hands.get(who).get(i))) {
            return i;
        }
    }
    return -1;
}

/// play(): Odebere platnou kartu, změní vrchní kartu a zvolenou barvu a rozpozná prázdnou ruku.
void play(int who, int index, int chosenSuit) {
    require(index >= 0 && index < hands.get(who).size());
    int card = hands.get(who).get(index);
    require(legal(card) && chosenSuit >= 0 && chosenSuit < 4);
    hands.get(who).remove(index);
    top = card;
    suit = rank(card) == 8 ? chosenSuit : card / 13;
    if (hands.get(who).isEmpty()) {
        outcome = who == 0 ? "You win" : "Bot wins";
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(int who) {
    require(firstLegal(who) < 0);
    if (!stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toLowerCase(Locale.ROOT).split("\\s+");
    if (input.equalsIgnoreCase("d")) {
        draw(0);
    } else {
        require((p.length == 2 || p.length == 3) && p[0].equals("p"));
        int index = Integer.parseInt(p[1]) - 1;
        require(index >= 0 && index < hands.get(0).size());
        int card = hands.get(0).get(index);
        require(rank(card) != 8 || p.length == 3);
        play(0, index, p.length == 3 ? Integer.parseInt(p[2]) : card / 13);
    }
    if (!outcome.isEmpty()) {
        return;
    }
    if (stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
        return;
    }
    int index = firstLegal(1);
    if (index < 0) {
        draw(1);
    } else {
        play(1, index, hands.get(1).get(index) / 13);
    }
    if (outcome.isEmpty() && stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
    }
}

String card(int n) {
    return rank(n) + "/" + n / 13;
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Suits 0=clubs 1=diamonds 2=hearts 3=spades. Ace=1, faces11-13.");
    IO.println("Top " + card(top) + "; active suit " + suit + "; bot cards " + hands.get(1).size() + "; stock " + stock.size());
    for (int i = 0; i < hands.get(0).size(); i++) {
        IO.println((i + 1) + ": " + card(hands.get(0).get(i)));
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
void main() {
    reset();
    IO.println("Crazy Eights / Simple Mau-Mau");
    IO.println("p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

