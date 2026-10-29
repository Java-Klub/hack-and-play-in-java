import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;

/// # Vyřešený úkol · SonarTreasureHunt
/// Lekce: 4.4 Zastav neplatný tah včas — 04-guards.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Prohledej moře 8×8 a najdi tři ukryté poklady pouze osmi sondami. Každé minutí oznámí vzdálenost tehdy
/// nejbližšího zbývajícího pokladu, proto před dalším pokusem porovnávej stopy.
///
/// **Tvoje mise · Hlídej všechny čtyři okraje moře:** Sestav pravidlo hranic moře: validRow přijímá
/// vnitřní řádky 0..7, validColumn
/// sloupce 0..7 a insideSea přijme pole jen při platnosti obou. Ani jedno tlačítko mimo mapu nesmí
/// spotřebovat sondu.
///
/// Program otevře moře. Kliknutím prozkoumej políčko. Zkus obě tlačítka Try outside: ani jedno nemá
/// spotřebovat sondu. Po opravě obou mezí začne New sea nový lov pokladů.

boolean insideSea(int row, int column) {
    boolean validRow = row >= 0 && row < 8;
    boolean validColumn = column >= 0 && column < 8;
    return validRow && validColumn;
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Klikni na políčko moře. Číslo označí vzdálenost k nejbližšímu pokladu, který byl v tu chvíli
/// ještě schovaný. Počítej kroky po řádcích a sloupcích, nikoli šikmo: jeden krok nahoru a dva doprava
/// znamenají vzdálenost tři. Clue log (záznam měření) uchová původní výsledek; po nalezení jiného pokladu
/// se stará čísla nepřepočítají. FOUND znamená nález, po osmi sondách X odhalí přehlédnuté poklady.
/// New sea začne nové kolo.
///
/// Obě tlačítka Try outside záměrně žádají řádek nebo sloupec devět. Sleduj počítadlo: chybný guard
/// (kontrola mezí) je přijme, správný je odmítne bez spotřebování sondy. Tlačítka volají stejnou command
/// a insideSea jako běžná kliknutí. Souřadnice hráče začínají jedničkou; command od nich před voláním
/// tvého pravidla odečte jedničku. Platný index (pořadové číslo) tak sahá od nuly do sedmi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Po opravě hranic připrav kamarádovi plán tří sond na předem vybraných polích a porovnej vodítka se
/// třemi sousedními sondami. Potom změň jen připravený počet sond a sleduj změnu strategie. Velikost
/// mapy 8×8 zachovej.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
Set<Integer> treasures;
Set<Integer> probed;
List<String> history;
int probes;
String outcome;

void reset() {
    var cells = new ArrayList<Integer>();
    for (int i = 0; i < 64; i++) {
        cells.add(i);
    }
    Collections.shuffle(cells);
    treasures = new HashSet<>(cells.subList(0, 3));
    probed = new HashSet<>();
    history = new ArrayList<>();
    probes = 0;
    outcome = "";
}

int distance(int cell) {
    int best = 99;
    for (int t : treasures) {
        best = Math.min(best, Math.abs(t / 8 - cell / 8) + Math.abs(t % 8 - cell % 8));
    }
    return best;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(insideSea(r, c) && !probed.contains(r * 8 + c));
    int cell = r * 8 + c;
    probed.add(cell);
    probes++;
    boolean hit = treasures.remove(cell);
    history.add("Probe " + probes + " at " + (r + 1) + "," + (c + 1) + ": " + (hit ? "treasure found" : "distance " + distance(cell)));
    if (treasures.isEmpty()) {
        outcome = "All treasures found!";
    } else {
        if (probes == 8) {
            outcome = "No probes left. Remaining treasures: " + treasures.stream().map((t)->(t / 8 + 1) + "," + (t % 8 + 1)).toList();
        }
    }
}

// Připravené Swing controls (ovládací prvky) volají stejnou command a insideSea jako testy.
JButton[][] seaButtons = new JButton[8][8];
JLabel scoreLabel = new JLabel();
JLabel feedbackLabel = new JLabel("Click any sea square to send your first probe.");
JTextArea clueLog = new JTextArea(5, 44);

void refreshSea() {
    scoreLabel.setText("Probes: " + probes + " / 8     Treasures left: " + treasures.size());
    clueLog.setText(String.join("\n", history));
    clueLog.setCaretPosition(clueLog.getDocument().getLength());
    for (int row = 0; row < 8; row++) {
        for (int column = 0; column < 8; column++) {
            int cell = row * 8 + column;
            var button = seaButtons[row][column];
            button.setEnabled(outcome.isEmpty() && !probed.contains(cell));
            if (!outcome.isEmpty() && treasures.contains(cell)) {
                button.setText("X");
                button.setToolTipText("A treasure you did not find.");
                button.setBackground(new Color(255, 210, 130));
            }
        }
    }
}

void probeAt(int row, int column) {
    if (!outcome.isEmpty()) {
        feedbackLabel.setText("Round finished. Click New sea to play again.");
        return;
    }
    boolean onMap = row >= 1 && row <= 8 && column >= 1 && column <= 8;
    int cell = (row - 1) * 8 + column - 1;
    if (onMap && probed.contains(cell)) {
        feedbackLabel.setText("You already explored that square; no probe spent.");
        return;
    }
    boolean hit = treasures.contains(cell);
    try {
        command(row + " " + column);
        if (onMap) {
            var button = seaButtons[row - 1][column - 1];
            button.setText(hit ? "FOUND" : "" + distance(cell));
            button.setBackground(hit ? new Color(255, 220, 100) : new Color(210, 230, 245));
            button.setToolTipText(history.getLast());
            feedbackLabel.setText(hit ? "Treasure found! Old distances still describe the earlier probes."
                : "Distance " + distance(cell) + ": count row steps + column steps to the nearest hidden treasure.");
        } else {
            feedbackLabel.setText("BUG: (" + row + ", " + column + ") is outside the sea, but spent a probe.");
        }
        if (!outcome.isEmpty()) feedbackLabel.setText(treasures.isEmpty()
            ? "You found all three treasures! Click New sea for another round."
            : "Eight probes used. X marks the missed treasures. Click New sea to retry.");
    } catch (IllegalArgumentException invalid) {
        feedbackLabel.setText(onMap ? "That probe was refused; no probe spent."
            : "Outside the sea: refused correctly; no probe spent.");
    }
    refreshSea();
}

void showWindow() {
    var frame = new JFrame("Sonar Treasure Hunt");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var board = new JPanel(new GridLayout(9, 9, 3, 3));
    board.add(new JLabel("Row / Col"));
    for (int column = 1; column <= 8; column++) {
        board.add(new JLabel("" + column, SwingConstants.CENTER));
    }
    for (int row = 0; row < 8; row++) {
        board.add(new JLabel("" + (row + 1), SwingConstants.CENTER));
        for (int column = 0; column < 8; column++) {
            int r = row + 1, c = column + 1;
            var button = new JButton("~");
            button.setPreferredSize(new Dimension(54, 40));
            button.setFont(button.getFont().deriveFont(12f));
            button.setToolTipText("Probe row " + r + ", column " + c);
            button.addActionListener(event -> probeAt(r, c));
            seaButtons[row][column] = button;
            board.add(button);
        }
    }
    var instructions = new JLabel("<html><b>Find 3 treasures with 8 probes.</b> Click a sea square.<br>"
        + "A number is the distance at that time: row steps + column steps.<br>"
        + "For example: one square up and two right is distance 3.</html>");
    var top = new JPanel(new BorderLayout(0, 8));
    top.add(instructions, BorderLayout.NORTH);
    top.add(scoreLabel, BorderLayout.SOUTH);
    var controls = new JPanel();
    var outsideRow = new JButton("Try outside: row 9, col 1");
    outsideRow.addActionListener(event -> probeAt(9, 1));
    var outsideColumn = new JButton("Try outside: row 1, col 9");
    outsideColumn.addActionListener(event -> probeAt(1, 9));
    var restart = new JButton("New sea");
    restart.addActionListener(event -> {
        reset();
        for (var row : seaButtons) for (var button : row) {
            button.setText("~");
            button.setToolTipText("Unexplored sea square");
            button.setBackground(null);
        }
        feedbackLabel.setText("New sea. Click a square; the treasures have moved.");
        refreshSea();
    });
    controls.add(outsideRow);
    controls.add(outsideColumn);
    controls.add(restart);
    clueLog.setEditable(false);
    var bottom = new JPanel(new BorderLayout(0, 8));
    bottom.add(feedbackLabel, BorderLayout.NORTH);
    bottom.add(new JScrollPane(clueLog), BorderLayout.CENTER);
    bottom.add(controls, BorderLayout.SOUTH);
    var content = new JPanel(new BorderLayout(8, 8));
    content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    content.add(top, BorderLayout.NORTH);
    content.add(board, BorderLayout.CENTER);
    content.add(bottom, BorderLayout.SOUTH);
    frame.add(content);
    refreshSea();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset();
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Sonar Treasure Hunt needs a graphical display. Run java 04-decisions/Check.java P14_Task_SonarTreasureHunt.java to check insideSea.");
        return;
    }
    SwingUtilities.invokeLater(this::showWindow);
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
