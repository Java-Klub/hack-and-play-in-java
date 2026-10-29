import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/// # Vyřešený úkol · Sudoku
/// Lekce: 8.2 Nejdřív zkontroluj okraj — 02-bounds.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Zastav zakázané číslo.** Vyber 1 a klikni na prázdné políčko bezprostředně vpravo od levé
/// horní jedničky. Výchozí program ji chybně přijme. V Sudoku 4×4 má být každé číslo 1–4 v řádku,
/// sloupci i bloku 2×2 jen jednou.
///
/// **Úkol:** Doplň ochranu zápisu v `place` níže. Zápis musí odmítnout porušení pravidel,
/// neplatné souřadnice či hodnoty, změnu pevného čísla i změnu po dokončení hry. K ověření volby
/// už máš připravenou `valid`; před čtením desky musí být souřadnice platné.
///
/// **Výsledek:** Další jednička se odmítne beze změny desky, správná dvojka do stejného políčka
/// projde. Pevná čísla zůstanou chráněná i při přímém volání `place`.

boolean place(int row, int col, int value) {
    if (complete()) return false;
    if (!valid(row, col, value)) return false;
    if (clues[row][col] != 0) return false;
    board[row][col] = value;
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Vyřeš opravenou hádanku, potom vymaž vybraná čísla v existující desce reset a připrav malou
/// hádanku kamarádovi. Zachovej shodu řádků, sloupců a bloků. Má hádanka zřejmý první tah?
/// Nepředpokládej, že každá upravená deska má jediné řešení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int[][] clues = {{1, 0, 0, 4}, {0, 4, 1, 0}, {2, 0, 4, 0}, {0, 3, 0, 1}};
int[][] board = new int[4][4];
JButton[][] buttons = new JButton[4][4];
JLabel status = new JLabel();
JComboBox<Integer> number = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4});

void reset() {
    for (int row = 0; row < 4; row++) board[row] = clues[row].clone();
}

boolean valid(int row, int col, int value) {
    if (row < 0 || row >= 4 || col < 0 || col >= 4 || value < 0 || value > 4) return false;
    if (value == 0) return true;
    for (int i = 0; i < 4; i++) {
        if (i != col && board[row][i] == value) return false;
        if (i != row && board[i][col] == value) return false;
    }
    int top = row / 2 * 2;
    int left = col / 2 * 2;
    for (int r = top; r < top + 2; r++) {
        for (int c = left; c < left + 2; c++) {
            if ((r != row || c != col) && board[r][c] == value) return false;
        }
    }
    return true;
}

boolean complete() {
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
            if (board[row][col] == 0 || !valid(row, col, board[row][col])) return false;
        }
    }
    return true;
}

void refresh() {
    boolean finished = complete();
    for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
            buttons[row][col].setText(board[row][col] == 0 ? "." : Integer.toString(board[row][col]));
            buttons[row][col].setEnabled(clues[row][col] == 0 && !finished);
        }
    }
    status.setText(finished ? "Solved! Restart for another round." : "Rows, columns and 2x2 boxes need 1, 2, 3, 4.");
}

void show() {
    var frame = new JFrame("Mini Sudoku");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var grid = new JPanel(new GridLayout(4, 4, 4, 4));
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            int row = r;
            int col = c;
            var button = new JButton();
            buttons[r][c] = button;
            button.addActionListener(event -> {
                boolean accepted = place(row, col, (Integer) number.getSelectedItem());
                refresh();
                if (!accepted) status.setText("Move rejected. Check the row, column and box; the starter guard may also need repair.");
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var restart = new JButton("Restart");
    restart.addActionListener(event -> {
        reset();
        refresh();
    });
    number.setSelectedItem(1);
    controls.add(new JLabel("Number (0 clears):"));
    controls.add(number);
    controls.add(restart);
    frame.add(status, BorderLayout.NORTH);
    frame.add(grid, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    refresh();
    frame.setSize(480, 390);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    reset();
    SwingUtilities.invokeLater(this::show);
}

// </editor-fold>
