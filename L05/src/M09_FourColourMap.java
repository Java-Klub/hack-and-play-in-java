/// # L05.09 · FourColourMap
///
/// Vybarvi malou mapu tak, aby sousední oblasti neměly stejnou barvu. Vzdálené oblasti se shodovat mohou.
///
/// **Tvoje mise:** Kontroluje se jen jedna společná hranice, takže ostatní spory počítadlu uniknou. Oprav
/// conflicts(), aby spočítalo všechny skutečné konflikty.
///
/// **Dnes trénuješ:** Traversal and search (průchod a hledání): procházej vztahy mezi oblastmi, nejen
/// oblasti samotné. Každá hranice určuje dvě barvy k porovnání.
///
/// Spusť Run File, napiš B 2 pro obarvení oblasti B barvou 2 a pokračuj dalšími označenými oblastmi.
/// Po opravě: Save → Test File → zahraj si znovu.
int conflicts() {
    int n = 0;
    for (int[] edge : new int[][]{adjacent[0]}) {
        if (colours[edge[0]] != 0 && colours[edge[0]] == colours[edge[1]]) {
            n++;
        }
    }
    return n;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int[] edge` — Dvojice indexů (pozic) oblastí, které sdílejí hranici.
/// - `adjacent[0]` — Vybírá pouze první uloženou dvojici sousedů; sleduj, co průchod dostává.
/// - `colours[edge[0]] != 0` — Nevybarvená oblast se do počtu konfliktů nezahrnuje.
/// - `n++` — Započítá jednu konfliktní společnou hranici, nikoli jednu vybarvenou oblast.
///
/// ### Co už obstarává připravená část
///
/// adjacent obsahuje připravené dvojice sousedních oblastí. Reakce na kliknutí mění barvu jedné oblasti;
/// solved() vyžaduje všechny vybarvené a přes conflicts() posuzuje jejich sousedství.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. adjacent obsahuje právě dvojice se společnou hranicí.
/// 2. Současný průchod navštíví jen první dvojici.
/// 3. Projdi adjacent; počítej stejné nenulové barvy obou konců.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] map = {"AABB", "ACCB", "DDEE"};
int[][] adjacent = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};
int[] colours;
String outcome;

/// reset(): Obnovuje počáteční kolo Four-Colour Map a maže stav patřící starému kolu.
void reset() {
    colours = new int[]{1, 0, 0, 0, 0};
    outcome = "";
}

/// solved(): Vyžaduje obarvení každé oblasti a žádný konflikt sousedů.
boolean solved() {
    for (int c : colours) {
        if (c == 0) {
            return false;
        }
    }
    return conflicts() == 0;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[0].matches("[B-E]"));
    int region = p[0].charAt(0) - 'A';
    int colour = Integer.parseInt(p[1]);
    require(colour >= 0 && colour <= 4);
    colours[region] = colour;
    if (solved()) {
        outcome = "Map coloured correctly!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    for (String row : map) {
        IO.println(row);
    }
    IO.println("A is fixed1. Colours1-4, 0 unfilled; corner-only touch is not adjacent.");
    for (int i = 0; i < 5; i++) {
        IO.println((char)('A' + i) + " = " + colours[i]);
    }
    IO.println("Equal-colour shared-edge conflicts: " + conflicts());
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
void main() {
    reset();
    IO.println("Four-Colour Map");
    IO.println("Enter region B-E and colour1-4; 0 clears. A=1 is fixed. Shared-edge neighbours must differ. /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter region B-E and colour1-4; 0 clears. A=1 is fixed. Shared-edge neighbours must differ. /r restarts; /q quits.");
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

