// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
// </editor-fold>

/// # L02.09 · SonarTreasureHunt
///
/// Search an 8×8 sea for three hidden treasures with only eight probes. Each miss reports how far the
/// nearest remaining treasure was, so compare clues before spending another probe.
///
/// **Your mission:** The program accepts a probe one row or column beyond the map. Repair insideSea so
/// only indices 0 through 7 belong to the internal sea.
///
/// **Today you learn:** Boolean ranges: several comparisons can describe one rectangular area. Both the
/// row and the column must stay within their own lower and upper bounds; eight positions starting at zero
/// end at seven.
///
/// From the course folder containing README.md:
/// - Play: `java L02/M09_SonarTreasureHunt.java`
/// - Check this mission: `java L02/Check.java M09_SonarTreasureHunt.java`
///
/// The play command opens the sea. Click a square to probe it. Try the two outside-map buttons:
/// neither should spend a probe. After fixing both bounds, New sea starts a treasure hunt. After
/// your repair: save the file, run the check command above, then play again.
///
/// Need an explanation? Recall · F06 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
boolean insideSea(int row, int column) {
    return row >= 0 && row < 8 && column >= 0 && column < 8;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F06">
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
/// - `insideSea(int row, int column)` — Receive internal row and column indices, already converted from
///   the player’s 1–8 coordinates.
/// - `row >= 0` — The row is at least zero; >= includes equality.
/// - `row <= 8` — The current upper comparison includes eight itself, which is outside an eight-position
///   zero-based range.
/// - `column <= 8` — The column has its own upper bound; fixing only the row would leave another escape.
/// - `&&` — Logical AND: every connected comparison must be true for the complete expression to be true.
/// - `return` — Return the combined decision; this rule does not spend a probe by itself.
///
/// ### What the prepared part already does
///
/// command reads player coordinates, subtracts one from them and calls insideSea before recording a
/// probe. distance computes a clue, and the supplied history keeps each old clue tied to its original
/// moment.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// Click a sea square; a number marks its distance to the nearest treasure that was still
/// hidden at that moment. Count horizontal plus vertical steps, not a diagonal shortcut: one row up
/// and two columns right is distance three. The clue log keeps each earlier measurement, so a number
/// does not change after another treasure is found. FOUND marks a discovery; after eight probes,
/// X reveals the treasures you missed. New sea starts another round.
///
/// The two Try outside buttons deliberately request row nine or column nine. Watch the probe counter:
/// the broken guard accepts them, but a correct guard refuses them without spending a probe. These
/// buttons call the same command and insideSea rule as normal clicks. Their player coordinates start
/// at one; command subtracts one before calling your rule, whose valid indices run from zero to seven.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The printed 1–8 coordinates are converted to 0–7 before this rule.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// All four bounds must hold at the same time.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use row < 8 and column < 8; keep both >= 0 tests.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
Set<Integer> treasures;
Set<Integer> probed;
List<String> history;
int probes;
String outcome;

void reset() {
    var cells = new ArrayList<Integer>();
    for (int i = 0; i < 64; i++) {
        cells.add(i);
    }
    Collections.shuffle(cells);
    treasures = new HashSet<>(cells.subList(0, 3));
    probed = new HashSet<>();
    history = new ArrayList<>();
    probes = 0;
    outcome = "";
}

int distance(int cell) {
    int best = 99;
    for (int t : treasures) {
        best = Math.min(best, Math.abs(t / 8 - cell / 8) + Math.abs(t % 8 - cell % 8));
    }
    return best;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(insideSea(r, c) && !probed.contains(r * 8 + c));
    int cell = r * 8 + c;
    probed.add(cell);
    probes++;
    boolean hit = treasures.remove(cell);
    history.add("Probe " + probes + " at " + (r + 1) + "," + (c + 1) + ": " + (hit ? "treasure found" : "distance " + distance(cell)));
    if (treasures.isEmpty()) {
        outcome = "All treasures found!";
    } else {
        if (probes == 8) {
            outcome = "No probes left. Remaining treasures: " + treasures.stream().map((t)->(t / 8 + 1) + "," + (t % 8 + 1)).toList();
        }
    }
}

// Prepared Swing controls call the same command and insideSea rule as the tests.
JButton[][] seaButtons = new JButton[8][8];
JLabel scoreLabel = new JLabel();
JLabel feedbackLabel = new JLabel("Click any sea square to send your first probe.");
JTextArea clueLog = new JTextArea(5, 44);

void refreshSea() {
    scoreLabel.setText("Probes: " + probes + " / 8     Treasures left: " + treasures.size());
    clueLog.setText(String.join("\n", history));
    clueLog.setCaretPosition(clueLog.getDocument().getLength());
    for (int row = 0; row < 8; row++) {
        for (int column = 0; column < 8; column++) {
            int cell = row * 8 + column;
            var button = seaButtons[row][column];
            button.setEnabled(outcome.isEmpty() && !probed.contains(cell));
            if (!outcome.isEmpty() && treasures.contains(cell)) {
                button.setText("X");
                button.setToolTipText("A treasure you did not find.");
                button.setBackground(new Color(255, 210, 130));
            }
        }
    }
}

void probeAt(int row, int column) {
    if (!outcome.isEmpty()) {
        feedbackLabel.setText("Round finished. Click New sea to play again.");
        return;
    }
    boolean onMap = row >= 1 && row <= 8 && column >= 1 && column <= 8;
    int cell = (row - 1) * 8 + column - 1;
    if (onMap && probed.contains(cell)) {
        feedbackLabel.setText("You already explored that square; no probe spent.");
        return;
    }
    boolean hit = treasures.contains(cell);
    try {
        command(row + " " + column);
        if (onMap) {
            var button = seaButtons[row - 1][column - 1];
            button.setText(hit ? "FOUND" : "" + distance(cell));
            button.setBackground(hit ? new Color(255, 220, 100) : new Color(210, 230, 245));
            button.setToolTipText(history.getLast());
            feedbackLabel.setText(hit ? "Treasure found! Old distances still describe the earlier probes."
                : "Distance " + distance(cell) + ": count row steps + column steps to the nearest hidden treasure.");
        } else {
            feedbackLabel.setText("BUG: (" + row + ", " + column + ") is outside the sea, but spent a probe.");
        }
        if (!outcome.isEmpty()) feedbackLabel.setText(treasures.isEmpty()
            ? "You found all three treasures! Click New sea for another round."
            : "Eight probes used. X marks the missed treasures. Click New sea to retry.");
    } catch (IllegalArgumentException invalid) {
        feedbackLabel.setText(onMap ? "That probe was refused; no probe spent."
            : "Outside the sea: refused correctly; no probe spent.");
    }
    refreshSea();
}

void showWindow() {
    var frame = new JFrame("Sonar Treasure Hunt");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var board = new JPanel(new GridLayout(9, 9, 3, 3));
    board.add(new JLabel("Row / Col"));
    for (int column = 1; column <= 8; column++) {
        board.add(new JLabel("" + column, SwingConstants.CENTER));
    }
    for (int row = 0; row < 8; row++) {
        board.add(new JLabel("" + (row + 1), SwingConstants.CENTER));
        for (int column = 0; column < 8; column++) {
            int r = row + 1, c = column + 1;
            var button = new JButton("~");
            button.setPreferredSize(new Dimension(54, 40));
            button.setFont(button.getFont().deriveFont(12f));
            button.setToolTipText("Probe row " + r + ", column " + c);
            button.addActionListener(event -> probeAt(r, c));
            seaButtons[row][column] = button;
            board.add(button);
        }
    }
    var instructions = new JLabel("<html><b>Find 3 treasures with 8 probes.</b> Click a sea square.<br>"
        + "A number is the distance at that time: row steps + column steps.<br>"
        + "For example: one square up and two right is distance 3.</html>");
    var top = new JPanel(new BorderLayout(0, 8));
    top.add(instructions, BorderLayout.NORTH);
    top.add(scoreLabel, BorderLayout.SOUTH);
    var controls = new JPanel();
    var outsideRow = new JButton("Try outside: row 9, col 1");
    outsideRow.addActionListener(event -> probeAt(9, 1));
    var outsideColumn = new JButton("Try outside: row 1, col 9");
    outsideColumn.addActionListener(event -> probeAt(1, 9));
    var restart = new JButton("New sea");
    restart.addActionListener(event -> {
        reset();
        for (var row : seaButtons) for (var button : row) {
            button.setText("~");
            button.setToolTipText("Unexplored sea square");
            button.setBackground(null);
        }
        feedbackLabel.setText("New sea. Click a square; the treasures have moved.");
        refreshSea();
    });
    controls.add(outsideRow);
    controls.add(outsideColumn);
    controls.add(restart);
    clueLog.setEditable(false);
    var bottom = new JPanel(new BorderLayout(0, 8));
    bottom.add(feedbackLabel, BorderLayout.NORTH);
    bottom.add(new JScrollPane(clueLog), BorderLayout.CENTER);
    bottom.add(controls, BorderLayout.SOUTH);
    var content = new JPanel(new BorderLayout(8, 8));
    content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    content.add(top, BorderLayout.NORTH);
    content.add(board, BorderLayout.CENTER);
    content.add(bottom, BorderLayout.SOUTH);
    frame.add(content);
    refreshSea();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Sonar Treasure Hunt needs a graphical display. Run java L02/Check.java M09_SonarTreasureHunt.java to check insideSea.");
        return;
    }
    SwingUtilities.invokeLater(this::showWindow);
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Prepared entry points: main, reset, command, probeAt, showWindow.
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
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `%` — The remainder after division; useful for repeated positions or turns.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
///   available.
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
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Set` — Stores distinct values; the same value is present at most once.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>

