import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// # Vyřešený úkol · SecretPNGPostcard
/// Lekce: 12.5 Ukládej až po ověření — 05-save.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Nosič zpráv už funguje. Dej mu skutečný obrázek: použij vlastní PNG místo kresby znovu vytvářené
/// uvnitř programu.
///
/// **Tvoje mise:** Použij data/cover.png jako skutečný obrázkový vstup nové tajné pohlednice. Načti
/// jej, schovej zadanou zprávu a zapiš zvlášť data/postcard.png bez změny obálky. Pozdější Enter má
/// otevřít uloženou kartu beze změny. Výměna obálky ovlivní další nově napsanou kartu, ne automaticky
/// starou.
///
/// **Zkoumání:** Prozkoumej uchovaná vstupní data a vytvoř nástroj obrázek → pohlednice.
///
/// První běh vytvoří ukázkovou obálku. Na vypsanou cestu obálky vlož jiné PNG, napiš novou zprávu a
/// porovnej kartu. Enter otevírá existující kartu; /q skončí.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// Do obálky navrhni obrazovou hádanku a do dvou karet vlož dvě různé zprávy. Před dekódováním
/// porovnej běžný obraz, potom zjisti, které spodní bity pixelů se změnily. Oba původní obrázky
/// zachovej.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">

String readMessage(Path path) throws IOException {
    try (var input = Files.newInputStream(path)) {
        var image = ImageIO.read(input);
        if (image == null) throw new IOException("Input is not a readable image.");
        return decode(image);
    }
}

String message = "The moon has excellent Wi-Fi.";

BufferedImage encode(BufferedImage original, String text) {
    byte[] payload = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    int capacity = original.getWidth() * original.getHeight() * 3 / 8 - 4;
    if (payload.length > capacity) {
        throw new IllegalArgumentException("Message exceeds image capacity.");
    }
    byte[] data = new byte[payload.length + 4];
    java.nio.ByteBuffer.wrap(data).putInt(payload.length).put(payload);
    var image = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    g.drawImage(original, 0, 0, null);
    g.dispose();
    for (int bit = 0; bit < data.length * 8; bit++) {
        int pixel = bit / 3;
        int x = pixel % image.getWidth();
        int y = pixel / image.getWidth();
        int shift = 16 - bit % 3 * 8;
        int rgb = image.getRGB(x, y);
        int value = (data[bit / 8] >> (7 - bit % 8)) & 1;
        image.setRGB(x, y, (rgb & ~(1 << shift)) | (value << shift));
    }
    return image;
}

int readByte(BufferedImage image, int index) {
    int result = 0;
    for (int offset = 0; offset < 8; offset++) {
        int bit = index * 8 + offset;
        int pixel = bit / 3;
        int rgb = image.getRGB(pixel % image.getWidth(), pixel / image.getWidth());
        result = result * 2 + ((rgb >> (16 - bit % 3 * 8)) & 1);
    }
    return result;
}

String decode(BufferedImage image) {
    int capacity = image.getWidth() * image.getHeight() * 3 / 8;
    if (capacity < 4) {
        throw new IllegalArgumentException("Image is too small.");
    }
    int length = 0;
    for (int i = 0; i < 4; i++) {
        length = (length << 8) | readByte(image, i);
    }
    if (length < 0 || length > capacity - 4) {
        throw new IllegalArgumentException("Invalid message header.");
    }
    byte[] data = new byte[length];
    for (int i = 0; i < length; i++) {
        data[i] = (byte)readByte(image, i + 4);
    }
    return new String(data, java.nio.charset.StandardCharsets.UTF_8);
}

BufferedImage background(long seed) {
    var image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 400; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 400.0F, 0.5F, 0.6F + random.nextFloat() * 0.4F));
        }
    }
    return image;
}

void showImages(String title, String[] captions, java.awt.image.BufferedImage[] images) {
    if (java.awt.GraphicsEnvironment.isHeadless()) return;
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        var pictures = new javax.swing.JPanel(new java.awt.GridLayout(1, images.length, 8, 8));
        for (int i = 0; i < images.length; i++) {
            var picture = new javax.swing.JPanel(new java.awt.BorderLayout());
            picture.add(new javax.swing.JLabel(captions[i], javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
            double scale = Math.min(1, Math.min(900.0 / images.length / images[i].getWidth(), 550.0 / images[i].getHeight()));
            var icon = new javax.swing.ImageIcon(images[i].getScaledInstance(
                    Math.max(1, (int)(images[i].getWidth() * scale)),
                    Math.max(1, (int)(images[i].getHeight() * scale)), java.awt.Image.SCALE_FAST));
            picture.add(new javax.swing.JLabel(icon), java.awt.BorderLayout.CENTER);
            pictures.add(picture);
        }
        frame.add(pictures);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void postcardFromCover(Path cover, Path destination, String text) throws Exception {
    if (Files.exists(destination) && Files.isSameFile(cover, destination)) {
        throw new IllegalArgumentException("Cover and postcard must be different files.");
    }
    BufferedImage original;
    try (var input = Files.newInputStream(cover)) {
        original = ImageIO.read(input);
        if (original == null) throw new IOException("Cover is not a readable image.");
    }
    BufferedImage postcard = encode(original, text);
    Files.createDirectories(destination.toAbsolutePath().getParent());
    try (var output = Files.newOutputStream(destination)) {
        if (!ImageIO.write(postcard, "png", output)) throw new IOException("PNG writer missing.");
    }
}

void main() {
    Path cover = Path.of("data", "cover.png");
    Path card = Path.of("data", "postcard.png");
    try {
        if (!Files.exists(cover)) {
            Files.createDirectories(cover.toAbsolutePath().getParent());
            if (!ImageIO.write(background(42), "png", cover.toFile())) throw new IOException("PNG writer missing.");
        }
        IO.println("Artwork input: " + cover.toAbsolutePath());
        String text = IO.readln("New secret message, Enter opens the saved postcard, /q to quit: ");
        if (text == null || text.strip().equalsIgnoreCase("/q")) return;
        if (!text.isEmpty() || !Files.exists(card)) {
            String chosen = text.isEmpty() ? "Pixel moon" : text;
            postcardFromCover(cover, card, chosen);
        }
        String recovered = readMessage(card);
        if (!text.isEmpty() && !recovered.equals(text)) throw new IOException("The saved message differs from the new text.");
        var image = ImageIO.read(card.toFile());
        if (image == null) throw new IOException("The postcard is not a readable PNG.");
        IO.println("Recovered from " + card.toAbsolutePath() + ": " + recovered);
        showImages("Secret PNG Postcard", new String[]{recovered}, new BufferedImage[]{image});
    } catch (Exception failure) {
        IO.println("Cannot open the postcard: " + failure.getMessage());
    }
}
// </editor-fold>
