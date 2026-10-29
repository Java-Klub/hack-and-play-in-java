// <editor-fold defaultstate="collapsed" desc="Prepared tools">
import module java.base;
import java.awt.*;
import java.awt.image.BufferedImage;
// </editor-fold>

class M08_DungeonBlueprints {
    /// # L09.08 · DungeonBlueprints
    ///
    /// Generate a dungeon blueprint and discover which rooms fit before corridors join them. Its legend
    /// should tell you how many small and large rooms actually survived placement.
    ///
    /// **Your mission:** Each accepted room resets its category count to one. Repair the legend’s totals
    /// without counting rejected placement attempts.
    ///
    /// **Today you learn:** A Map groups many individual rooms under shared category keys. Classification
    /// chooses the key; accumulation updates that key’s total without erasing earlier rooms.
    ///
    /// From the course folder containing README.md:
    /// - Play: `java L09/M08_DungeonBlueprints.java`
    /// - Check this mission: `java L09/Check.java M08_DungeonBlueprints.java`
    ///
    /// After the play command, inspect the Java picture; close its window, edit the active rule, Save
    /// and the check command, then run the play command again to compare. After your repair: save the
    /// file, run the check command above, then play again.
    ///
    /// Need an explanation? Recall · F24 explains the Java skill; Read this code explains these lines.
    /// Hints 1 and 2 open separately; Repair outline reveals the direction.
    void countRoom(Room room) {
        String kind = room.width() * room.height() <= 42 ? "SMALL" : "LARGE";
        roomCounts.put(kind, roomCounts.getOrDefault(kind, 0) + 1);
    }

    // <editor-fold defaultstate="collapsed" desc="Recall · F24">
    /// ### Map: look up a value by its key
    ///
    /// A `Map<String, Integer>` associates text keys with integer values. Each key has at most one stored
    /// value.
    ///
    /// ```java
    /// Map<String, Integer> badges = new HashMap<>();
    /// badges.put("moon", 4);
    /// IO.println(badges.getOrDefault("moon", 0));
    /// IO.println(badges.getOrDefault("sun", 0));
    /// ```
    ///
    /// The output is `4`, then `0`. `getOrDefault(key, fallback)` reads an existing value, or returns the
    /// fallback when the key is absent. This lookup does not insert anything. `put(key, value)` stores a
    /// value and replaces any previous value for that key; it does not add numbers automatically. Use
    /// `containsKey(key)` when you need to distinguish absence explicitly. These examples do not store
    /// null values.
    ///
    /// Predict: after `badges.put("moon", 9)`, what will the same two lookups return?
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Read this code">
    /// ## Read these particular lines
    ///
    /// - `room.width() * room.height()` — Reads record components and multiplies them to obtain room area.
    /// - `<= 42 ? "SMALL" : "LARGE"` — Chooses one category string; area 42 belongs to SMALL.
    /// - `roomCounts.put(kind, 1)` — Stores one under the chosen key even if several rooms share it.
    ///
    /// ### What the prepared part already does
    ///
    /// generate proposes bounded rooms, asks countRoom to remember accepted sizes, and joins the rooms
    /// by corridors. render draws the map and its SMALL/LARGE totals directly in Java.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
    /// The area boundary includes exactly 42.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
    /// Different room objects can have the same kind key.
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
    /// Use the old count or zero, then add one.
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
            if (fits) {
                rooms.add(room);
                countRoom(room);
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

    /// Supplied image creation: generation and drawing remain visible in this file.
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
            g.drawString("SMALL " + roomCounts.getOrDefault("SMALL", 0) + " / LARGE " + roomCounts.getOrDefault("LARGE", 0), 8, 14);
        } finally {
            g.dispose();
        }
        return image;
    }


    Map<String, Integer> roomCounts = new HashMap<>();

    /// Use the play command above to start the program. The check command checks the rule without
    /// opening a window. The supplied part supports only the main game or experiment described above.
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

    // <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
    /// ## An unfamiliar piece of Java?
    ///
    /// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
    ///
    /// - `///` — Words for the reader. Java skips these comment lines when running.
    /// - `import` — Makes library names available in this file; it does not run the library.
    /// - `;` — Ends one statement, like a full stop. A semicolon inside a for header separates its three
    ///   parts.
    /// - `()` — After a name: call an action, or list its parameters in its declaration. Empty parentheses
    ///   mean no supplied arguments.
    /// - `{}` — Braces group a block of code. The opening and closing brace belong together.
    /// - `.` — Accesses something belonging to the value or type on its left, such as IO.println.
    /// - `void` — This method performs an action and returns no result value.
    /// - `main` — Java starts the program here. Other method declarations do not run just because they appear
    ///   earlier in the file.
    /// - `int` — A type for whole numbers within a fixed range.
    /// - `double` — A type for approximate fractional numbers. Java uses a decimal point.
    /// - `boolean` — A value that is either true or false.
    /// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
    /// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
    /// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
    /// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
    ///   type.
    /// - `=` — Stores the value on the right under the name on the left. It does not compare them.
    /// - `!=` — Checks that two values differ.
    /// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
    /// - `||` — At least one condition must be true; if the first is true, Java skips the second.
    /// - `!` — In front of a boolean expression, reverses true and false.
    /// - `++` — Increases a numeric value by one.
    /// - `+=` — Adds to the old value and stores the result back under the same name.
    /// - `+` — Adds numbers. When a String is involved, joins text instead.
    /// - `-` — Subtracts numbers; in front of one number, changes its sign.
    /// - `*` — Multiplies numbers. In an import such as java.awt.*, makes that package’s type names
    ///   available.
    /// - `/` — Divides numbers. Dividing two int values discards the fractional part.
    /// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
    ///   direction. In List<String>, angle brackets describe the element type.
    /// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
    ///   the second.
    /// - `:` — In for (int value : values), reads each element in turn. It also separates the two
    ///   alternatives of ? : or follows a switch label.
    /// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
    ///   count.
    /// - `new` — Creates a fresh value or object, such as a new array or window.
    /// - `return` — Ends this method call, optionally handing a result back to its caller.
    /// - `if` — Runs its body only when the condition in parentheses is true.
    /// - `for` — Repeats a block over a range or a sequence of values.
    /// - `while` — Keeps repeating its body while its condition stays true.
    /// - `record` — A small named bundle of values. Its component accessors use the component names with ().
    /// - `null` — No object is present.
    /// - `try` — A block whose possible failures can be handled by the following catch.
    /// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
    /// - `throw` — Reports a failure by creating or passing an exception.
    /// - `Map` — Associates keys with values. A missing key needs an explicit fallback or decision.
    /// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
    /// - `IO` — Console input and output. println writes a line.
    // </editor-fold>

}
