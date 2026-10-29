/// # L12.06 · CipherPostcards
///
/// Pošli hádankovou pohlednici v jedné ze tří hračkových šifer a zase ji rozlušti. Výměna šifrovací karty
/// má změnit převod a nechat rozpoznatelné mezery i interpunkci.
///
/// **Tvoje mise:** Alternativa Vigenere míří do Caesar s nezměněným výsledkem. Ať použije vlastní
/// přenášený klíč a volbu decode při zachování ostatních šifer.
///
/// **Dnes trénuješ:** Record pattern (vzor record) dává každé uzavřené alternativě vlastní pojmenovaná
/// data. Switch má předat rozpoznaná data správnému připravenému postupu, ne považovat každou kartu za
/// stejnou operaci.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L12/M06_CipherPostcards.java`
/// - Kontrola této mise: `java L12/Check.java M06_CipherPostcards.java`
///
/// Po spuštění programu stiskni Enter pro vigenere a ORANGE, potom napiš ATTACK AT DAWN!; přečti
/// zvolený cipher (šifru) a porovnej Result s původní zprávou: Vigenere branch (větev) má změnit
/// její písmena. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F35 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
String transform(String text, Cipher cipher, boolean decode) {
    return switch (cipher) {
        case Caesar(var shift) -> applyText(text, "caesar", Integer.toString(shift), decode);
        case Substitution(var alphabet) -> applyText(text, "substitution", alphabet, decode);
        case Vigenere(var keyword) -> applyText(text, "vigenere", keyword, decode);
    };
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F35">
/// ### Record patterns (vzory záznamů): vyber typ a rozbal data
///
/// ```java
/// sealed interface Badge permits Name, Stars {}
/// record Name(String text) implements Badge {}
/// record Stars(int count) implements Badge {}
/// String label(Badge badge) {
///     return switch (badge) {
///         case Name(var text) -> text;
///         case Stars(var n) when n > 0 -> n + " stars";
///         case Stars(var n) -> "none";
///     };
/// }
/// ```
///
/// `label(new Stars(2))` vrátí `"2 stars"`. Sealed uzavírá alternativy; permits je vyjmenovává.
/// Record pattern rozpozná typ záznamu a dá hodnotám složek místní jména v pořadí deklarace. `var`
/// odvodí jejich typy. Musí platit také when guard (dodatečná podmínka); jinak hledání pokračuje.
/// Poslední Stars větev zpracuje nekladné počty. Každá povolená alternativa zde potřebuje zpracování.
///
/// Předpověz: která větev zpracuje `new Stars(0)` a jaký text vznikne pro `new Name("Nova")`?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `return switch (cipher)` — Vypočítá výsledek method (metody) podle právě jedné rozpoznané
///   alternativy šifry.
/// - `case Vigenere(var keyword)` — Vybere keyword uložený právě v tomto Vigenere record.
/// - `"caesar", "0"` — Nyní vybírá jiný postup s pevným nulovým posunem.
/// - `decode` — Přenáší volbu caller (volající části) mezi dopředným a zpětným převodem.
///
/// ### Co už obstarává připravená část
///
/// chooseCipher vytvoří zvolenou typovanou alternativu; transform předá record components (složky
/// záznamu) do applyText. Jediný console flow (průběh v konzoli) vypíše zvolený cipher (šifru) a
/// zakódovanou zprávu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Rozpoznej kartu a použij její vlastní data
///
/// U record (záznamu) Vigenere nesoucího `"CAT"` je rozpoznaný keyword CAT bez ohledu na jméno místní
/// proměnné. Předpověz, zda změna této karty na `"DOG"` má změnit zašifrovaná písmena. Porovnej dvě
/// volání se stejnou zprávou a klíčem, ale opačnou hodnotou decode; předání volby volajícího patří ke
/// contract (smlouvě). Připravené applyText přijímá zprávu, název režimu, text jeho klíče a přepínač
/// decode. Rozpoznaná větev vybere, který připravený algoritmus tato data dostane. Interpunkce má
/// zůstat zachovaná.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Keyword z match (rozpoznání) patří této hodnotě Vigenere.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Použij tuto component (složku), nikoli pevný klíč či jiný režim.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// ApplyText už řeší velikost písmen, interpunkci, ověření klíče a opačný převod.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String applyText(String text, String mode, String key, boolean decode) {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String upper = key.toUpperCase(Locale.ROOT);
    if (mode.equals("substitution") && (!upper.matches("[A-Z]{26}") || upper.chars().distinct().count() != 26)) {
        throw new IllegalArgumentException("Substitution key needs each A-Z once.");
    }
    if (mode.equals("vigenere") && !key.matches("[A-Za-z]+")) {
        throw new IllegalArgumentException("Vigenere key needs ASCII letters.");
    }
    if (!List.of("caesar", "substitution", "vigenere").contains(mode)) {
        throw new IllegalArgumentException("Unknown cipher.");
    }
    int shift = mode.equals("caesar") ? Integer.parseInt(key) : 0;
    var output = new StringBuilder();
    int position = 0;
    for (char c : text.toCharArray()) {
        boolean lower = c >= 'a' && c <= 'z';
        int index = c >= 'A' && c <= 'Z' ? c - 'A' : lower ? c - 'a' : -1;
        if (index < 0) {
            output.append(c);
            continue;
        }
        int result;
        if (mode.equals("substitution")) {
            result = decode ? upper.indexOf(alphabet.charAt(index)) : upper.charAt(index) - 'A';
        } else {
            int amount = mode.equals("caesar") ? Math.floorMod(shift, 26) : upper.charAt(position % upper.length()) - 'A';
            result = Math.floorMod(index + (decode ? -amount : amount), 26);
        }
        output.append((char)((lower ? 'a' : 'A') + result));
        position++;
    }
    return output.toString();
}

sealed interface Cipher permits Caesar, Substitution, Vigenere {}
record Caesar(int shift) implements Cipher {}
record Substitution(String alphabet) implements Cipher {}
record Vigenere(String keyword) implements Cipher {}
Cipher chooseCipher(String mode, String key) {
    return switch (mode) {
        case "caesar" -> new Caesar(Integer.parseInt(key));
        case "substitution" -> new Substitution(key);
        case "vigenere" -> new Vigenere(key);
        default -> throw new IllegalArgumentException("Unknown cipher.");
    };
}

/// Program spusť uvedeným command (příkazem) pro hraní. Kontrola hry ověří pravidlo bez otevření
/// okna. Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
void main() {
    String mode = IO.readln("Cipher caesar/substitution/vigenere (blank vigenere, q quits): ");
    if (mode == null || mode.strip().equalsIgnoreCase("q")) return;
    mode = mode.isBlank() ? "vigenere" : mode.strip().toLowerCase(Locale.ROOT);
    String key = IO.readln("Key (blank 3 / reversed alphabet / ORANGE; q quits): ");
    if (key == null || key.strip().equalsIgnoreCase("q")) return;
    if (key.isBlank()) key = mode.equals("caesar") ? "3" : mode.equals("substitution") ? "ZYXWVUTSRQPONMLKJIHGFEDCBA" : "ORANGE";
    String text = IO.readln("Message (q quits): ");
    if (text == null || text.equalsIgnoreCase("q")) return;
    try {
        Cipher cipher = chooseCipher(mode, key);
        IO.println("Selected cipher: " + cipher);
        IO.println("Result: " + transform(text, cipher, false));
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
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
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
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
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `implements` — Říká, že record nebo class (třída) plní roli uvedeného interface (rozhraní).
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `interface` — Popisuje role (roli), kterou mohou plnit různé implementation (implementace). Volající
///   používá roli bez znalosti jejích vnitřností.
/// - `sealed` — Omezuje data type (datový typ) na výslovně uvedené alternativy.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
