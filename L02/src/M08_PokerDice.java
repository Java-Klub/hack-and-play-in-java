/// # L02.08 · PokerDice
///
/// Roll five dice, keep promising values and reroll selected positions before banking the hand. Five
/// rounds let you compare safe small rewards with a chase for stronger combinations.
///
/// **Your mission:** The training hand 2,2,2,3,4 is incorrectly rewarded as a full house. Repair
/// fullHouse so both the triple and the pair are required.
///
/// **Today you learn:** Combining boolean conditions: OR accepts either part, while AND requires both.
/// Work out the two truth values separately for a triple without a pair before combining them.
///
/// Use Run File to inspect the training hand and its points, then use a command such as roll 4 5 to
/// reroll only those two dice before bank.
/// After your repair: Save → Test File → play again.
boolean fullHouse(int largestGroup, int pairs) {
    return largestGroup == 3 || pairs == 1;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `boolean fullHouse(int largestGroup, int pairs)` — The rule receives two counted facts and returns
///   one classification decision.
/// - `largestGroup == 3` — Test whether the largest equal-die group contains three dice; == compares
///   these numbers.
/// - `pairs == 1` — Test whether exactly one pair was found.
/// - `||` — Logical OR: true when at least one side is true, including cases where the other side is
///   false.
/// - `return` — Return the combined true/false result to the points calculation.
///
/// ### What the prepared part already does
///
/// points already counts dice groups and asks fullHouse about this category. reset supplies the known
/// training hand; later deal calls roll random hands, and command handles rerolls and banking.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Write down whether each half is true for three-kind without a pair.
/// 2. OR accepts either half; this rule needs both.
/// 3. Join the two comparisons with &&.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] dice;
int rolls;
int round;
int total;
String outcome;
Random random = new Random();

void deal() {
    dice = new int[5];
    for (int i = 0; i < 5; i++) {
        dice[i] = random.nextInt(1, 7);
    }
    rolls = 1;
}

void reset() {
    round = total = 0;
    outcome = "";
    deal();
    dice = new int[]{2, 2, 2, 3, 4};
}

int points() {
    int[] n = new int[7];
    for (int d : dice) {
        n[d]++;
    }
    int max = Arrays.stream(n).max().orElse(0);
    int pairs = 0;
    for (int v : n) {
        if (v == 2) {
            pairs++;
        }
    }
    boolean straight = true;
    for (int i = 2; i <= 5; i++) {
        straight &= n[i] == 1;
    }
    straight &= n[1] == 1 || n[6] == 1;
    return max == 5 ? 50 : max == 4 ? 40 : fullHouse(max, pairs) ? 35 : straight ? 30 : max == 3 ? 20 : pairs == 2 ? 10 : pairs == 1 ? 5 : 0;
}

void reroll(String[] p) {
    require(rolls < 3 && p.length > 1);
    var ids = new HashSet<Integer>();
    for (int i = 1; i < p.length; i++) {
        int n = Integer.parseInt(p[i]) - 1;
        require(n >= 0 && n < 5 && ids.add(n));
    }
    for (int n : ids) {
        dice[n] = random.nextInt(1, 7);
    }
    rolls++;
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("bank")) {
        total += points();
        round++;
        if (round == 5) {
            outcome = "Five rounds finished. Total: " + total;
        } else {
            deal();
        }
    } else {
        String[] p = input.split("\\s+");
        require(p[0].equalsIgnoreCase("roll"));
        reroll(p);
    }
}

void display() {
    IO.println("Round " + (round + 1) + "/5; dice " + Arrays.toString(dice) + "; roll " + rolls + "/3; current points " + points() + "; total " + total);
    IO.println("Five50 Four40 FullHouse35 Straight30 Three20 TwoPairs10 Pair5 High0. Equal categories tie.");
}

void main() {
    reset();
    IO.println("Poker Dice");
    IO.println("roll positions(1-5) rerolls those dice, at most twice; bank scores the hand. Five rounds. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. roll positions(1-5) rerolls those dice, at most twice; bank scores the hand. Five rounds. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Prepared entry points: main, reset, command.
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
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
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

