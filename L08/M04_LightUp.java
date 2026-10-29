/// # L08.04 · LightUp
///
/// Rozmísti žárovky tak, aby svítila všechna volná políčka, ale žádné dvě žárovky na sebe neviděly. Zdi
/// mohou jednu chodbu rozdělit na několik samostatných světelných úloh.
///
/// **Tvoje mise:** Pravidlo vidí jen bezprostředně sousední žárovky. Oprav visible(), aby se rovná volná
/// chodba prověřovala až ke zdi nebo okraji.
///
/// **Dnes trénuješ:** Grid neighbours (sousedé v mřížce): opakovaným použitím směrového posunu prozkoumáš
/// řadu políček. Test jednoho souseda a průchod chodbou odpovídají na různé otázky.
///
/// Spusť program a zadáním 1 1 přepni žárovku vlevo nahoře, potom zkus osvětlit ostatní volná
/// políčka bez střetů žárovek.
int visible(int r, int c) {
    int n = 0;
    for (int[] d : dirs) {
        int y = r + d[0];
        int x = c + d[1];
        if (open(y, x)) {
            if (bulbs[y][x]) {
                n++;
            }
            y += d[0];
            x += d[1];
        }
    }
    return n;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F18">
/// ### Coordinates (souřadnice) popisují sousedy
///
/// Grid (mřížka) ukládá nejdřív row (řádek): `board[y][x]`. Rostoucí x vede doprava, rostoucí y na
/// obrazovce dolů. Neighbour (soused) vznikne přičtením malého offset (posunu).
///
/// ```text
///           (0,-1)
/// (-1,0)    HERE    (1,0)
///           (0,1)
/// ```
///
/// ```java
/// int[][] board = {{2, 5, 8}, {1, 4, 7}};
/// int x = 1, y = 0;
/// int nx = x + 0, ny = y + 1;
/// if (ny >= 0 && ny < board.length && nx >= 0 && nx < board[ny].length) {
///     IO.println(board[ny][nx]);
/// }
/// ```
///
/// Přesun vede z 5 na 4, vypíše se tedy 4. Bounds (hranice) kontrolujeme před indexováním: array (pole)
/// za okrajem žádné cell (políčko) nemá. Diagonal (úhlopříčný posun) mění obě coordinates. Corridor
/// (chodba) opakuje stejný offset; neighbourhood (okolí) zkouší několik offset. Jde o různé postupy se
/// stejným modelem.
///
/// Předpověz výsledek offset `(-1,0)` ze stejného začátku. Který offset opustí tento board (hrací
/// plochu)? Než spojíš směry do loop (opakování), zakresli začátek a cíl na papír.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `for (int[] d : dirs)` — Spustí stejné hledání viditelnosti v každém ze čtyř směrů.
/// - `if (open(y, x))` — Teď zkouší další pozici jen jednou; zvaž délku celé chodby.
/// - `bulbs[y][x]` — Čte, zda právě navštívené políčko chodby obsahuje žárovku.
/// - `y += d[0]` — Posune řádek stejným směrovým posunem pro další krok.
///
/// ### Co už obstarává připravená část
///
/// open() odmítá zdi a polohy mimo desku. solved() používá visible() pro osvětlení i střety žárovek a
/// kontroluje očíslované zdi; přepínání žárovek a konzolové zobrazení je připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// dirs dodává čtyři posuny o jedno pole.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Opakovaně posouvej y a x, dokud platí open(y,x).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Místo jediného if použij while; posuny souřadnic ponech uvnitř.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Navrhni chodbu se zatáčkou a odvoď, která políčka uvidí jediná žárovka. Dej ji spoluhráči jako malou
/// světelnou hádanku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] walls = {"...", ".0.", "..."};
boolean[][] bulbs;
String outcome;
int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

/// reset(): Obnovuje počáteční kolo Light Up a maže stav patřící starému kolu.
void reset() {
    bulbs = new boolean[3][3];
    outcome = "";
}

boolean open(int r, int c) {
    return r >= 0 && r < 3 && c >= 0 && c < 3 && walls[r].charAt(c) == '.';
}

/// solved(): Vyžaduje osvětlení, žádné viditelné dvojice žárovek a správné počty u očíslovaných
/// zdí.
boolean solved() {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            if (open(r, c)) {
                if (bulbs[r][c] && visible(r, c) > 0 || !bulbs[r][c] && visible(r, c) == 0) {
                    return false;
                }
            } else {
                int count = 0;
                for (int[] d : dirs) {
                    int y = r + d[0];
                    int x = c + d[1];
                    if (open(y, x) && bulbs[y][x]) {
                        count++;
                    }
                }
                if (count != walls[r].charAt(c) - '0') {
                    return false;
                }
            }
        }
    }
    return true;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(open(r, c));
    bulbs[r][c] = !bulbs[r][c];
    if (solved()) {
        outcome = "All cells lit, no bulb conflicts, wall numbers satisfied!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Columns1-3. B bulb, + lit, . dark, digit wall with adjacent-bulb count.");
    for (int r = 0; r < 3; r++) {
        String s = (r + 1) + " ";
        for (int c = 0; c < 3; c++) {
            s += !open(r, c) ? walls[r].charAt(c) : bulbs[r][c] ? 'B' : visible(r, c) > 0 ? '+' : '.';
        }
        IO.println(s);
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Light Up");
    IO.println("Light every open cell without two bulbs seeing each other. A wall digit counts bulbs touching its four sides.");
    IO.println("Enter row column (1-3) to toggle a bulb. Light every open cell; bulbs cannot see each other. /r restarts; /q quits.");
    IO.println("Example: 1 1 toggles a bulb at the upper-left. B is a bulb; + is lit; a dot is still dark.");
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
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "Enter row column (1-3) to toggle a bulb. Light every open cell; bulbs cannot see each other. /r restarts; /q quits.");
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
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
