/// # Task · CipherPostcards
/// Lesson: 14.3 Ask which kind of data arrived — 03-patterns.html
///
/// You received a shifted postcard without a key. The normal cipher machine already works; turn it
/// into your own decoding assistant and choose the readable candidate yourself.
///
/// **Your mission:** Add a crack mode for a Caesar postcard whose shift is unknown. It must display
/// all 26 decoded candidates, one per line as Shift 0: ... through Shift 25: ..., so the player can
/// recognise a message. Keep the three normal cipher modes and their decoding intact. The tool
/// enumerates possibilities; it must not pretend to know which sentence is meaningful.
///
/// Choose a normal cipher to confirm the starter works. Your new mode is crack and needs no key; try
/// KHOOR, then solve a friend’s Caesar postcard.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// Add another toy cipher card that reverses text. Connect its data shape, selection and
/// interpretation. Predict which exhaustive switch will need another alternative, then make a puzzle
/// for a friend.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
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
