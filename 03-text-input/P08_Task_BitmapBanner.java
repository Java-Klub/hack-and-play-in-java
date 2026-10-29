/// # Vyřešený úkol · BitmapBanner
/// Lekce: 3.3 Nech hráče měnit příběh — 03-stories.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Rozsviť klubovou ceduli.** Po spuštění je mezi oddělovacími řádky pět prázdných řádků.
/// Program písmena kreslit umí, ale dostává zprávu složenou z mezer.
///
/// **Úkol:** V `bannerMessage` níže nahraď mezery uvnitř uvozovek textem `JAVA CLUB!`, včetně
/// mezery mezi slovy a vykřičníku. Ostatní znaky řádku ponech.
///
/// **Výsledek:** Po spuštění se v textovém výstupu objeví velká cedule JAVA CLUB! složená ze
/// znaků `#`. Samostatné okno se neotevírá.

String bannerMessage() {
    return "JAVA CLUB!";
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Tentokrát je výsledkem text v terminálu, nikoli samostatné okno nebo soubor. Starter (výchozí
/// program) vypíše pět řádků mezer a potom běžně skončí; prázdný output (výstup) je zkoumaná chyba.
/// Rozšiř panel, aby bylo písmo s pevnou šířkou dobře vidět. Připravené písmo podporuje pouze
/// uvedená písmena, `!` a mezery, takže libovolný text může být odmítnut. Comment (komentář)
/// popisující novou ceduli nezmění zprávu vrácenou bannerMessage.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Z dostupných písmen A B C J L U V, mezer a ! vytvoř absurdní klubovou ceduli. Zkus CLUB JAVA! nebo
/// opakovaný poplach. Existující kreslicí znak # změň na jiný jediný znak a vyber čitelnější ceduli.
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

// </editor-fold>
