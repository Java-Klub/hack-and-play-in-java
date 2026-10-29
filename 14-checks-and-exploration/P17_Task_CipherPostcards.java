/// # Úkol · CipherPostcards
/// Lekce: 14.3 Zjisti, která datová varianta přišla — 03-patterns.html
///
/// Dostal jsi posunutou pohlednici bez klíče. Běžný šifrovací stroj už funguje; proměň jej ve
/// vlastního lušticího pomocníka a čitelný výsledek vyber sám.
///
/// **Tvoje mise:** Přidej crack režim pro Caesarovu pohlednici s neznámým posunem. Má ukázat všech 26
/// dekódovaných kandidátů, po řádcích Shift 0: ... až Shift 25: ..., aby hráč poznal zprávu. Zachovej
/// tři běžné šifry i jejich dekódování. Nástroj vyjmenovává možnosti; nepředstírá znalost smyslu
/// věty.
///
/// **Zkoumání:** Vytvoř nástroj k řešení hádanky průchodem kandidátů a rozšířením směrování podle
/// typu.
///
/// Běžnou šifrou nejdřív ověř fungující výchozí verzi. Nový režim se jmenuje crack a nepotřebuje
/// klíč; zkus KHOOR a potom pohlednici od kamaráda.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// Přidej další toy cipher card (kartu hračkové šifry), která obrací text. Propoj datový tvar, výběr
/// i vyhodnocení. Předpověz, který exhaustive switch (úplný výběr) potřebuje novou větev, a připrav
/// hádanku kamarádovi.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
String transform(String text, Cipher cipher, boolean decode) {
    return switch (cipher) {
        case Caesar(var shift) -> applyText(text, "caesar", Integer.toString(shift), decode);
        case Substitution(var alphabet) -> applyText(text, "substitution", alphabet, decode);
        case Vigenere(var keyword) -> applyText(text, "vigenere", keyword, decode);
    };
}

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
