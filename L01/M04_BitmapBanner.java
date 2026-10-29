import module java.base;
class M04_BitmapBanner {
    /// # L01.04 · BitmapBanner
    ///
    /// Vytvoř velkou ceduli do arcade z malých vypsaných znaků #. Ručně připravená abeceda promění krátkou
    /// zprávu v pět řádků obrazového písma.
    ///
    /// **Tvoje mise:** Cedule nyní vypisuje jen prázdné místo. Oprav zprávu z bannerMessage tak, aby výchozí
    /// cedule říkala JAVA CLUB!.
    ///
    /// **Dnes trénuješ:** Literal (doslovný zápis) a output (výstup): zvolený text určuje, co další akce
    /// vytiskne. Mezery v uvozovkách jsou také skutečné znaky, i když jejich výstup může vypadat prázdně.
    ///
    /// Z hlavní složky obsahující README.md:
    /// - Hraní: `java L01/M04_BitmapBanner.java`
    /// - Kontrola této mise: `java L01/Check.java M04_BitmapBanner.java`
    ///
    /// Spusť command (příkaz) pro hraní uvedený výše a v výstup v terminálu si prohlédni pětiřádkovou
    /// ceduli; po opravě ji zkus přečíst z větší vzdálenosti. Po opravě: ulož soubor, spusť uvedenou
    /// kontrolu a zahraj si znovu.
    ///
    /// Potřebuješ výklad? Připomenutí · F02 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
    /// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
    String bannerMessage() {
        return "     ";
    }

    // <editor-fold defaultstate="collapsed" desc="Připomenutí · F02">
    /// String literal (doslovně zapsaný řetězec) je text mezi dvojitými uvozovkami. Vnější uvozovky
    /// říkají Javě, kde text začíná a končí; nevypisují se. I číslice v uvozovkách jsou text. Expression
    /// (výraz) naopak může před vypsáním vypočítat hodnotu.
    ///
    /// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
    ///
    /// ```java
    /// IO.println("Shelf 4");
    /// IO.println(4 + 2);
    /// IO.println("Shelf \"B\"");
    /// ```
    ///
    /// Předpověz tři řádky. Jsou to `Shelf 4`, `6` a `Shelf "B"`. V posledním literal představuje každé
    /// `\"` jednu uvozovku patřící do textu. Zpětné lomítko je zápis, nikoli další vypsaný znak.
    /// `IO.println` vypíše svůj argument (předanou hodnotu) a ukončí řádek. Mezery uvnitř literal
    /// zůstávají skutečnými mezerami; změna comment (komentářů) vypsanou zprávu nezmění. Rozlišuj tedy
    /// znaky zapsané ve source (zdrojovém textu) a znaky, které se opravdu objeví v output (výstupu).
    ///
    /// Nyní předpověz `IO.println(4 + 5);` a `IO.println("4 + 5");`.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
    /// ## Jak číst právě tyto řádky
    ///
    /// - `String` — Tato akce odpovídá textem místo čísla.
    /// - `bannerMessage()` — Prázdné závorky znamenají, že výběr výchozí cedule nepotřebuje input (vstup).
    /// - `{` — Odpovídající složené závorky obsahují tělo, které vytváří odpověď.
    /// - `return` — Předej zprávu zpět; tento statement (příkaz) sám ceduli netiskne.
    /// - `"     "` — Tento literal (doslovný text) obsahuje pět mezer, nikoli prázdné uvozovky. Písmo tyto
    ///   mezery zobrazí jako prázdná políčka.
    /// - `;` — Ukonči return statement (příkaz vrácení) za textovou hodnotou.
    ///
    /// ### Co už obstarává připravená část
    ///
    /// banner už obsahuje malé vzory písmen a sestaví pět řádků output (výstupu). main si vyžádá výchozí
    /// zprávu a vytiskne výsledek. Písmo podporuje A B C J L U V ! a mezery.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
    /// Tentokrát je výsledkem text v terminálu, nikoli samostatné okno nebo soubor. Starter (výchozí
    /// program) vypíše pět řádků mezer a potom běžně skončí; prázdný output (výstup) je zkoumaná chyba.
    /// Rozšiř panel, aby bylo písmo s pevnou šířkou dobře vidět. Připravené písmo podporuje pouze
    /// uvedená písmena, `!` a mezery, takže libovolný text může být odmítnut. Comment (komentář)
    /// popisující novou ceduli nezmění zprávu vrácenou bannerMessage.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
    /// Mezery jsou skutečné znaky, ale toto písmo je nechá prázdné.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
    /// Text nech uvnitř jedné dvojice dvojitých uvozovek.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
    /// Celý return statement (příkaz vrácení) je return "JAVA CLUB!";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
    Map<Character, String> font = Map.of(
        'A', "01110/10001/11111/10001/10001",
        'B', "11110/10001/11110/10001/11110",
        'C', "01111/10000/10000/10000/01111",
        'J', "00111/00010/00010/10010/01100",
        'L', "10000/10000/10000/10000/11111",
        'U', "10001/10001/10001/10001/01110",
        'V', "10001/10001/10001/01010/00100",
        '!', "00100/00100/00100/00000/00100",
        ' ', "00000/00000/00000/00000/00000"
    );

    String banner(String message, char ink) {
        if (message.isEmpty() || message.length() > 30) {
            throw new IllegalArgumentException("Message length: 1..30.");
        }
        String upper = message.toUpperCase(Locale.ROOT);
        var result = new StringBuilder();
        for (int row = 0; row < 5; row++) {
            for (char letter : upper.toCharArray()) {
                String glyph = font.get(letter);
                if (glyph == null) {
                    throw new IllegalArgumentException("Supported letters: A B C J L U V ! and space.");
                }
                for (char pixel : glyph.split("/")[row].toCharArray()) {
                    result.append(pixel == '1' ? ink : ' ');
                }
                result.append(' ');
            }
            result.append('\n');
        }
        return result.toString();
    }

    void main() {
        try {
            String message = bannerMessage();
            IO.println("Banner preview: [" + message + "]");
            IO.println("--- five picture rows below ---");
            IO.print(banner(message, '#'));
            IO.println("--- end of banner; spaces in the starter make blank rows ---");
        } catch (IllegalArgumentException invalid) {
            IO.println(invalid.getMessage());
        }
    }

    /// Připravené vstupní body: main.
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
    /// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
    /// - `++` — Zvětší číselnou hodnotu o jedna.
    /// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
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
    /// - `null` — Žádný object (objekt) tu není.
    /// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
    /// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
    /// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
    /// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
    /// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. print vypíše text bez
    ///   přidání nového řádku.
    // </editor-fold>

}
