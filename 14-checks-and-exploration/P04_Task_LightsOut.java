import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;

/// # Solved task · LightsOut
/// Lesson: 14.1 Choose examples that catch mistakes — 01-checks.html
///
/// Original assignment (this version is complete):
///
/// Turn every light off by clicking crosses of cells. Become the tester who catches convincing but unfair
/// copies of the toggle rule.
///
/// **Your mission:** Design at most two experiments that catch all six counterfeit click rules
/// described below. Each experiment starts all-off, makes two clicks, and predicts the final number of
/// lit cells. Both clicks may target the same cell. Your evidence must also accept the real cross rule.
/// Derive each expectation before running it; the game itself already works.
///
/// Play the ordinary cross-toggle game. Then compare Check’s surviving counterfeit with your two-click
/// experiment on an all-off sketch of the board.

record CrossExample(int firstRow, int firstColumn, int secondRow, int secondColumn, int expected) {
}

CrossExample[] learnerChecks() {
    return new CrossExample[]{new CrossExample(2, 2, 2, 2, 0), new CrossExample(0, 1, 0, 0, 3)};
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Six counterfeit rules to investigate
///
/// Each counterfeit keeps the five-by-five board but changes one rule:
///
/// - Only the pressed cell changes.
/// - Side neighbours change, but the pressed cell does not.
/// - The pressed cell and diagonal neighbours change instead of side neighbours.
/// - A cross continues across a border to the opposite side of the board.
/// - Affected cells are set on, even if they were already on.
/// - The second click is ignored entirely.
///
/// A useful experiment gives a different final count for the real rule and at least one counterfeit.
/// The same experiment may expose several. Check reports a surviving rule by its description; you do
/// not need to read the check runner.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int SIZE = 5;
boolean[][] lights = new boolean[SIZE][SIZE];
int moves;
JButton[][] buttons = new JButton[SIZE][SIZE];
JLabel status = new JLabel();

void flip(int row, int col) {
    int[][] offsets = {{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    for (var offset : offsets) {
        int r = row + offset[0];
        int c = col + offset[1];
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
            lights[r][c] = !lights[r][c];
        }
    }
}

boolean solved() {
    for (var row : lights) {
        for (boolean light : row) {
            if (light) {
                return false;
            }
        }
    }
    return true;
}

void reset(long seed) {
    lights = new boolean[SIZE][SIZE];
    var random = new Random(seed);
    for (int i = 0; i < 40; i++) {
        flip(random.nextInt(SIZE), random.nextInt(SIZE));
    }
    if (solved()) {
        flip(2, 2);
    }
    moves = 0;
}

boolean play(int row, int col) {
    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || solved()) {
        return false;
    }
    flip(row, col);
    moves++;
    return true;
}

void refresh() {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            buttons[r][c].setText(lights[r][c] ? "ON" : "off");
            buttons[r][c].setBackground(lights[r][c] ? Color.YELLOW : Color.DARK_GRAY);
            buttons[r][c].setForeground(lights[r][c] ? Color.BLACK : Color.WHITE);
            buttons[r][c].setOpaque(true);
        }
    }
    status.setText((solved() ? "All lights out! " : "Turn every light off. ") + "Moves: " + moves);
}

void show() {
    var frame = new JFrame("Lights Out");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(SIZE, SIZE, 3, 3));
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                play(row, col);
                refresh();
            });
            grid.add(button);
        }
    }
    var restart = new JButton("New puzzle");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        refresh();
    });
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(restart, BorderLayout.SOUTH);
    refresh();
    frame.setSize(440, 480);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    verifyExamples();
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}

void verifyExamples() {
    boolean[][] saved = lights;
    try {
        for (var example : learnerChecks()) {
            int[] coordinates = {example.firstRow(), example.firstColumn(), example.secondRow(), example.secondColumn()};
            for (int coordinate : coordinates) {
                if (coordinate < 0 || coordinate >= SIZE) {
                    throw new IllegalArgumentException("Cross examples must use valid game cells 0..4");
                }
            }
            lights = new boolean[SIZE][SIZE];
            flip(example.firstRow(), example.firstColumn());
            flip(example.secondRow(), example.secondColumn());
            int count = 0;
            for (var row : lights) {
                for (boolean on : row) {
                    if (on) count++;
                }
            }
            if (count != example.expected()) {
                throw new IllegalStateException("Two-click example: expected " + example.expected() + " lit cells, actual " + count);
            }
        }
    } finally {
        lights = saved;
    }
}
// </editor-fold>
