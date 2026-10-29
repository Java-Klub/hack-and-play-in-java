// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
// </editor-fold>

/// # 11.12 · MemoryPairs
///
/// Flip two cards and remember where their partners hide. An X-ray can help a beginner, but deciding
/// exactly what it may reveal changes the challenge.
///
///
/// **Your mission:** Turn the all-seeing X-ray into a partner finder. With one card selected, X-ray
/// should reveal only cards with that same face, alongside normally visible cards. With no selection it
/// reveals nothing extra. Toggle it off or play another valid card to hide the extra information. It
/// must never mark a pair, spend an attempt or alter the deck.
///
/// **Today you learn:** An array lookup turns a selected position into a value; comparing positions is
/// different from comparing the values stored there.
///
/// Select one card and toggle X-ray. Notice which private faces leak. Turn it off, then try it before
/// selecting any card in a fresh round.
int visible(int index) {
    if (matched[index] || index == first || index == second) {
        return cards[index];
    }
    if (xray) {
        return cards[index];
    }
    return 0;
}

// <editor-fold defaultstate="expanded" desc="Learn · F16">
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

// <editor-fold defaultstate="expanded" desc="Read this code">
/// A view can reveal information without changing the matching rules. Follow a card click and an X-ray
/// click separately: which one calls play, and which one only refreshes labels?
///
/// For separate cards [7, 3, 7, 5], selected position 2 contains value 7, so position 0 is its partner.
/// Position 2 and value 7 are not interchangeable. Predict the matching position if selection becomes
/// 1. No selection uses -1; that marker cannot be used as an array index. Preserve normal visibility
/// for matched and currently selected cards.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Trace the button to the rule deciding what a face may show, rather than changing card values.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// visible already handles ordinary visibility. Its xray branch currently reveals every cards[index];
/// first gives the selected position.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Permit the extra reveal only when first is valid and cards[index] equals cards[first]. Keep ordinary
/// visibility and the hidden zero marker.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Design a different fair assistance rule, such as showing only one partner after two failed attempts.
/// Explain what information it reveals and what it must leave unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] cards = new int[12];
boolean[] matched = new boolean[12];
int first = -1;
int second = -1;
boolean xray;
int attempts;
int pairs;
JButton[] buttons = new JButton[12];
JLabel status = new JLabel();

/// reset(): Restores the starting Memory Pairs round, clearing the state that belongs to the old
/// round.
void reset(long seed) {
    var values = new ArrayList<Integer>();
    for (int value = 1; value <= 6; value++) {
        values.add(value);
        values.add(value);
    }
    Collections.shuffle(values, new Random(seed));
    for (int i = 0; i < cards.length; i++) {
        cards[i] = values.get(i);
    }
    matched = new boolean[12];
    first = -1;
    second = -1;
    xray = false;
    attempts = 0;
    pairs = 0;
}

/// play(): Tracks two reveals, delays mismatches and permanently marks a matching pair.
boolean play(int index) {
    if (index < 0 || index >= cards.length || matched[index] || index == first || index == second || pairs == 6) {
        return false;
    }
    xray = false;
    if (second != -1) {
        first = -1;
        second = -1;
    }
    if (first == -1) {
        first = index;
    } else {
        second = index;
        attempts++;
        if (cards[first] == cards[second]) {
            matched[first] = true;
            matched[second] = true;
            pairs++;
            first = -1;
            second = -1;
        }
    }
    return true;
}

/// refresh(): Copies the current model values into button labels, enabled states and status text.
void refresh() {
    for (int i = 0; i < cards.length; i++) {
        int value = visible(i);
        buttons[i].setText(value == 0 ? "?" : Integer.toString(value));
        buttons[i].setEnabled(!matched[i] && pairs < 6);
    }
    String guidance = pairs == 6 ? "All pairs found!" : second != -1
            ? "Not a match: click another covered card." : first != -1
            ? "Choose a second card." : "Find matching pairs.";
    status.setText("<html>" + guidance + "<br>X-ray: " + (xray ? "on" : "off") + ". Pairs: " + pairs + "/6. Attempts: " + attempts + "</html>");
}

/// show(): Supplied later topic: Swing button callbacks invoke the model and refresh its visible
/// state.
void show() {
    var frame = new JFrame("Memory Pairs");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(3, 4, 4, 4));
    for (int i = 0; i < cards.length; i++) {
        int index = i;
        var button = new JButton();
        buttons[i] = button;
        button.addActionListener(event -> {
            play(index);
            refresh();
        });
        grid.add(button);
    }
    var restart = new JButton("New cards");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        refresh();
    });
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    var peek = new JButton("X-ray on / off");
    peek.addActionListener(event -> {
        xray = !xray;
        refresh();
    });
    var actions = new JPanel();
    actions.add(peek);
    actions.add(restart);
    frame.add(actions, BorderLayout.SOUTH);
    refresh();
    frame.setSize(470, 350);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

/// main(): Starts the model, then schedules the supplied window on Swing’s event thread.
void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
/// ## An unfamiliar piece of Java?
///
/// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
///
/// - `///` — Words for the reader. Java skips these comment lines when running.
/// - `import` — Makes library names available in this file; it does not run the library.
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
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
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
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
