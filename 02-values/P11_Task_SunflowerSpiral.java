import java.awt.*;
import java.awt.image.BufferedImage;

/// # Task · SunflowerSpiral
/// Lesson: 2.3 Let the program calculate — 03-calculations.html
///
/// **Grow a spiral from two spokes.** The program plants coloured seeds one after another. It
/// currently turns half a circle between seeds, leaving two opposite rows.
///
/// **Your task:** Find the angle setting between successive seeds and change the value used by an
/// ordinary run to `137.5` degrees. Use a decimal point. Keep the supplied coordinate calculation
/// unchanged.
///
/// **Result:** A fresh run will spread the seeds into a spiral flower. Once you find the setting,
/// also follow how its value reaches the drawing of one seed.

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Try three nearby values and give a friend a numeric recipe without showing the image. Ask them to
/// predict spokes or a spiral.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · explore as needed">
double defaultAngle = 180;

double[] position(int index, double degrees) {
    double angle = Math.toRadians(degrees) * index;
    double radius = 7 * Math.sqrt(index);
    return new double[]{250 + radius * Math.cos(angle), 250 + radius * Math.sin(angle)};
}

BufferedImage render(long seed, double degrees) {
    if (!Double.isFinite(degrees) || degrees < 1 || degrees > 360) {
        throw new IllegalArgumentException("Angle: 1..360 degrees.");
    }
    var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        float hue = new Random(seed).nextFloat();
        for (int i = 0; i < 1000; i++) {
            var point = position(i, degrees);
            g.setColor(Color.getHSBColor(hue + i / 1000.0F, 0.65F, 1));
            g.fillOval((int)point[0] - 3, (int)point[1] - 3, 6, 6);
        }
    } finally {
        g.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42, defaultAngle);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("SunflowerSpiral");
        var picture = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
        picture.setOpaque(true);
        picture.setBackground(new java.awt.Color(35, 39, 48));
        frame.add(picture);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    showPicture();
}
// </editor-fold>
