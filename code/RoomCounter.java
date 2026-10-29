import javax.swing.*;

int count = 0;

boolean handle(String command) {
    if (command.equals("add")) {
        count++;
        return true;
    }
    return false;
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var status = new JLabel("Inside: " + count);
        var panel = new JPanel();
        for (String command : new String[]{"add", "leave", "reset"}) {
            var button = new JButton(command);
            button.addActionListener(event -> {
                boolean accepted = handle(command);
                status.setText("Inside: " + count + (accepted ? "" : " (refused)"));
            });
            panel.add(button);
        }
        panel.add(status);
        var frame = new JFrame("Room Counter");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
