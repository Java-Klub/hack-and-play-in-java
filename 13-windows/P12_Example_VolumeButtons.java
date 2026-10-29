import javax.swing.*;

/// # Hotový příklad · 13.3 Dej událostem jeden jasný úkol
/// Lekce: 03-handlers.html

int volume = 2;

boolean adjust(int delta) {
    int next = volume + delta;
    if (next < 0 || next > 5) return false;
    volume = next;
    return true;
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var status = new JLabel("Volume: " + volume);
        var quieter = new JButton("Quieter");
        var louder = new JButton("Louder");
        quieter.addActionListener(event -> {
            adjust(-1);
            status.setText("Volume: " + volume);
        });
        louder.addActionListener(event -> {
            adjust(1);
            status.setText("Volume: " + volume);
        });
        var panel = new JPanel();
        panel.add(quieter);
        panel.add(status);
        panel.add(louder);
        var frame = new JFrame("Volume Buttons");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
