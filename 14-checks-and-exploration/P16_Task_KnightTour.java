import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Vyřešený úkol · KnightTour
/// Lekce: 14.3 Zjisti, která datová varianta přišla — 03-patterns.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Projeď malou desku šachovým jezdcem a na každé políčko vstup nejvýše jednou. Platný tah má dojít na
/// místo kliknutí, ne na jeho odraz přes úhlopříčku.
///
/// **Tvoje mise:** Sestav apply jako celý command dispatcher (rozesílání příkazů). Move uvnitř desky
/// 5 na 5 volá move se svým sloupcem a řádkem; Move mimo desku vrací false. Restart obnoví desku a
/// yield vrátí true. Pravidlo jezdce i navštívená pole nech metodě move.
///
/// Po spuštění programu klikni na zelený tah jezdce, například na sloupec 1 a řádek 2 při
/// číslování od nuly, a klávesou R začni cestu znovu.

boolean apply(Command command) {
    return switch (command) {
        case Move(var xx, var yy) when xx >= 0 && yy >= 0 && xx < 5 && yy < 5 -> move(xx, yy);
        case Move(var xx, var yy) -> false;
        case Restart() -> { restart(); yield true; }
    };
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni jiného jezdce: legal-move predicate (podmínku tahu) změň na velblouda, který jde tři pole
/// po jedné ose a jedno po druhé. Dispatch (rozesílání) Move/Restart zachovej. Dokážeš na stejné
/// desce navštívit podobný počet různých polí?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Pořadí složek nese význam
///
/// Deklarace `Move(int x, int y)` určuje pořadí components (složek). Při rozpoznání Move vytvořeného
/// z hodnot 2 a 1 získá xx hodnotu 2 a yy hodnotu 1; místní jména tyto hodnoty neprohodí. Před
/// spuštěním předpověz požadované políčko a porovnej jej s políčkem předaným do move. Prohození
/// souřadnic zde může být také platným skokem jezdce, takže samotné přijetí tahu není dostatečný
/// důkaz. Trasuj i Move mimo desku: musí dojít k odmítající alternativě bez změny desky. Restart je
/// jiná alternativa, nikoli dvojice souřadnic.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 520, HEIGHT = 420;

int[][] visited;
int x, y, count;
void restart() {
    visited = new int[5][5];
    x = y = 0;
    count = 1;
    visited[0][0] = 1;
}

boolean legal(int xx, int yy) {
    return xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && visited[yy][xx] == 0 && Math.abs(xx - x) * Math.abs(yy - y) == 2;
}

boolean move(int xx, int yy) {
    if (!legal(xx, yy)) {
        return false;
    }
    x = xx;
    y = yy;
    visited[y][x] = ++count;
    return true;
}

boolean won() {
    return count == 25;
}

void draw(Graphics2D g) {
    for (int yy = 0; yy < 5; yy++) {
        for (int xx = 0; xx < 5; xx++) {
            int px = 100 + xx * 60, py = 35 + yy * 60;
            g.setColor((xx + yy) % 2 == 0 ? new Color(235, 225, 205) : new Color(135, 155, 145));
            g.fillRect(px, py, 60, 60);
            if (legal(xx, yy)) {
                g.setColor(new Color(75, 190, 120));
                g.fillOval(px + 22, py + 22, 16, 16);
            }
            if (visited[yy][xx]>0) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("" + visited[yy][xx], px + 23, py + 35);
            }
        }
    }
    g.setColor(new Color(65, 75, 200));
    g.drawOval(105 + x * 60, 40 + y * 60, 50, 50);
    g.setColor(Color.BLACK);
    boolean canContinue = false;
    for (int row = 0; row < 5; row++) {
        for (int column = 0; column < 5; column++) canContinue |= legal(column, row);
    }
    g.drawString(won() ? "All 25 squares visited! R: replay" : !canContinue ? "No legal moves remain. R: try a new tour" : "Visited " + count + " / 25. Green dots are legal next moves.", 20, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        apply(new Restart());
    }
}

void press(int x, int y, int button) {
    if (x >= 100 && y >= 35 && x < 400 && y < 335) {
        apply(new Move((x - 100) / 60, (y - 35) / 60));
    }
}

void showWindow() {
    var frame = new JFrame("Knight Tour");
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
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click a green legal move   Visit every square once   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

sealed interface Command permits Move, Restart {}
record Move(int x, int y) implements Command {}
record Restart() implements Command {}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
