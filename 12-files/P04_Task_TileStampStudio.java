import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

/// # Task · TileStampStudio
/// Lesson: 12.1 Decide which values belong — 01-validation.html
///
/// The stamp editor already accepts numbers. Give it a tiny command language so a planned sequence can
/// zoom the density without calculating every new number yourself.
///
/// **Your mission:** Add denser to double the current repeat count and sparser to halve it only when it
/// is even. Commands ignore case and surrounding whitespace. Keep numeric choices 1–12. Reject a typo,
/// an odd halving or any result outside 1–12 without changing the live pattern. A command is relative
/// to the last accepted setting, not always the initial one.
///
/// Draw an asymmetric tile. Enter 3 and click Repeats, then try denser, denser, sparser. The starter
/// refuses those words; your command tool must visibly produce densities 6, 12, 6. Clear resets the
/// drawing, not the density.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
int[][] tile = new int[8][8];
int[] palette = {0xFFFFFF, 0x182030};
JButton[][] buttons = new JButton[8][8];

boolean paint(int row, int col, int colour) {
    if (row < 0 || row >= 8 || col < 0 || col >= 8 || colour < 0 || colour >= palette.length) {
        return false;
    }
    tile[row][col] = colour;
    return true;
}

BufferedImage render(int repeats) {
    if (repeats < 1 || repeats > 12) {
        throw new IllegalArgumentException("Repeats: 1..12.");
    }
    int size = 288;
    var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            image.setRGB(x, y, palette[tile[(y * repeats * 8 / size) % 8][(x * repeats * 8 / size) % 8]]);
        }
    }
    return image;
}

boolean applyRepeats(String text) {
    try {
        int candidate = Integer.parseInt(text.strip());
        if (candidate < 1 || candidate > 12) return false;
        repeats = candidate;
        return true;
    } catch (NumberFormatException invalid) {
        return false;
    }
}

void refresh() {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            buttons[row][col].setBackground(new Color(palette[tile[row][col]]));
            buttons[row][col].setOpaque(true);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Tile Stamp Studio");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var preview = new JPanel(){

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(render(repeats), 0, 0, null);
        }
    };
    preview.setPreferredSize(new Dimension(288, 288));
    var grid = new JPanel(new GridLayout(8, 8, 1, 1));
    grid.setPreferredSize(new Dimension(288, 288));
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            int r = row;
            int c = col;
            var button = new JButton();
            buttons[row][col] = button;
            button.addActionListener(event -> {
                paint(r, c, tile[r][c] == 0 ? 1 : 0);
                refresh();
                preview.repaint();
            });
            grid.add(button);
        }
    }
    var controls = new JPanel();
    var clear = new JButton("Clear");
    clear.addActionListener(event -> {
        tile = new int[8][8];
        refresh();
        preview.repaint();
    });
    var status = new JLabel("Repeated preview");
    var repeatText = new JTextField("6", 3);
    var apply = new JButton("Repeats");
    apply.addActionListener(event -> {
        boolean accepted = applyRepeats(repeatText.getText());
        status.setText(accepted ? "Repeats: " + repeats : "Use a whole number 1..12");
        preview.repaint();
    });
    controls.add(repeatText);
    controls.add(apply);
    controls.add(clear);
    frame.add(grid, BorderLayout.WEST);
    frame.add(preview, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.add(status, BorderLayout.NORTH);
    refresh();
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

int repeats = 6;

void main() {
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent a reset-density command with your favourite sparse setting. Decide whether it should also
/// erase the tile, then make that distinction clear in the interface.
// </editor-fold>
