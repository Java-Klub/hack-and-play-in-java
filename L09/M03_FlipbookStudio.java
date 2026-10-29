// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
// </editor-fold>

/// # L09.03 · FlipbookStudio
///
/// Draw several tiny animation frames, then watch them flip into motion. Undo should let you experiment
/// boldly and peel back the latest mark first.
///
/// **Your mission:** Undo currently restores the oldest edit rather than the newest. Repair which
/// recorded change is removed, including an empty history.
///
/// **Today you learn:** A mutable List can act as a stack: changes enter in time order and are undone in
/// reverse time order. Each Edit stores enough old data to restore one pixel.
///
/// Run the program, then paint two different cells and press Z to undo the latest one;
/// right-click erases, 1–4 switches frames, Space plays the animation and R restarts.
boolean undo() {
    if (history.isEmpty()) return false;
    Edit edit = history.removeFirst();
    frames[edit.frame()][edit.y()][edit.x()] = edit.oldColour();
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F22">
/// ### List: a sequence you can change
///
/// A `List<String>` holds text in order. `new ArrayList<>()` makes a mutable list: its contents can
/// change without replacing the list itself.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// The output is `apple`, then `2`. `add(value)` appends one item; `get(index)` reads without
/// removing it. Indexes start at zero. `isEmpty()` checks for no items. `removeLast()` removes and
/// returns the final item; call it only on a nonempty list. A list made directly by `List.of(...)`
/// cannot be changed, but `new ArrayList<>(List.of(...))` can.
///
/// Predict: after `snacks.removeLast()`, what are its size and first item?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `history.isEmpty()` — Checks whether there is any edit to undo before removing one.
/// - `history.removeFirst()` — Returns and removes the earliest stored Edit; ask whether Undo should use
///   that end.
/// - `edit.frame()` — Reads a record component that identifies the frame to restore.
/// - `frames[edit.frame()][edit.y()][edit.x()]` — Finds a frame, then a row, then the pixel within that
///   row.
///
/// ### What the prepared part already does
///
/// paintCell records each change for undo; draw shows the chosen frame and animation preview.
/// Left/right mouse draws/erases, 1–4 selects a frame, Space plays and R restarts.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Successive edits go to the end of history.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Undo needs the last element and must remove that same element.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Restore the recorded frame/x/y/colour, not the currently selected frame.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 430;

int[][][] frames;
int edited, played, paintColour;
boolean playing, drawing;
String message;
/// Supplied restart: restore the initial model before the UI uses it.
void restart() {
    frames = new int[4][16][16];
    history.clear();
    edited = played = 0;
    playing = drawing = false;
    message = "Draw four frames; Space previews them";
}

void paintCell(int x, int y, int value) {
    if (x >= 0 && y >= 0 && x < 16 && y < 16) {
        history.add(new Edit(edited, x, y, frames[edited][y][x]));
        frames[edited][y][x] = value;
    }
}

/// Supplied bounded update: one call means one model step; the active repair is described above.
void step() {
    if (playing) {
        played = (played + 1) % 4;
    }
}


void paintAt(int x, int y) {
    if (x >= 25 && x < 281 && y >= 55 && y < 311) {
        paintCell((x - 25) / 16, (y - 55) / 16, paintColour);
    }
}

/// Supplied drawing: reads model values; repainting is not another turn.
void draw(Graphics2D g) {
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
            g.setColor(frames[edited][y][x] == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(25 + x * 16, 55 + y * 16, 16, 16);
            g.setColor(new Color(205, 205, 205));
            g.drawRect(25 + x * 16, 55 + y * 16, 16, 16);
        }
    }
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
            g.setColor(frames[played][y][x] == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(350 + x * 12, 75 + y * 12, 12, 12);
        }
    }
    g.setColor(Color.DARK_GRAY);
    g.drawRect(350, 75, 192, 192);
    g.drawString("Editing frame " + (edited + 1), 25, 35);
    g.drawString("Preview frame " + (played + 1) + (playing ? " - playing" : " - paused"), 350, 45);
    for (int i = 0; i < 4; i++) {
        g.setColor(edited == i ? Color.ORANGE : Color.LIGHT_GRAY);
        g.fillRect(25 + i * 65, 335, 55, 32);
        g.setColor(Color.BLACK);
        g.drawString("" + (i + 1), 48 + i * 65, 356);
    }
    g.drawString(message, 20, 405);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
/// Supplied restart: restore the initial model before the UI uses it.
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
        edited = code - KeyEvent.VK_1;
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_Z -> undo();
            case KeyEvent.VK_SPACE -> playing = !playing;
        }
    }
}

void press(int x, int y, int button) {
    if (x >= 25 && x < 285 && y >= 335 && y < 367) {
        edited = (x - 25) / 65;
    }
    else {
        drawing = true;
        paintColour = button == MouseEvent.BUTTON3 ? 0 : 1;
        paintAt(x, y);
    }
}

void drag(int x, int y) {
    if (drawing) {
        paintAt(x, y);
    }
}

void release(int x, int y) {
    drawing = false;
}

/// Supplied Swing UI: create it on the EDT; timer cleanup stays with the window.
void showWindow() {
    var frame = new JFrame("Flipbook Studio");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/// Supplied drawing: reads model values; repainting is not another turn.
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    var timer = new Timer(180, e -> {
/// Supplied bounded update: one call means one model step; the active repair is described above.
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Draw: left mouse   Erase: right mouse   1-4: frame   Space: play   Z: undo<br>R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

record Edit(int frame, int x, int y, int oldColour) {}
List<Edit> history = new ArrayList<>();

void main() {
/// Supplied restart: restore the initial model before the UI uses it.
    restart();
    SwingUtilities.invokeLater(this::showWindow);
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `final` — The named value cannot be assigned another value afterwards. A referenced mutable object
///   can still change.
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
/// - `switch` — Selects one case from a set of alternatives. It can also produce a result.
/// - `case` — One alternative of a switch; the arrow points to what it does or returns.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `::` — A method reference: supplies an action without calling it yet.
/// - `this` — The current game object: this particular run and its stored state.
/// - `public` — Makes this declaration accessible to other code.
/// - `protected` — A visibility keyword used by supplied library callbacks, such as paintComponent.
/// - `super` — In super.paintComponent(g), asks the supplied parent implementation to paint its standard
///   background first.
/// - `record` — A small named bundle of values. Its component accessors use the component names with ().
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
