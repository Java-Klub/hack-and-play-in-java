import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Vyřešený úkol · CafeWallLab
/// Lekce: 1.2 Pořadí příkazů — 02-order.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Odhal schovaný obrázek.** Spusť program. Uvidíš šedou plochu: kresba dlaždic je přemalovaná.
///
/// **Úkol:** V `paintWall` hned pod tímto textem prohoď dva řádky uvnitř `{ }`. Přesouvej celé
/// řádky včetně `(g);`. Oba zachovej; zbytek programu teď upravovat nemusíš.
///
/// **Výsledek:** Po uložení a novém spuštění uvidíš osm řad černých a bílých dlaždic. Šedý nátěr
/// má zůstat za nimi.

void paintWall(Graphics2D g) {
    paintBackground(g);
    paintTiles(g);
}

// <editor-fold defaultstate="collapsed" desc="Až se obrázek objeví">
/// Left/Right posouvá dlaždice, Up/Down mění šířku spár a Space ukáže červené pravítko. Zkus
/// kamaráda přesvědčit, že jsou vodorovné spáry šikmé; potom odhal pravítko.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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

// </editor-fold>
