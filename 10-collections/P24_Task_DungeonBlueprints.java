import java.awt.*;
import java.awt.image.BufferedImage;

/// # Solved task · DungeonBlueprints
/// Lesson: 10.3 Each name has its own account — 03-maps.html
///
/// Original assignment (this version is complete):
///
/// Design the mix of rooms in a dungeon. Admit a limited number of small refuges and large chambers,
/// then let corridors connect the rooms that your policy accepted.
///
/// **Your mission:** Build admitRoom(room): area up to and including 42 is SMALL with a quota of three;
/// larger rooms are LARGE with a quota of two. Accept and count a room only while its category has room
/// left. Refusal returns false without changing counts. The generator calls you after geometric checks,
/// so these Map quotas control which rooms actually appear.
///
/// Run the program: the unfinished admission rule leaves the blueprint empty. After completing
/// admitRoom, inspect the accepted rooms, connecting corridors and category quotas.

boolean admitRoom(Room room) {
    String kind = room.width() * room.height() <= 42 ? "SMALL" : "LARGE";
    int limit = kind.equals("SMALL") ? 3 : 2;
    int used = roomCounts.getOrDefault(kind, 0);
    if (used >= limit) return false;
    roomCounts.put(kind, used + 1);
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Design a dungeon policy with one giant boss chamber and several small refuges. Change the area
/// threshold and quotas, then inspect how the same placement attempts tell a different story.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
record Room(int x, int y, int width, int height) {
    boolean overlaps(Room other) {
        return x - 1 < other.x + other.width && x + width + 1 > other.x && y - 1 < other.y + other.height && y + height + 1 > other.y;
    }
}

boolean[][] generate(long seed, int requested) {
    if (requested < 2 || requested > 18) {
        throw new IllegalArgumentException("Rooms: 2..18.");
    }
    roomCounts.clear();
    var random = new Random(seed);
    var rooms = new ArrayList<Room>();
    for (int attempt = 0; attempt < 500 && rooms.size() < requested; attempt++) {
        var room = new Room(1 + random.nextInt(37), 1 + random.nextInt(22), 4 + random.nextInt(7), 4 + random.nextInt(7));
        boolean fits = true;
        for (var other : rooms) {
            fits &= !room.overlaps(other);
        }
        if (fits && admitRoom(room)) {
            rooms.add(room);
        }
    }
    boolean[][] floor = new boolean[34][50];
    Room previous = null;
    for (var room : rooms) {
        for (int y = room.y; y < room.y + room.height; y++) {
            for (int x = room.x; x < room.x + room.width; x++) {
                floor[y][x] = true;
            }
        }
        if (previous != null) {
            int x = previous.x + previous.width / 2;
            int y = previous.y + previous.height / 2;
            int targetX = room.x + room.width / 2;
            int targetY = room.y + room.height / 2;
            while (x != targetX) {
                floor[y][x] = true;
                x += Integer.signum(targetX - x);
            }
            while (y != targetY) {
                floor[y][x] = true;
                y += Integer.signum(targetY - y);
            }
            floor[y][x] = true;
        }
        previous = room;
    }
    return floor;
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite room count required.");
    }
    var floor = generate(seed, (int)option);
    var image = new BufferedImage(600, 408, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int y = 0; y < 34; y++) {
            for (int x = 0; x < 50; x++) {
                g.setColor(floor[y][x] ? new Color(235, 220, 180) : new Color(30, 35, 40));
                g.fillRect(x * 12, y * 12, 11, 11);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("SMALL " + roomCounts.getOrDefault("SMALL", 0) + "/3 | LARGE " + roomCounts.getOrDefault("LARGE", 0) + "/2", 8, 14);
    } finally {
        g.dispose();
    }
    return image;
}

Map<String, Integer> roomCounts = new HashMap<>();

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

void main() {
    try {
        showImages("Dungeon Blueprints", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 8)});
    } catch (Exception failure) {
        IO.println("Cannot draw this result; check the editable rule: " + failure);
    }
}
// </editor-fold>
