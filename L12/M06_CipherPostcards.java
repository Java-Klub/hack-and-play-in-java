import module java.base;
class M06_CipherPostcards {
    /// # L12.06 · CipherPostcards
    ///
    /// Send a puzzle postcard in one of three toy ciphers and decode it again. Changing the cipher card
    /// should change the transformation while keeping spaces and punctuation recognisable.
    ///
    /// **Your mission:** The Vigenere alternative is routed to an unchanged Caesar result. Make it use its
    /// own carried key and decoding choice while retaining the other cipher alternatives.
    ///
    /// **Today you learn:** Record patterns give each closed alternative its own named data. The switch
    /// should forward that matched data to the correct supplied algorithm, rather than treating every card as
    /// the same operation.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L12/M06_CipherPostcards.java`
    /// - Check this mission: `java L12/Check.java M06_CipherPostcards.java`
    ///
    /// After the play command, press Enter for vigenere and ORANGE, then type ATTACK AT DAWN!; read the
    /// selected cipher and compare Result with your original message: the Vigenere branch should change
    /// its letters. After your repair: save the file, run the check command above, then play again.
    ///
    /// Need an explanation? Recall · F35 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    String transform(String text, Cipher cipher, boolean decode) {
        return switch (cipher) {
            case Caesar(var shift) -> applyText(text, "caesar", Integer.toString(shift), decode);
            case Substitution(var alphabet) -> applyText(text, "substitution", alphabet, decode);
            case Vigenere(var keyword) -> applyText(text, "vigenere", keyword, decode);
        };
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F35">
    /// ### Record patterns: choose a shape and unpack its data
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
    /// `label(new Stars(2))` returns `"2 stars"`. Sealed closes the alternatives; permits lists them. A
    /// record pattern matches its record type and gives component values local names, in declaration
    /// order. `var` infers their types. The `when` guard must also pass; otherwise matching continues.
    /// The last Stars case handles nonpositive counts. Every permitted alternative needs handling here.
    ///
    /// Predict: which case handles `new Stars(0)`, and what text does `new Name("Nova")` produce?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `return switch (cipher)` — Computes the method result from exactly one matched cipher alternative.
    /// - `case Vigenere(var keyword)` — Extracts the keyword stored in this particular Vigenere record.
    /// - `"caesar", "0"` — Currently selects a different algorithm with a fixed zero shift.
    /// - `decode` — Carries the caller’s choice of forward or reverse transformation.
    ///
    /// ### What the prepared part already does
    ///
    /// chooseCipher creates the selected typed alternative; transform passes its record components to
    /// applyText. The single console flow prints the selected cipher and encoded message.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="For this game">
    /// ### Match the card, then use its own data
    ///
    /// For a Vigenere record carrying `"CAT"`, the matched keyword is CAT, regardless of the local
    /// variable's name. Predict whether changing that card to `"DOG"` should change the encoded letters.
    /// Compare two calls with the same message and key but opposite decode values; forwarding the
    /// caller's choice is part of the contract. The prepared applyText accepts a message, a mode name,
    /// its key text and the decode flag. The matching case chooses which prepared algorithm receives
    /// those values. Punctuation should survive that routing choice.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// The matched keyword belongs to this Vigenere value.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Use that component, rather than a fixed key or another cipher mode.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// applyText already handles letter case, punctuation, key validation and reverse transformation.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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

    /// Use the play command above to start the program. The check command checks the rule without
    /// opening a window. The supplied part supports only the main game or experiment described above.
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

    // <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
    /// ## An unfamiliar piece of Java?
    ///
    /// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
    ///
    /// - `///` — Words for the reader. Java skips these comment lines when running.
    /// - `;` — Ends one statement, like a full stop. A semicolon inside a for header separates its three
    ///   parts.
    /// - `()` — After a name: call an action, or list its parameters in its declaration. Empty parentheses
    ///   mean no supplied arguments.
    /// - `{}` — Braces group a block of code. The opening and closing brace belong together.
    /// - `.` — Accesses something belonging to the value or type on its left, such as IO.println.
    /// - `void` — This method performs an action and returns no result value.
    /// - `main` — Java starts the program here. Other method declarations do not run just because they appear
    ///   earlier in the file.
    /// - `int` — A type for whole numbers within a fixed range.
    /// - `boolean` — A value that is either true or false.
    /// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
    /// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
    /// - `'` — Single quotes enclose one character, such as a board marker.
    /// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
    ///   String.
    /// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
    ///   type.
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `==` — Compares primitive values. For text contents, use equals instead.
    /// - `!=` — Checks that two values differ.
    /// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
    /// - `%` — The remainder after division; useful for repeated positions or turns.
    /// - `+` — Adds numbers. When a String is involved, joins text instead.
    /// - `-` — Subtracts numbers; in front of one number, changes its sign.
    /// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
    ///   direction. In List<String>, angle brackets describe the element type.
    /// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
    ///   the second.
    /// - `:` — In for (int value : values), reads each element in turn. It also separates the two
    ///   alternatives of ? : or follows a switch label.
    /// - `new` — Creates a fresh value or object, such as a new array or window.
    /// - `return` — Ends this method call, optionally handing a result back to its caller.
    /// - `if` — Runs its body only when the condition in parentheses is true.
    /// - `else` — The alternative when the preceding if condition is false.
    /// - `for` — Repeats a block over a range or a sequence of values.
    /// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
    /// - `case` — One alternative of a switch; the arrow points to what it does or returns.
    /// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
    ///   later.
    /// - `implements` — Says that a record or class fulfils the named interface role.
    /// - `record` — A small named bundle of values. Its component accessors use the component names with ().
    /// - `interface` — Describes a role that different implementations can fulfil. The caller can use that
    ///   role without choosing its internals.
    /// - `sealed` — Restricts a data type to its explicitly listed alternatives.
    /// - `null` — No object is present. IO.readln returns null when the input stream ends.
    /// - `try` — A block whose possible failures can be handled by the following catch.
    /// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
    /// - `throw` — Reports a failure by creating or passing an exception.
    /// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
    /// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
    // </editor-fold>

}
