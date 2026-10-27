/// # L02.06 · MadLibsStoryMachine
///
/// Dej stroji jméno, tvora, místo a předmět a přečti si absurdní příběh, který z nich sestaví. Víceslovné
/// odpovědi mohou vytvořit mnohem zvláštnější výsledek než jednotlivá slova.
///
/// **Tvoje mise:** Mezery omylem napsané kolem odpovědi pronikají do příběhu. Oprav cleanWord tak, aby
/// okrajové mezery zmizely, ale fráze jako Red Fox zůstala celá.
///
/// **Dnes trénuješ:** Text celého řádku: přečíst frázi se liší od převzetí jen jejího prvního slova.
/// Pracuj s vráceným String (řetězcem) a zachovej mezery, které patří dovnitř fráze.
///
/// Použij Run File a na otázku na jméno napiš Red Fox s mezerami kolem, potom doplň další tři fráze a
/// prohlédni hotový příběh.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F05 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
String cleanWord(String word) {
    return word.strip();
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F05">
/// `IO.readln` přijímá celý řádek včetně vnitřních mezer. String operation (operace nad řetězcem)
/// vrací text, nemění původní String (řetězec). Potřebuješ-li výsledek, musíš jej uchovat.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// String raw = "  Quiet Room  ";
/// String cleaned = raw.strip();
/// String loud = cleaned.toUpperCase(Locale.ROOT);
/// ```
///
/// Předpověz všechny tři hodnoty. `raw` stále obsahuje okrajové mezery, `cleaned` je `Quiet Room` a
/// `loud` je `QUIET ROOM`. `strip()` odstraní okrajové whitespace (bílé znaky), nikoli mezeru mezi
/// slovy. Tečka vybere operaci hodnoty vlevo; závorky předávají její argument (argumenty).
/// `Locale.ROOT` žádá převod velikosti písmen nezávislý na jazyce místo místního nastavení počítače.
/// Připravený input code (vstupní část) řeší `null` při konci vstupu; prázdný řádek je jiná hodnota.
/// Samotné zavolání operace ještě nenahradí text uchovaný pod původním jménem.
///
/// Nyní předpověz cleaned a loud pro raw text `"  Soft Sky  "`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `String cleanWord(String word)` — Jediný String (řetězec) vstoupí jako word a očištěný String má
///   odejít jako výsledek.
/// - `word` — Místní jméno této odpovědi; může obsahovat několik slov i mezery.
/// - `return word;` — Současné tělo vrací přijatý text beze změny, včetně okrajových mezer.
/// - `{` — Odpovídající složené závorky uzavírají malé pravidlo pro čištění textu; otázky zůstávají
///   jinde.
///
/// ### Co už obstarává připravená část
///
/// main se zeptá na čtyři celé řádky. story volá cleanWord pro každou odpověď, ověří použitelnou délku a
/// vloží očištěné fráze do připraveného příběhu. Upravuješ očištění frází, nikoli zpracování jejich
/// vstupu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nerozděluj text podle mezer: jméno může mít několik slov.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// String.strip vrací očištěnou kopii.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Použij return word.strip();
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String story(String name, String creature, String place, String object) {
    name = cleanWord(name);
    creature = cleanWord(creature);
    place = cleanWord(place);
    object = cleanWord(object);
    for (String word : List.of(name, creature, place, object)) {
        if (word.isBlank() || word.length() > 40) {
            throw new IllegalArgumentException("Words need 1..40 characters.");
        }
    }
    return name + " hired a " + creature + " to guard " + place + ".\n"
        + "Unfortunately, the only security equipment was a " + object + ".\n"
        + "By midnight, the " + creature + " had promoted the " + object + " to manager.\n";
}

void main(String[] args) {
    String[] labels = {"A name", "A creature", "A place", "An object"};
    String[] words = new String[4];
    for (int i = 0; i < 4; i++) {
        words[i] = args.length > i ? args[i] : IO.readln(labels[i] + " (q to quit): ");
        if (words[i] == null || words[i].equalsIgnoreCase("q")) {
            return;
        }
    }
    try {
        IO.print(story(words[0], words[1], words[2], words[3]));
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

