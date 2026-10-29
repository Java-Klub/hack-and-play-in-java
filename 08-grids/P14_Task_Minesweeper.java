import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Úkol · Minesweeper
/// Lekce: 8.3 Ptej se okolních buněk — 03-neighbours.html
///
/// Běžná hra s minami i bodová prohlížecí lupa už fungují. Rozšiř lupu na místní rentgen a pomocí
/// získaných údajů naplánuj bezpečnou cestu.
///
/// **Tvoje mise:** S volbou Inspect musí kliknutí ukázat všechny miny v okolí 3×3 včetně středu, ale
/// žádnou mimo něj. Nové prohlédnutí nahrazuje staré. Prohlídka nesmí odkrývat bezpečná políčka, stavět
/// vlajky, zvyšovat postup ani prohrát kolo. Rohy se bezpečně oříznou; New board lupu smaže. Běžné
/// odkrývání i čísla min zůstanou.
///
/// Zapni Inspect a klikni: výchozí lupa nahlédne jen do tohoto políčka. Po rozšíření klikni vedle
/// podezřelé miny a uvidíš i diagonal neighbour (úhlopříčné sousedy). Pro běžné odkrývání Inspect
/// vypni; Flag mode a New board fungují dál.

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
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
    if (inside(row, col)) inspected[row][col] = mines[row][col];
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
