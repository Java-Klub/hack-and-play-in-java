import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Solved task · Minesweeper
/// Lesson: 8.3 Ask the neighbouring cells — 03-neighbours.html
///
/// Original assignment (this version is complete):
///
/// The normal mine game and a point inspection lens already work. Expand that lens into a local X-ray
/// tool, then use what it reveals to plan a safe route.
///
/// **Your mission:** With Inspect selected, a click must show every mine in the clicked 3×3
/// neighbourhood, including the centre, but none outside it. A new inspection replaces the old one.
/// Inspection must not reveal safe cells, place flags, advance progress or lose the round. Corners must
/// clip safely; New board clears the lens. Ordinary reveal and mine clues stay unchanged.
///
/// Select Inspect and click: the starter peeks at only that square. After expanding the lens, inspect
/// beside a suspected mine to see diagonal as well as straight neighbours. Turn Inspect off to reveal
/// safe cells normally; Flag mode and New board still work.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
final int SIZE = 6;
final int MINE_COUNT = 6;
boolean[][] mines = new boolean[SIZE][SIZE];
boolean[][] revealed = new boolean[SIZE][SIZE];
boolean[][] flags = new boolean[SIZE][SIZE];
boolean lost;
boolean[][] inspected = new boolean[SIZE][SIZE];
int opened;
JButton[][] buttons = new JButton[SIZE][SIZE];
JLabel status = new JLabel();
JToggleButton flagMode = new JToggleButton("Flag mode");
JToggleButton inspectMode = new JToggleButton("Inspect");

int adjacent(int row, int col) {
    int count = 0;
    for (int r = row - 1; r <= row + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (inside(r, c) && (r != row || c != col) && mines[r][c]) count++;
        }
    }
    return count;
}

boolean inside(int row, int col) {
    return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
}

void reset(long seed) {
    mines = new boolean[SIZE][SIZE];
    revealed = new boolean[SIZE][SIZE];
    flags = new boolean[SIZE][SIZE];
    inspected = new boolean[SIZE][SIZE];
    var random = new Random(seed);
    int placed = 0;
    while (placed < MINE_COUNT) {
        int row = random.nextInt(SIZE);
        int col = random.nextInt(SIZE);
        if (!mines[row][col]) {
            mines[row][col] = true;
            placed++;
        }
    }
    lost = false;
    opened = 0;
}

boolean won() {
    return !lost && opened == SIZE * SIZE - MINE_COUNT;
}

boolean reveal(int row, int col) {
    if (!inside(row, col) || revealed[row][col] || flags[row][col] || lost || won()) {
        return false;
    }
    revealed[row][col] = true;
    if (mines[row][col]) {
        lost = true;
    } else {
        opened++;
        if (adjacent(row, col) == 0) {
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    reveal(r, c);
                }
            }
        }
    }
    return true;
}

void inspect(int row, int col) {
    inspected = new boolean[SIZE][SIZE];
    if (!inside(row, col)) return;
    for (int r = row - 1; r <= row + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (inside(r, c)) inspected[r][c] = mines[r][c];
        }
    }
}

boolean toggleFlag(int row, int col) {
    if (!inside(row, col) || revealed[row][col] || lost || won()) {
        return false;
    }
    flags[row][col] = !flags[row][col];
    return true;
}

void refresh() {
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            String text = inspected[r][c] ? "*" : flags[r][c] ? "F" : "?";
            if (revealed[r][c] || (lost && mines[r][c])) {
                int count = adjacent(r, c);
                text = mines[r][c] ? "*" : count == 0 ? "" : Integer.toString(count);
            }
            buttons[r][c].setText(text);
            buttons[r][c].setEnabled(!revealed[r][c] && !lost && !won());
        }
    }
    status.setText(lost ? "Mine! Start a new board." : won() ? "All safe cells found!" : "Safe cells: " + opened + "/" + (SIZE * SIZE - MINE_COUNT) + ". First click can hit a mine.");
}

void show() {
    var frame = new JFrame("Minesweeper");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(SIZE, SIZE, 2, 2));
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                if (inspectMode.isSelected()) {
                    inspect(row, col);
                }
                else if (flagMode.isSelected()) {
                    toggleFlag(row, col);
                }
                else {
                    reveal(row, col);
                }
                refresh();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var restart = new JButton("New board");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        flagMode.setSelected(false);
        inspectMode.setSelected(false);
        refresh();
    });
    controls.add(inspectMode);
    controls.add(flagMode);
    controls.add(restart);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.setSize(520, 510);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Limit your lens to three uses per board and display the remaining charges. Can that smaller
/// advantage still help a partner finish a board?
// </editor-fold>
