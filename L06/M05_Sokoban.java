/// # L06.05 · Sokoban
///
/// Dotlač bednu na cíl ve stísněném skladišti. Plánuj dopředu, protože bednu můžeš tlačit, ale
/// nepřitáhneš ji zpět.
///
///
/// **Tvoje mise:** Nech ? vypsat přesně platné okamžité tahy W/A/S/D bez pohybu hráče nebo bedny.
/// Tlačení je platné jen s volnou podlahou za bednou; zeď, hranice mapy, diagonála ani dokončené kolo
/// nejsou platný tah. Opakované dotazy musí vše zachovat. Jde o radar jediného tahu, nikoli slib, že
/// hádanka zůstane řešitelná.
///
/// **Dnes se učíš:** Query (dotaz) a command (příkaz) se mohou shodovat na platnosti, ale jen příkaz
/// mění stav. Kontroluj vedlejší účinky přijatých i odmítnutých kandidátů.
///
/// Zadej dvakrát ?, potom dvakrát d a znovu ?. Porovnej radu s pokusem o třetí d a sleduj, zda samotný
/// dotaz mění vypsaný sklad.
boolean canMove(int dx, int dy) {
    return floor(x + dx, y + dy);
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F15">
/// ### Contracts: ptej se bez změny světa
///
/// Contract (smlouva) popisuje přijímaný input (vstup) a význam výsledku. Pure calculation (čistý
/// výpočet) používá své vstupy bez změny dalšího state (stavu). Query (dotaz) může číst aktuální
/// model (model pravidel), ale má jej zachovat. Získání odpovědi a potvrzení akce jsou různé
/// odpovědnosti.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` dává nezápornou velikost, odstraní tedy případné záporné znaménko. Tuto method (metodu)
/// můžeš volat opakovaně, aniž by něco spotřebovala. Argument (argumenty) `(9, 4)` dávají pět: rozdíl
/// je pět. Předpověz `(4, 9)`. Které uložené hodnoty porovnáš před dotazem a po něm, abys doložil
/// nepřítomnost side effect (vedlejšího účinku)? Stejnou ochranu potřebuje odmítnutý kandidát.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Příští políčko hráče a příští políčko bedny jsou dva různí kandidáti. Prozkoumej terrain (terén)
/// před změnou kterékoli polohy.
///
/// V jiné chodbě @ $ # nestačí vstoupit na bednu: za ní je zeď, takže tlačení není platné. U @ $ . je
/// platné. Plánovací dotaz má dát stejnou odpověď dvakrát bez provedení a vracení tahu. Před dotazem
/// zachyť hráče, bednu i outcome (výsledek). Předpověz dotaz směrem od bedny a dotaz na okraj mapy.
// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Porovnej cestu radaru se skutečným tahem. Najdi kandidáta s odlišným verdiktem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Větev ? volá canMove pro čtyři směry. canMove zatím kontroluje jen podlahu příštího políčka hráče;
/// command navíc ověřuje cíl tlačené bedny.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Odmítni nekardinální krok a dokončené kolo, odmítni chybějící podlahu a cíl za bednou zkoumej jen
/// při vstupu na ni. Vrať odpověď bez přiřazování poloh.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Přidej druhého poradce varujícího před zatlačením bedny do rohu bez cíle. Varování odděl od okamžité
/// platnosti a navrhni jeden platný, ale prohraný tah dokazující rozdíl.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean floor(int px, int py) {
    if (py < 0 || py >= terrain.length || px < 0 || px >= terrain[py].length()) {
        return false;
    }
    return terrain[py].charAt(px) != '#';
}

String[] terrain = {"#######", "#    .#", "#     #", "#     #", "#######"};
int x;
int y;
int bx;
int by;
String outcome;

/// reset(): Obnovuje počáteční kolo Sokoban a maže stav patřící starému kolu.
void reset() {
    x = 2;
    y = 2;
    bx = 3;
    by = 2;
    outcome = "";
}

/// command(): Před zápisem obou pozic ověří krok hráče i případný cíl tlačené bedny.
void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[wasd]"));
    int dx = input.equalsIgnoreCase("a") ? -1 : input.equalsIgnoreCase("d") ? 1 : 0;
    int dy = input.equalsIgnoreCase("w") ? -1 : input.equalsIgnoreCase("s") ? 1 : 0;
    int nx = x + dx;
    int ny = y + dy;
    require(floor(nx, ny));
    if (nx == bx && ny == by) {
        require(floor(bx + dx, by + dy));
        bx += dx;
        by += dy;
    }
    x = nx;
    y = ny;
    if (terrain[by].charAt(bx) == '.') {
        outcome = "Crate on goal: you win!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    for (int r = 0; r < 5; r++) {
        String s = "";
        for (int c = 0; c < 7; c++) {
            s += x == c && y == r ? '@' : bx == c && by == r ? (terrain[r].charAt(c) == '.' ? '*' : '$') : terrain[r].charAt(c);
        }
        IO.println(s);
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Sokoban");
    IO.println("Push the crate $ onto the goal dot. You can push, but cannot pull; restart if you trap the crate.");
    IO.println("W/A/S/D move; ? asks the move radar. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
    IO.println("Example: d moves right; pressing Enter submits the command. Use /r to reset a stuck attempt.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
            return;
        }
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                || (!outcome.isEmpty() && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("That round is over. Press Enter to play again, or /q to quit.");
            continue;
        }
        if (input.equals("?")) {
            String advice = "";
            if (canMove(0, -1)) advice += "W ";
            if (canMove(-1, 0)) advice += "A ";
            if (canMove(0, 1)) advice += "S ";
            if (canMove(1, 0)) advice += "D ";
            IO.println("Immediate moves: " + (advice.isEmpty() ? "none" : advice.strip()));
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "W/A/S/D move; ? asks the move radar. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
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
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
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
