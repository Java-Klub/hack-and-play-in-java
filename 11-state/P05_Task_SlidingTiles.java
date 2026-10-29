import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Úkol · SlidingTiles
/// Lekce: 11.1 Objekt si pamatuje změnu — 01-state.html
///
/// Posouvej očíslované dílky kolem jedné mezery do správného pořadí. Dejte si s kamarádem stejný hlavolam
/// a porovnejte počet tahů.
///
/// **Tvoje mise:** Nový hlavolam už má započítané tahy použité při míchání. Oprav závěrečné nulování
/// počítadla a zachovej způsob tvorby řešitelných desek.
///
/// Klikni na očíslovaný dílek hned vedle mezery, aby se přesunul na prázdné
/// místo.

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
    int moves = 0;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
