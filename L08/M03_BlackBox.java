import module java.base;
class M03_BlackBox {
    /// # L08.03 · BlackBox
    ///
    /// Posílej paprsky neviditelnou krabicí a odvozuj, kde jsou skryté kuličky. Zásahy, odrazy i zahnuté
    /// výstupy prověří tvou teorii.
    ///
    /// **Tvoje mise:** Paprsek prověřuje tutéž stranu dvakrát, takže některé stopy lžou. Oprav výpočet
    /// bočních sousedů v trace() a zachovej připravená pravidla paprsku.
    ///
    /// **Dnes trénuješ:** Grid neighbours (sousedé v mřížce): vlevo a vpravo se určují podle aktuálního směru
    /// paprsku, nikoli pevně podle obrazovky. Sleduj kolmé posuny při zatáčení paprsku.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L08/M03_BlackBox.java`
    /// - Kontrola této mise: `java L08/Check.java M03_BlackBox.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše a zadáním ray N1 prozkoumej krabici shora, potom
    /// pozorování porovnej s paprsky z dalších označených vstupů. Po opravě: ulož soubor, spusť
    /// uvedenou kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F18 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    String trace(boolean[][] grid, String entry) {
        require(entry.matches("[NSEW][1-4]"));
        int n = entry.charAt(1) - '1';
        char side = entry.charAt(0);
        int x = side == 'W' ? -1 : side == 'E' ? 4 : n;
        int y = side == 'N' ? -1 : side == 'S' ? 4 : n;
        int dx = side == 'W' ? 1 : side == 'E' ? -1 : 0;
        int dy = side == 'N' ? 1 : side == 'S' ? -1 : 0;
        boolean entering = true;
        var seen = new HashSet<String>();
        while (true) {
            if (!seen.add(x + "," + y + "," + dx + "," + dy)) {
                return "LOOP";
            }
            int nx = x + dx;
            int ny = y + dy;
            if (ball(grid, nx, ny)) {
                return "HIT";
            }
            boolean left = ball(grid, nx - dy, ny + dx);
            boolean right = ball(grid, nx - dy, ny + dx);
            if (entering && (left || right)) {
                return "REFLECT";
            }
            if (left && right) {
                dx = -dx;
                dy = -dy;
            } else {
                if (left) {
                    int old = dx;
                    dx = dy;
                    dy = -old;
                } else {
                    if (right) {
                        int old = dx;
                        dx = -dy;
                        dy = old;
                    } else {
                        x = nx;
                        y = ny;
                        if (x < 0 || x >= 4 || y < 0 || y >= 4) {
                            String exit = port(x, y);
                            return exit.equals(entry) ? "REFLECT" : exit;
                        }
                    }
                }
            }
            entering = false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F18">
    /// ### Grids: nejdřív řádek, potom sloupec
    ///
    /// V array of rows (poli řádků) první index (pozice) vybírá řádek a druhý pozici v něm. Řádky mohou
    /// mít různé délky. Neighbour (soused) je posun od buňky, nikoli další absolutní pozice. Před
    /// kontrolou existence kandidáta urči povolené směry.
    ///
    /// ```java
    /// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
    /// int row = 0;
    /// int column = 1;
    /// IO.println(mosaic[row][column + 1]);
    /// ```
    ///
    /// Výstup je devět: řádek nula a sloupec dva vyberou tento element (prvek). Pohyb dolů mění row;
    /// doprava mění column. Který z těchto pohybů z pravé horní cell (buňky) zůstane uvnitř? U úhlopříčky
    /// se mění obě souřadnice; bounds (meze) musíš ověřit před přístupem ke kandidátovi.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `int nx = x + dx` — Najde další políčko vpřed, ještě než se rozhodne o vstupu paprsku.
    /// - `nx - dy, ny + dx` — Kolmý posun najde jednoho bočního souseda vzhledem k políčku vpředu.
    /// - `left && right` — Připravené pravidlo odlišuje kuličky na obou stranách od kuličky jen na jedné
    ///   straně.
    /// - `!seen.add(` — Pokud je celá poloha se směrem už uložená, tento test pozná zacyklení.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// ball() bezpečně odpovídá na obsazenost a port() pojmenovává výstupy na okraji. Zbytek trace() řeší
    /// zásahy, odrazy a zacyklení; equivalent() při ověření odhadu porovnává všechna pozorování paprsků.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
    /// ### Kolmý směr je vztažený k pohybu
    ///
    /// Direction (směr) je dvojice změn, ne absolutní pozice obrazovky. Paprsek mířící doprava ve směru
    /// `(1,0)` má dva boční posuny `(0,1)` a `(0,-1)`. Jsou opačné. Společným počátkem obou bočních
    /// dotazů je další pozice vpřed. Nakresli stejnou konstrukci pro paprsek mířící vzhůru. Zachovej
    /// připravená pravidla otočení a odrazu; s pohybem mají souhlasit dvě odlišné boční pozice.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Dopředný vektor je (dx,dy) a další pole (nx,ny).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Kolmé posuny jsou (-dy,+dx) a (+dy,-dx).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Levou stranu ponech (nx-dy,ny+dx); pravá má být (nx+dy,ny-dx).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    boolean[][] hidden;
    boolean[][] marked;
    List<String> clues;
    String outcome;
    String message;

    /// reset(): Obnovuje počáteční kolo Black Box a maže stav patřící starému kolu.
    void reset() {
        hidden = new boolean[4][4];
        hidden[1][1] = hidden[2][3] = true;
        marked = new boolean[4][4];
        clues = new ArrayList<>();
        outcome = message = "";
    }

    boolean ball(boolean[][] grid, int x, int y) {
        return x >= 0 && x < 4 && y >= 0 && y < 4 && grid[y][x];
    }

    String port(int x, int y) {
        return y < 0 ? "N" + (x + 1) : y >= 4 ? "S" + (x + 1) : x < 0 ? "W" + (y + 1) : "E" + (y + 1);
    }

    /// equivalent(): Porovnává každý okrajový paprsek a přijímá každé pozorováním shodné skryté
    /// rozmístění.
    boolean equivalent() {
        for (char side : new char[]{'N', 'S', 'E', 'W'}) {
            for (int n = 1; n <= 4; n++) {
                if (!trace(hidden, "" + side + n).equals(trace(marked, "" + side + n))) {
                    return false;
                }
            }
        }
        return true;
    }

    /// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
    void command(String input) {
        require(outcome.isEmpty());
        String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
        if (p.length == 2 && p[0].equals("RAY")) {
            clues.add(p[1] + " -> " + trace(hidden, p[1]));
            message = "Probe recorded";
        } else {
            if (p.length == 3 && p[0].equals("MARK")) {
                int r = Integer.parseInt(p[1]) - 1;
                int c = Integer.parseInt(p[2]) - 1;
                require(r >= 0 && r < 4 && c >= 0 && c < 4);
                marked[r][c] = !marked[r][c];
                message = marked[r][c] ? "Guess marked" : "Guess removed";
            } else {
                require(p.length == 1 && p[0].equals("CHECK"));
                int count = 0;
                for (boolean[] row : marked) {
                    for (boolean b : row) {
                        if (b) {
                            count++;
                        }
                    }
                }
                require(count == 2);
                if (equivalent()) {
                    outcome = "All possible ray observations agree: solved!";
                    message = "";
                } else {
                    message = "Some observations disagree. Revise the marks.";
                }
            }
        }
    }

    /// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
    void display() {
        IO.println("Exactly two balls. Borders N/S use columns1-4; E/W use rows1-4.");
        for (int r = 0; r < 4; r++) {
            String s = (r + 1) + " ";
            for (boolean b : marked[r]) {
                s += b ? "O " : ". ";
            }
            IO.println(s);
        }
        for (String clue : clues) {
            IO.println(clue);
        }
        IO.println(message);
    }

    /// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
    /// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
    /// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
    void main() {
        reset();
        IO.println("Black Box");
        IO.println("Find two hidden balls by probing rays, then mark two guesses and check their observations.");
        IO.println("ray N1/S1/E1/W1 probes; mark row col toggles a guess; check tests all ray observations. /r restarts; /q quits.");
        IO.println("Example: ray N1 enters from the north at column 1; mark 2 2 toggles a guess. HIT is absorption; REFLECT returns to entry.");
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
                IO.println("Invalid move. " + "ray N1/S1/E1/W1 probes; mark row col toggles a guess; check tests all ray observations. /r restarts; /q quits.");
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
    /// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
    ///   měnit velikost.
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
    ///   řádek vstupu.
    // </editor-fold>

}
