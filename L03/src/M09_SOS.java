/// # L03.09 · SOS
///
/// Dva hráči vkládají S nebo O na společnou desku a bodují za každé nově vzniklé SOS. Bodující tah
/// ponechá hráči další tah, proto hledej umístění dokončující i více řad.
///
/// **Tvoje mise:** Skóre zahrnuje i řady SOS existující před tahem. Oprav gainedPoints tak, aby
/// odměňovalo pouze nově vytvořené řady.
///
/// **Dnes trénuješ:** Význam parameter (parametrů) a return value (vrácené hodnoty): before a after
/// popisují dva okamžiky stejného počtu. Pokud oba obsahují tři řady, žádná nová řada nemá být odměněna;
/// method (metoda) tento jediný výsledek vrátí volající části.
///
/// Použij Run File a jako samostatné tahy zadej 1 S, 2 O, 3 S a 4 S; porovnej odměnu za poslední tah,
/// který nové SOS nevytvoří.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F13 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int gainedPoints(int before, int after) {
    return after + before;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F13">
/// Method (metoda) zabalí operaci pod jediné jméno. Parameter (parametry) jsou místní jména informací
/// přijatých při každém call (volání); argument (argumenty) jsou skutečně předané hodnoty. `return`
/// předá jediný výsledek zpět volající části.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Předpověz obě vypsané hodnoty: sedmičku a pětku. Čárka odděluje parameter či argument a jejich
/// pořadí rozhoduje, kterou hodnotu jméno dostane. Jména popisují role; informace si sama
/// nezískávají. Tato method počítá bez vypisování a změny připraveného state (stavu programu). Při
/// opravě výpočtu vyzkoušej druhou dvojici argument, aby se za jediný případ nemohla schovat náhodná
/// pevná odpověď. Method se provede až při skutečném call, nikoli samotným zapsáním definice.
///
/// Nyní předpověz remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int gainedPoints(int before, int after)` — Přijmi nejdřív starý počet a potom nový; vrať jedinou
///   celočíselnou odměnu.
/// - `before` — Počet uložený před změnou desky tímto tahem.
/// - `after` — Počet získaný z desky po provedení tahu.
/// - `+` — Toto nyní sčítá dva časové snímky počtu a zahrnuje staré řady dvakrát.
/// - `return` — Předej odměnu do command; samo pravidlo skóre nemění.
///
/// ### Co už obstarává připravená část
///
/// triples počítá všechny SOS na skutečné desce. command uloží počet před tahem, vloží písmeno, požádá
/// gainedPoints o odměnu a potom řeší skóre a bonusové tahy. Procházení desky a input (vstup) jsou
/// připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// before a after počítají úplné řady SOS na celé desce; nejsou to dosavadní skóre obou hráčů.
/// Jsou-li oba počty stejné, tento tah nevytvořil žádnou novou řadu. Připravená volající část přičte
/// vrácenou odměnu ke skóre aktuálního hráče. Volitelný transfer (přenos znalosti): vymysli dvě různé
/// dvojice before/after se stejnou odměnou a potom jednu dvojici s jinou odměnou. Vysvětli, proč
/// tvoje pravidlo zvládne všechny tři bez znalosti desky a bez vypisování.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Parameter (parametry) jsou počty ze dvou okamžiků, nikoli dvě odměny.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Nezměněný počet musí vrátit nulu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať after - before.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
char[] board;
int player;
int moves;
int[] scores;
String outcome;

void reset() {
    board = ".........".toCharArray();
    player = 1;
    moves = 0;
    scores = new int[3];
    outcome = "";
}

int triples() {
    int total = 0;
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            for (int[] d : new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, -1}}) {
                int er = r + 2 * d[0];
                int ec = c + 2 * d[1];
                if (er < 0 || er >= 3 || ec < 0 || ec >= 3) {
                    continue;
                }
                if (board[r * 3 + c] == 'S' && board[(r + d[0]) * 3 + c + d[1]] == 'O' && board[er * 3 + ec] == 'S') {
                    total++;
                }
            }
        }
    }
    return total;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[1].matches("[SO]"));
    int cell = Integer.parseInt(p[0]) - 1;
    require(cell >= 0 && cell < 9 && board[cell] == '.');
    int before = triples();
    board[cell] = p[1].charAt(0);
    moves++;
    int gained = gainedPoints(before, triples());
    scores[player] += gained;
    if (gained == 0) {
        player = 3 - player;
    }
    if (moves == 9) {
        outcome = scores[1] == scores[2] ? "Draw" : "Player " + (scores[1] > scores[2] ? 1 : 2) + " wins";
    }
}

void display() {
    IO.println("Player " + player + "; score " + scores[1] + ":" + scores[2]);
    for (int r = 0; r < 3; r++) {
        String line = "";
        for (int c = 0; c < 3; c++) {
            line += (board[r * 3 + c] == '.' ? "" + (r * 3 + c + 1) : "" + board[r * 3 + c]) + " ";
        }
        IO.println(line);
    }
}

void main() {
    reset();
    IO.println("SOS");
    IO.println("Enter cell 1-9 and S or O, e.g. 5 O. Each new SOS scores. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Enter cell 1-9 and S or O, e.g. 5 O. Each new SOS scores. /r restarts; /q quits.");
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

