import module java.base;
class M07_Codebreaker {
    /// # L02.07 · Codebreaker
    ///
    /// Break a four-digit code using clues that distinguish the right digit in the right place from a digit
    /// placed elsewhere. After the known training code, restart to try a hidden random code.
    ///
    /// **Your mission:** Entering the training code gives four exact digits but no win. Repair matchesCode so
    /// equal text is accepted even when it came from separate String values.
    ///
    /// **Today you learn:** Boolean decisions and text equality: boolean means true or false. For text,
    /// comparing the characters is different from asking whether two names point to the very same stored
    /// String object.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L02/M07_Codebreaker.java`
    /// - Check this mission: `java L02/Check.java M07_Codebreaker.java`
    ///
    /// Run the play command above and enter 1234 to investigate the failed training win; after
    /// repairing it, enter r to try a newly hidden code. After your repair: save the file, run the
    /// check command above, then play again.
    ///
    /// Need an explanation? Learn · F06 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    boolean matchesCode(String guess) {
        return secret == guess;
    }

    // <editor-fold defaultstate="expanded" desc="Learn · F06">
    /// A boolean expression produces `true` or `false`. `&&` requires both conditions, `||` needs at
    /// least one, and `!` reverses a boolean result. Comparisons such as `>=` include equality; `>` does
    /// not.
    ///
    /// These example statements belong inside a called method.
    ///
    /// ```java
    /// int age = 14;
    /// boolean hasTicket = false;
    /// boolean enter = age >= 12 && hasTicket;
    /// boolean recognised = "open".equals("OPEN");
    /// ```
    ///
    /// Predict both results: each is `false`. The age is sufficient, but the ticket condition is not.
    /// `equals` compares String contents and is case-sensitive; `equalsIgnoreCase` deliberately ignores
    /// letter case. For objects, `==` asks whether two references identify the same object, not whether
    /// their text matches. A content comparison and a range comparison answer different questions. Check
    /// values exactly at a boundary and just beyond it before trusting a condition.
    ///
    /// Now predict enter for age twelve with a ticket.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `boolean` — The result is a yes/no decision: true for a match and false otherwise.
    /// - `matchesCode(String guess)` — The supplied caller asks this rule about the complete guessed text.
    /// - `secret` — The stored target code for this round.
    /// - `guess` — The text received for this attempt, not necessarily the same stored object as secret.
    /// - `==` — With String objects this asks about identity, not matching characters; it is different from
    ///   numeric equality.
    /// - `return` — Give the decision back to submit, where it determines whether the round was won.
    ///
    /// ### What the prepared part already does
    ///
    /// submit validates the four digits, computes exact/elsewhere clues and asks matchesCode for the win
    /// decision. The first secret is 1234 for investigation; reset generates random codes for later rounds.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// Two separate String values can contain the same text.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Use the String content comparison operation.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Return secret.equals(guess).
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    String secret = "1234";
    int attempts;
    boolean won;
    int[] feedback = {0, 0};

    String newCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 4; i++) {
            code += random.nextInt(1, 7);
        }
        return code;
    }

    void reset() {
        secret = newCode();
        attempts = 0;
        won = false;
        feedback = new int[] {0, 0};
    }

    boolean over() {
        return won || attempts >= 10;
    }

    int[] score(String guess) {
        boolean[] usedSecret = new boolean[4];
        boolean[] usedGuess = new boolean[4];
        int exact = 0;
        int elsewhere = 0;
        for (int i = 0; i < 4; i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                exact++;
                usedSecret[i] = true;
                usedGuess[i] = true;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (usedGuess[i]) {
                continue;
            }
            for (int j = 0; j < 4; j++) {
                if (!usedSecret[j] && guess.charAt(i) == secret.charAt(j)) {
                    elsewhere++;
                    usedSecret[j] = true;
                    break;
                }
            }
        }
        return new int[] {exact, elsewhere};
    }

    boolean submit(String guess) {
        if (over() || !guess.matches("[1-6]{4}")) {
            return false;
        }
        feedback = score(guess);
        attempts++;
        won = matchesCode(guess);
        return true;
    }

    void main() {
        IO.println("Training opening: code 1234. Restart deals a random code.");
        IO.println("CODEBREAKER: guess four digits 1-6 in ten attempts. Repeats are allowed.");
        IO.println("Exact = right digit and position; elsewhere = right digit, different position.");
        boolean showState = true;
        while (true) {
            if (showState) {
                if (over()) {
                    IO.println((won ? "You win!" : "Out of attempts!") + " Code: " + secret);
                }
                showState = false;
            }
            String input = IO.readln(over() ? "Enter or /r: new round; /q: quit: " : "Attempt " + (attempts + 1) + "/10, r restart, q quit: ");
            if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
                return;
            }
            input = input.trim();
            if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (over() && input.isBlank()))) {
                reset();
                showState = true;
                continue;
            }
            if (over()) {
                IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
                continue;
            }
            if (submit(input)) {
                showState = true;
                IO.println(input + " -> exact " + feedback[0] + ", elsewhere " + feedback[1]);
            } else {
                IO.println("Enter exactly four digits 1-6 during an unfinished round.");
            }
        }
    }

    /// Prepared entry points: main, reset.
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
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `==` — Compares primitive values. For text contents, use equals instead.
    /// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
    /// - `+=` — Adds to the old value and stores the result back under the same name.
    /// - `+` — Adds numbers. When a String is involved, joins text instead.
    /// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
    ///   direction. In List<String>, angle brackets describe the element type.
    /// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
    ///   the second.
    /// - `:` — In for (int value : values), reads each element in turn. It also separates the two
    ///   alternatives of ? : or follows a switch label.
    /// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
    ///   count.
    /// - `new` — Creates a fresh value or object, such as a new array or window.
    /// - `return` — Ends this method call, optionally handing a result back to its caller.
    /// - `if` — Runs its body only when the condition in parentheses is true.
    /// - `else` — The alternative when the preceding if condition is false.
    /// - `for` — Repeats a block over a range or a sequence of values.
    /// - `while` — Keeps repeating its body while its condition stays true.
    /// - `null` — No object is present. IO.readln returns null when the input stream ends.
    /// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
    /// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
    // </editor-fold>

}
