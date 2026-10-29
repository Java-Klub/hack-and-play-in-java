import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;

/// # Úkol · LightsOut
/// Lekce: 14.1 Vyber příklady, které odhalí chyby — 01-checks.html
///
/// Zhasni všechna světla klikáním na kříže políček. Staň se testerem, který odhalí přesvědčivé, ale nefér
/// kopie pravidla přepínání.
///
/// **Tvoje mise:** Navrhni nejvýše dva experimenty, které odhalí všech šest padělaných pravidel
/// popsaných níže. Každý začíná zhasnutý, provede dva kliky a předpoví konečný počet svítících buněk.
/// Oba kliky mohou mířit na stejné pole. Důkazy musí zároveň přijmout skutečné křížové pravidlo.
/// Očekávání odvoď před spuštěním; hra už funguje.
///
/// Zahraj běžné křížové přepínání. Potom porovnej padělek přeživší Check se svým dvouklikovým
/// experimentem na nakreslené zhasnuté desce.

record CrossExample(int firstRow, int firstColumn, int secondRow, int secondColumn, int expected) {
}

CrossExample[] learnerChecks() {
    return new CrossExample[]{new CrossExample(2, 2, 2, 2, 0)};
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Šest padělaných pravidel k prozkoumání
///
/// Každý padělek zachová desku 5×5, ale změní jedno pravidlo:
///
/// - Mění se jen stisknuté pole.
/// - Mění se sousedé přes hranu, ale stisknuté pole ne.
/// - Mění se stisknuté pole a diagonální sousedé místo sousedů přes hranu.
/// - Kříž pokračuje přes okraj na protější stranu desky.
/// - Zasažená pole se vždy rozsvítí, i když už svítila.
/// - Druhý klik se úplně vynechá.
///
/// Užitečný experiment dá jiný konečný počet pro skutečné pravidlo a alespoň jeden padělek. Stejný
/// experiment jich může odhalit několik. Check pojmenuje přeživší pravidlo tímto popisem; není třeba
/// číst spouštěč kontrol.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
