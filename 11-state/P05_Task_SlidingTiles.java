import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Solved task · SlidingTiles
/// Lesson: 11.1 An object remembers a change — 01-state.html
///
/// Original assignment (this version is complete):
///
/// Slide numbered tiles into order with one empty space. Challenge a friend on the same puzzle and
/// compare how many moves you need.
///
/// **Your mission:** A new puzzle starts with moves already counted during shuffling. Repair the final
/// counter reset without changing how solvable boards are created.
///
/// Click a numbered tile immediately beside the gap to slide it into the
/// empty space.

void reset(long seed) {
    for (int i = 0; i < 9; i++) {
        tiles[i] = (i + 1) % 9;
    }
    var random = new Random(seed);
    for (int step = 0; step < 100; step++) {
        var choices = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (canMove(i)) {
                choices.add(i);
            }
        }
        slide(choices.get(random.nextInt(choices.size())));
    }
    if (solved()) {
        slide(7);
    }
    moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[] tiles = new int[9];
int moves;
JButton[] buttons = new JButton[9];
JLabel status = new JLabel();

int gap() {
    for (int i = 0; i < tiles.length; i++) {
        if (tiles[i] == 0) {
            return i;
        }
    }
    throw new IllegalStateException("The board needs a gap.");
}

boolean canMove(int index) {
    if (index < 0 || index >= tiles.length) {
        return false;
    }
    int empty = gap();
    return Math.abs(index / 3 - empty / 3) + Math.abs(index % 3 - empty % 3) == 1;
}

void slide(int index) {
    int empty = gap();
    tiles[empty] = tiles[index];
    tiles[index] = 0;
}

boolean solved() {
    for (int i = 0; i < 8; i++) {
        if (tiles[i] != i + 1) {
            return false;
        }
    }
    return tiles[8] == 0;
}

boolean play(int index) {
    if (!canMove(index) || solved()) {
        return false;
    }
    slide(index);
    moves++;
    return true;
}

void refresh() {
    for (int i = 0; i < 9; i++) {
        buttons[i].setText(tiles[i] == 0 ? "" : Integer.toString(tiles[i]));
        buttons[i].setEnabled(tiles[i] != 0 && !solved());
    }
    status.setText((solved() ? "Solved! " : "Click a tile next to the gap. ") + "Moves: " + moves);
}

void show() {
    var frame = new JFrame("Sliding Tiles");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(3, 3, 4, 4));
    for (int i = 0; i < 9; i++) {
        int index = i;
        var button = new JButton();
        buttons[i] = button;
        button.addActionListener(event -> {
            play(index);
            refresh();
        });
        grid.add(button);
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
    frame.setSize(380, 390);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset(System.nanoTime());
    SwingUtilities.invokeLater(this::show);
}
// </editor-fold>
