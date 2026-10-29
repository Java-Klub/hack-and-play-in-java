import module java.base;
class M03_DotsAndBoxes {
    /// # L05.03 · DotsAndBoxes
    ///
    /// Take turns claiming lines between dots. Close a box to steal its point and take another turn.
    ///
    /// **Your mission:** Every box currently consults the same four edges. Repair box(int n) so scoring
    /// follows the box actually closed.
    ///
    /// **Today you learn:** Arrays: an int[] can describe a group of board positions. Translate one box
    /// number into the edge indices its caller needs.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L05/M03_DotsAndBoxes.java`
    /// - Check this mission: `java L05/Check.java M03_DotsAndBoxes.java`
    ///
    /// the play command, then take turns typing an unclaimed edge number from 1 to 12 and pressing
    /// Enter. After your repair: save the file, run the check command above, then play again.
    ///
    /// Need an explanation? Recall · F16 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    int[] box(int n) {
        int r = n / 2;
        int c = n % 2;
        return new int[]{r * 2 + c, (r + 1) * 2 + c, 6 + r * 3 + c, 6 + r * 3 + c + 1};
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F16">
    /// ### Arrays: the position matters
    ///
    /// An array keeps a fixed number of values of one element type. Square brackets select a position:
    /// the first index is zero, and `length` is the count. Reading one element does not alter the others.
    /// An index must exist before it is used; an empty array has no first element.
    ///
    /// ```java
    /// String[] tools = {"pen", "tape", "chalk"};
    /// int chosen = 1;
    /// IO.println(tools[chosen]);
    /// IO.println(tools.length);
    /// ```
    ///
    /// These statements belong inside a method. Index one selects `tape`; the count is three. What
    /// changes if `chosen` becomes two? Which index would be the first invalid one?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `int[]` — The result is an array of whole-number edge indices.
    /// - `box(int n)` — n is the box number supplied by the caller, starting at zero.
    /// - `new int[]` — Creates a separate array containing the values inside the following braces.
    /// - `return` — Passes the edge group back to command(); it does not claim the edges.
    ///
    /// ### What the prepared part already does
    ///
    /// display() draws numbered edges. command() claims one edge, asks box() about each unfinished box, and
    /// handles scores and extra turns; those rules are already supplied.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="For this game">
    /// ### Edges are values too
    ///
    /// The returned int array lists four edge indices; it does not list the four corners. Draw the
    /// displayed board and label its edges with internal indices, one less than the printed numbers. For
    /// a chosen box, find its top, bottom, left and right edges separately. A `new int[]{...}` groups
    /// those four numbers into a result. Predict one box away from the top-left before writing a general
    /// expression for n.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// A box number gives row n / 2 and column n % 2.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Horizontal edges occupy slots 0-5; vertical edges start at 6.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Return top, bottom, left and right in one new int[].
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
    boolean[] edges;
    int[] owners;
    int[] scores;
    int player;
    int moves;
    String outcome;

    /// reset(): Restores the starting Dots and Boxes round, clearing the state that belongs to the old
    /// round.
    void reset() {
        edges = new boolean[12];
        owners = new int[4];
        scores = new int[3];
        player = 1;
        moves = 0;
        outcome = "";
    }

    /// command(): Claims one edge, awards newly closed boxes and retains the turn after a score.
    void command(String input) {
        require(outcome.isEmpty());
        int edge = Integer.parseInt(input) - 1;
        require(edge >= 0 && edge < 12 && !edges[edge]);
        edges[edge] = true;
        moves++;
        int gained = 0;
        for (int b = 0; b < 4; b++) {
            if (owners[b] != 0) {
                continue;
            }
            boolean complete = true;
            for (int e : box(b)) {
                complete &= edges[e];
            }
            if (complete) {
                owners[b] = player;
                scores[player]++;
                gained++;
            }
        }
        if (moves == 12) {
            outcome = scores[1] == scores[2] ? "Draw" : "Player " + (scores[1] > scores[2] ? 1 : 2) + " wins";
        }
        if (gained == 0) {
            player = 3 - player;
        }
    }

    String edge(int n) {
        return edges[n] ? "##" : String.format("%02d", n + 1);
    }

    /// display(): Presents the current board and the legal input choices in the console.
    void display() {
        IO.println("Player " + player + "; scores " + scores[1] + ":" + scores[2]);
        for (int r = 0; r < 3; r++) {
            IO.println("+--" + edge(r * 2) + "--+--" + edge(r * 2 + 1) + "--+");
            if (r < 2) {
                IO.println(edge(6 + r * 3) + "  " + owners[r * 2] + "   " + edge(7 + r * 3) + "  " + owners[r * 2 + 1] + "   " + edge(8 + r * 3));
            }
        }
    }

    /// main(): Starts the real game loop: display, read a command, validate, replay or quit.
    /// Prepared console flow: type a command and press Enter. A valid command redraws the board.
    /// Rejected input leaves the display alone; after a round, Enter or /r starts again and /q quits.
    void main() {
        reset();
        IO.println("Dots and Boxes");
        IO.println("Take turns claiming numbered edges. Close a box to score and play again; most boxes wins.");
        IO.println("Enter an unclaimed edge number 1-12. Completing a box grants another turn. /r restarts; /q quits.");
        IO.println("Example: enter 1 to claim edge 01. ## means an edge is already taken.");
        display();
        while (true) {
            String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
            if (input == null) {
                return;
            }
            input = input.strip();
            if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
                return;
            }
            if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                    || (!outcome.isEmpty() && input.isEmpty())) {
                reset();
                display();
                continue;
            }
            if (!outcome.isEmpty()) {
                IO.println("That round is over. Press Enter to play again, or /q to quit.");
                continue;
            }
            try {
                command(input);
                display();
                if (!outcome.isEmpty()) {
                    IO.println(outcome);
                }
            } catch (IllegalArgumentException invalid) {
                IO.println("Invalid move. " + "Enter an unclaimed edge number 1-12. Completing a box grants another turn. /r restarts; /q quits.");
            }
        }
    }

    /// require(): Rejects an illegal command with an exception handled by the supplied input loop.
    void require(boolean valid) {
        if (!valid) {
            throw new IllegalArgumentException("Invalid move or command.");
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
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `==` — Compares primitive values. For text contents, use equals instead.
    /// - `!=` — Checks that two values differ.
    /// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
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
    /// - `for` — Repeats a block over a range or a sequence of values.
    /// - `while` — Keeps repeating its body while its condition stays true.
    /// - `null` — No object is present. IO.readln returns null when the input stream ends.
    /// - `try` — A block whose possible failures can be handled by the following catch.
    /// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
    /// - `throw` — Reports a failure by creating or passing an exception.
    /// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
    // </editor-fold>

}
