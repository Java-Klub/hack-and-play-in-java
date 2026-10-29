import java.awt.*;
import java.util.List;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Solved task · FlipbookStudio
/// Lesson: 10.1 A bag need not know its size ahead of time — 01-lists.html
///
/// Original assignment (this version is complete):
///
/// Draw several tiny animation frames, then watch them flip into motion. Undo should let you experiment
/// boldly and peel back the latest mark first.
///
/// **Your mission:** Build undo(): return false for an empty history; otherwise remove the newest Edit,
/// restore its oldColour in that edit’s own frame/y/x, and return true. Painting works, but the
/// unfinished undo cannot recover any mark.

boolean undo() {
    if (history.isEmpty()) return false;
    Edit edit = history.removeLast();
    frames[edit.frame()][edit.y()][edit.x()] = edit.oldColour();
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Draw a four-frame creature walk using the existing frame keys. Make one foot stay fixed between two
/// frames and use undo to compare that illusion with a sliding foot.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 430;

int[][][] frames;
int edited, played, paintColour;
boolean playing, drawing;
String message;
void restart() {
    frames = new int[4][16][16];
    history.clear();
    edited = played = 0;
    playing = drawing = false;
    message = "Draw four frames; Space previews them";
}

void paintCell(int x, int y, int value) {
    if (x >= 0 && y >= 0 && x < 16 && y < 16) {
        history.add(new Edit(edited, x, y, frames[edited][y][x]));
        frames[edited][y][x] = value;
    }
}

void step() {
    if (playing) {
        played = (played + 1) % 4;
    }
}

void paintAt(int x, int y) {
    if (x >= 25 && x < 281 && y >= 55 && y < 311) {
        paintCell((x - 25) / 16, (y - 55) / 16, paintColour);
    }
}

void draw(Graphics2D g) {
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
            g.setColor(frames[edited][y][x] == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(25 + x * 16, 55 + y * 16, 16, 16);
            g.setColor(new Color(205, 205, 205));
            g.drawRect(25 + x * 16, 55 + y * 16, 16, 16);
        }
    }
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++) {
            g.setColor(frames[played][y][x] == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(350 + x * 12, 75 + y * 12, 12, 12);
        }
    }
    g.setColor(Color.DARK_GRAY);
    g.drawRect(350, 75, 192, 192);
    g.drawString("Editing frame " + (edited + 1), 25, 35);
    g.drawString("Preview frame " + (played + 1) + (playing ? " - playing" : " - paused"), 350, 45);
    for (int i = 0; i < 4; i++) {
        g.setColor(edited == i ? Color.ORANGE : Color.LIGHT_GRAY);
        g.fillRect(25 + i * 65, 335, 55, 32);
        g.setColor(Color.BLACK);
        g.drawString("" + (i + 1), 48 + i * 65, 356);
    }
    g.drawString(message, 20, 405);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
        edited = code - KeyEvent.VK_1;
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_Z -> undo();
            case KeyEvent.VK_SPACE -> playing = !playing;
        }
    }
}

void press(int x, int y, int button) {
    if (x >= 25 && x < 285 && y >= 335 && y < 367) {
        edited = (x - 25) / 65;
    }
    else {
        drawing = true;
        paintColour = button == MouseEvent.BUTTON3 ? 0 : 1;
        paintAt(x, y);
    }
}

void drag(int x, int y) {
    if (drawing) {
        paintAt(x, y);
    }
}

void release(int x, int y) {
    drawing = false;
}

void showWindow() {
    var frame = new JFrame("Flipbook Studio");
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
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    var timer = new Timer(180, e -> {
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Draw: left mouse   Erase: right mouse   1-4: frame   Space: play   Z: undo<br>R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

record Edit(int frame, int x, int y, int oldColour) {}
List<Edit> history = new ArrayList<>();

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
