/// # L03.10 · ShutTheBox
///
/// Roll two dice and close open numbered boxes whose sum matches their total. Try to leave a useful
/// selection for later rolls and close every box before no legal choice remains.
///
/// **Your mission:** The target includes only the first die, although both faces are printed. Repair
/// diceTotal so both received die values contribute.
///
/// **Today you learn:** Reusable calculation: two parameters let one method work for any pair of dice.
/// For example, calls with 1 and 1 or with 6 and 6 should follow the same rule without copying its body.
///
/// From the course folder containing README.md:
/// - Play: `java L03/M10_ShutTheBox.java`
/// - Check this mission: `java L03/Check.java M10_ShutTheBox.java`
///
/// Run the play command above, compare the two printed Dice values with the rolled target, then
/// type distinct open numbers that should add up to that target. After your repair: save the file,
/// run the check command above, then play again.
///
/// Need an explanation? Recall · F13 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int diceTotal(int first, int second) {
    return first + second;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F13">
/// A method packages an operation under one name. Parameters are local names for the information
/// received on each call; arguments are the actual supplied values. `return` gives one result back to
/// the caller.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Predict both printed values: seven and five. The comma separates the parameters or arguments, and
/// their order decides which value each name receives. The names describe roles; they do not fetch
/// information by magic. This method calculates without printing or changing the supplied program
/// state. When repairing a calculation, try a second pair of arguments so an accidental fixed answer
/// cannot hide.
///
/// Now predict remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int diceTotal(int first, int second)` — The method returns a whole number and receives two
///   separately named whole numbers.
/// - `first` — The first die argument supplied to this call.
/// - `second` — The second die argument, available even though the incomplete body ignores it.
/// - `,` — Separate the two parameter declarations; it does not combine their values.
/// - `return first;` — This currently sends back only one input instead of a result using both.
///
/// ### What the prepared part already does
///
/// roll generates and prints two actual dice, then calls diceTotal. possible checks whether open boxes
/// can meet the target, command closes selected boxes, and display shows the remaining choices.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Use different values such as 2 and 5 to expose the ignored parameter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Both parameters belong to this particular call.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return first + second.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[] open;
int target;
String outcome;
Random random = new Random();

void reset() {
    open = new boolean[10];
    Arrays.fill(open, true);
    open[0] = false;
    outcome = "";
    roll();
}

int sum() {
    int n = 0;
    for (int i = 1; i <= 9; i++) {
        if (open[i]) {
            n += i;
        }
    }
    return n;
}

boolean possible() {
    for (int bits = 1; bits < 512; bits++) {
        int total = 0;
        boolean legal = true;
        for (int i = 1; i <= 9; i++) {
            if ((bits & (1 << (i - 1))) != 0) {
                total += i;
                legal &= open[i];
            }
        }
        if (legal && total == target) {
            return true;
        }
    }
    return false;
}

void roll() {
    firstDie = random.nextInt(1, 7);
    secondDie = random.nextInt(1, 7);
    target = diceTotal(firstDie, secondDie);
    if (!possible()) {
        outcome = "No legal closure. Final open sum: " + sum();
    }
}

void command(String input) {
    require(outcome.isEmpty());
    var chosen = new HashSet<Integer>();
    int total = 0;
    for (String p : input.split("\\s+")) {
        int n = Integer.parseInt(p);
        require(n >= 1 && n <= 9 && open[n] && chosen.add(n));
        total += n;
    }
    require(total == target);
    for (int n : chosen) {
        open[n] = false;
    }
    if (sum() == 0) {
        outcome = "Box shut: you win!";
    } else {
        roll();
    }
}

void display() {
    String s = "Open: ";
    for (int i = 1; i <= 9; i++) {
        if (open[i]) {
            s += i + " ";
        }
    }
    IO.println("Dice: " + firstDie + " + " + secondDie);
    IO.println(s + "; rolled " + target);
}

void main() {
    reset();
    IO.println("Shut the Box");
    IO.println("Enter distinct open numbers whose sum matches two dice. No legal subset ends the round. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
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
            IO.println("Invalid move or command. Enter distinct open numbers whose sum matches two dice. No legal subset ends the round. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

int firstDie;
int secondDie;

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
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
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
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

