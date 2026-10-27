/// # L04.13 · YatzyScorecard
///
/// Build a dice scorecard by deciding which dice to reroll and which category to spend. Each category can
/// be filled only once, so taking points now may give up a better opportunity later.
///
/// **Your mission:** After several categories are banked, the displayed total forgets earlier scores.
/// Repair the main sum in total so all filled categories contribute, unfilled entries are skipped and the
/// supplied upper-section bonus remains intact.
///
/// **Today you learn:** Running sum: preserve the previous total and incorporate each accepted score. Two
/// separate accumulators are needed here because sum counts every category, while upper counts only the
/// first six for a later bonus decision.
///
/// Use Run File, enter bank 13, then bank another open category showing nonzero points and compare the
/// printed total with both filled scores.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F12 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int total() {
    int sum = 0;
    int upper = 0;
    for (int i = 0; i < 13; i++) {
        if (scores[i] >= 0) {
            sum += scores[i];
            if (i < 6) {
                upper += scores[i];
            }
        }
    }
    return sum + (upper >= 63 ? 35 : 0);
}

// <editor-fold defaultstate="collapsed" desc="Recall · F12">
/// An accumulator remembers a growing result across passes. Its starting value belongs before the
/// loop; putting it inside would start the result again for every item. The remembered result can be
/// text, not only a number.
///
/// These example statements belong inside a called method.
///
/// ```java
/// String receipt = "Items:";
/// for (String item : List.of("tea", "cake")) {
///     receipt += " " + item;
/// }
/// ```
///
/// Predict receipt after each pass: `Items: tea`, then `Items: tea cake`. `+=` combines the old value
/// with the new contribution and stores the result. Plain `=` replaces the stored value.
/// `List.of(...)` supplies the fixed sequence of listed values; the colon visits one item per pass.
/// Trace the current item and accumulator in two columns to discover whether earlier work survives.
///
/// Now predict receipt when the supplied list contains only `"rice"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int sum = 0;` — Start the overall sum once before scanning the scorecard.
/// - `int upper = 0;` — Keep a separate running sum for the six upper categories; do not confuse it with
///   the overall sum.
/// - `for (int i = 0; i < 13; i++)` — Visit all thirteen category positions once, from zero through
///   twelve.
/// - `scores[i] >= 0` — A nonnegative value is a filled category, including a deliberate zero; -1 means
///   it is still open.
/// - `sum = scores[i];` — The current assignment overwrites the running sum with one category’s score.
/// - `upper += scores[i];` — This supplied accumulation preserves earlier upper-category points before
///   incorporating the current score.
/// - `upper >= 63 ? 35 : 0` — The prepared short choice yields a bonus of 35 when upper reaches 63,
///   otherwise zero; the bonus is added once after the loop.
///
/// ### What the prepared part already does
///
/// value already calculates the thirteen category scores from the five dice. command handles
/// rerolls and fills a chosen category only once; display shows each available score and calls
/// total. The supplied rules add a 35-point bonus when the first six categories reach 63.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// sum = scores[i] replaces the total on every filled category.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// sum must retain earlier scores; upper already accumulates correctly.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use sum += scores[i].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] names = {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Three-kind", "Four-kind", "Full-house", "Small-straight", "Large-straight", "Yatzy", "Chance"};
int[] dice;
int[] scores;
int rolls;
int turn;
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
    scores = new int[13];
    Arrays.fill(scores, -1);
    turn = 0;
    outcome = "";
    deal();
}

int value(int category) {
    int[] n = new int[7];
    int sum = 0;
    for (int d : dice) {
        n[d]++;
        sum += d;
    }
    if (category < 6) {
        return n[category + 1] * (category + 1);
    }
    int max = Arrays.stream(n).max().orElse(0);
    boolean two = false;
    boolean three = false;
    for (int v : n) {
        two |= v == 2;
        three |= v == 3;
    }
    int run = 0;
    int longest = 0;
    for (int i = 1; i <= 6; i++) {
        run = n[i] > 0 ? run + 1 : 0;
        longest = Math.max(longest, run);
    }
    return switch (category) {
    case 6 -> max >= 3 ? sum : 0;
    case 7 -> max >= 4 ? sum : 0;
    case 8 -> two && three ? 25 : 0;
    case 9 -> longest >= 4 ? 30 : 0;
    case 10 -> longest == 5 ? 40 : 0;
    case 11 -> max == 5 ? 50 : 0;
    default -> sum;
    };
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length >= 2);
    if (p[0].equalsIgnoreCase("bank")) {
        require(p.length == 2);
        int c = Integer.parseInt(p[1]) - 1;
        require(c >= 0 && c < 13 && scores[c] < 0);
        scores[c] = value(c);
        turn++;
        if (turn == 13) {
            outcome = "Scorecard complete. Total including upper bonus: " + total();
        } else {
            deal();
        }
    } else {
        require(p[0].equalsIgnoreCase("roll") && rolls < 3);
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
}

void display() {
    IO.println("Dice positions:  1  2  3  4  5");
    IO.println("Dice values:    " + Arrays.toString(dice));
    IO.println("Hand " + Math.min(13, turn + 1) + "/13; rerolls left " + (3 - rolls) + "; total " + total());
    for (int i = 0; i < 13; i++) {
        IO.println((i + 1) + " " + names[i] + ": " + (scores[i] >= 0 ? scores[i] + " points saved" : "open; this hand would score " + value(i)));
    }
    IO.println(rolls < 3 ? "Example: roll 4 5 changes only dice 4 and 5; the others stay. Or bank an open category." : "No rerolls left. Bank an open category to continue.");
    IO.println("Example: bank 2 saves the Twos score in category 2. Each category can be filled once. /help explains scoring.");
}

void main() {
    reset();
    IO.println("Yatzy Scorecard");
    IO.println("Each hand starts with five dice. Reroll selected positions up to twice, then save one open scoring category. /help explains scores; /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? (rolls < 3 ? "roll 4 5, bank category, /help, /r, /q: " : "bank category, /help, /r, /q: ") : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/help")) {
            IO.println("Categories 1-6: add only dice showing that number. Example: Twos adds all the 2s.");
            IO.println("Three-kind or Four-kind: at least three or four equal dice; score the sum of all five dice.");
            IO.println("Full-house: a triple and a pair, 25 points. Small-straight: four consecutive values, 30 points.");
            IO.println("Large-straight: five consecutive values, 40 points. Yatzy: five equal dice, 50 points.");
            IO.println("Chance: sum all five dice. A category whose condition is not met scores zero.");
            IO.println("If categories 1-6 total at least 63, add a 35-point bonus. Bank fills a category even when it scores zero.");
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Move rejected. Try roll 4 5 while rerolls remain, or bank 2 if category 2 is open. /help explains scoring.");
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
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
/// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
/// - `case` — One alternative of a switch; the arrow points to what it does or returns.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

