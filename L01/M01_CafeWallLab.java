// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// </editor-fold>

/// # L01.01 · CafeWallLab
///
/// **Uncover the hidden picture.** Run the program. You will see a grey area: the tile drawing
/// has been painted over.
///
/// **Your task:** In `paintWall` immediately below, swap the two lines inside `{ }`. Move each
/// whole line, including `(g);`. Keep both lines; you do not need to edit the rest of the program
/// yet.
///
/// **Result:** After saving and running again, you will see eight rows of black and white tiles.
/// The grey paint should stay behind them.

void paintWall(Graphics2D g) {
    paintBackground(g);
    paintTiles(g);
}

// <editor-fold defaultstate="expanded" desc="Execution order · F01">
/// Each of the two lines is a statement: one drawing step. `paintTiles(g);` draws the tiles.
/// `paintBackground(g);` paints the whole canvas grey.
///
/// Inside the braces, Java performs these steps from top to bottom. It works like painting on
/// paper: later paint covers earlier drawing. Today you change the order of the steps, not their
/// contents.
///
/// Imagine another picture: first a blue sheet, then a yellow star. What would you see if you
/// reversed those two steps?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `void` — This action does its work without returning a value to its caller.
/// - `paintWall` — The name of the action you are defining; the supplied drawing code calls it.
/// - `(Graphics2D g)` — The parentheses describe one supplied input: a drawing pen called g. Use this pen
///   without changing its type.
/// - `{` — The opening brace starts the action body; the matching closing brace ends it.
/// - `paintTiles(g);` — Call the supplied tile-painting action with pen g. Parentheses carry the input;
///   the semicolon ends the statement.
/// - `paintBackground(g);` — Call the action that paints the whole canvas grey. Its position in the
///   sequence matters.
///
/// ### What the prepared part already does
///
/// Lines beginning /// are words for you; Java does not execute them. The supplied main below starts the
/// program and opens the window. When the window needs a picture, draw calls your paintWall action.
/// paintBackground and paintTiles contain the ready-made painting; key handles the controls.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Once the picture appears">
/// Left/Right shifts the tiles, Up/Down changes the gaps, and Space shows a red ruler. Try to
/// convince a friend that the horizontal lines are tilted, then reveal the ruler.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Read the two actions from top to bottom.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The last paint covers earlier paint at the same place.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Move paintBackground(g) above paintTiles(g).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void paintBackground(Graphics2D g) {
    g.setColor(new Color(145, 145, 145));
    g.fillRect(0, 0, WIDTH, HEIGHT);
}

void paintTiles(Graphics2D g) {
    for (int row = 0; row < 8; row++) {
        int shift = row % 2 == 0 ? 0 : offset;
        for (int tile = -1; tile < 12; tile++) {
            g.setColor(Math.floorMod(tile, 2) == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(tile * 60 + shift, rowY(row), 60, 40);
        }
    }
}

final int WIDTH = 640, HEIGHT = 450;

int offset, mortar;
boolean guide;
void restart() {
    offset = 25;
    mortar = 4;
    guide = false;
}

void adjust(int shift, int width) {
    offset = Math.max(0, Math.min(60, offset + shift));
    mortar = Math.max(1, Math.min(14, mortar + width));
}

int rowY(int row) {
    return 30 + row * (40 + mortar);
}

void draw(Graphics2D g) {
    paintWall(g);
    if (guide) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, rowY(4) - mortar / 2, WIDTH, rowY(4) - mortar / 2);
    }
    g.setColor(new Color(255, 220, 90));
    g.fillRect(0, 0, WIDTH, 25);
    g.setColor(Color.BLACK);
    g.drawString("Offset: " + offset + "  Mortar: " + mortar + "   The horizontal edges remain straight.", 10, 18);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> adjust(-2, 0);
            case KeyEvent.VK_RIGHT -> adjust(2, 0);
            case KeyEvent.VK_UP -> adjust(0, 1);
            case KeyEvent.VK_DOWN -> adjust(0, -1);
            case KeyEvent.VK_SPACE -> guide = !guide;
        }
    }
}

void showWindow() {
    var frame = new JFrame("Caf\u00e9 Wall Lab");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: offset   Up / Down: mortar width   Space: red guide   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

/// Prepared entry points: main, restart, draw, showWindow.
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
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
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
/// - `Graphics2D` — The drawing tool supplied while a window or image is being painted.
/// - `JFrame` — A desktop window supplied by the JDK’s Swing library.
/// - `JPanel` — A window area that can display the game’s drawing or controls.
/// - `@Override` — Marks a supplied replacement for a library action, such as painting.
/// - `SwingUtilities.invokeLater` — Schedules window work for Swing’s event thread; the action runs
///   later.
// </editor-fold>
