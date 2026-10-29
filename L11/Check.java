import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_TinyStepSequencer.java`
/// Kontroly volají testovací metody přímo v této JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_TinyStepSequencer", "L11.01 TinyStepSequencer · F23", () -> M01_TinyStepSequencerTest.main(new String[0]));
        run("M02_CirclePacking", "L11.02 CirclePacking · F23", () -> M02_CirclePackingTest.main(new String[0]));
        run("M03_WireframeCube", "L11.03 WireframeCube · F23", () -> M03_WireframeCubeTest.main(new String[0]));
        run("M04_MandelbrotPostcards", "L11.04 MandelbrotPostcards · F27", () -> M04_MandelbrotPostcardsTest.main(new String[0]));
        run("M05_ChaosGame", "L11.05 ChaosGame · F27", () -> M05_ChaosGameTest.main(new String[0]));
        run("M06_ColourFilterSecrets", "L11.06 ColourFilterSecrets · F27", () -> M06_ColourFilterSecretsTest.main(new String[0]));
        run("M07_TwoNoisyPictures", "L11.07 TwoNoisyPictures · F27", () -> M07_TwoNoisyPicturesTest.main(new String[0]));
        run("M08_EndlessToneStaircase", "L11.08 EndlessToneStaircase · F32", () -> M08_EndlessToneStaircaseTest.main(new String[0]));
        run("M09_PixelSortingGlitch", "L11.09 PixelSortingGlitch · F32", () -> M09_PixelSortingGlitchTest.main(new String[0]));
        run("M10_SecretPNGPostcard", "L11.10 SecretPNGPostcard · F32", () -> M10_SecretPNGPostcardTest.main(new String[0]));
        run("M11_MirrorCylinderPicture", "L11.11 MirrorCylinderPicture · F32", () -> M11_MirrorCylinderPictureTest.main(new String[0]));
        run("M12_MagicEyeStrip", "L11.12 MagicEyeStrip · F32", () -> M12_MagicEyeStripTest.main(new String[0]));
        if (checked == 0) throw new IllegalArgumentException("Unknown game: " + selected);
        IO.println(checked + " missions; " + failed + " still need repair.");
        if (failed > 0) System.exit(1);
    }

    void run(String game, String label, Test test) {
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + label);
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + label + ": " + failure.getMessage());
        }
    }
}

class M01_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Dvě přepnutí obnoví stejný Set (množinu) aktivních not.",
        "Nová nota se objeví právě jednou.",
        "Neplatná cell (buňka) zachová příslušnost; prázdný Set uloží ticho."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I102: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M01_TinyStepSequencer();
            p.reset();var original=Set.copyOf(p.notes);p.toggle(0,1);p.toggle(0,1);
            check(p.notes.equals(original),0,p.notes);
            p.notes.clear();check(p.toggle(2,4)&&p.notes.contains(new M01_TinyStepSequencer.Note(2,4)),1,p.notes);
            check(p.toggle(2,4)&&p.notes.isEmpty(),0,p.notes);
            check(!p.toggle(-1,0)&&!p.toggle(4,0)&&!p.toggle(0,8)&&p.notes.isEmpty(),2,p.notes);
            check(Arrays.stream(p.sequence(1)).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I102: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "První střed se uloží a přijme.",
        "Stejný střed se odmítne; prohozené coordinate (souřadnice) jsou jiná hodnota.",
        "Skutečná zahrada dál umístí osm různých bublin."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I111: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M02_CirclePacking();
            Set<M02_CirclePacking.Centre> seen=new HashSet<>();
            check(p.claimCentre(seen,4,7)&&seen.size()==1,0,seen);
            check(!p.claimCentre(seen,4,7)&&seen.size()==1,1,seen);
            check(p.claimCentre(seen,7,4)&&seen.size()==2,1,seen);
            var bubbles=p.pack(42,8);check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->new M02_CirclePacking.Centre((int)b.x(),(int)b.y())).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I111: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "Stejné a obrácené dvojice se sloučí na dvě různé hrany.",
        "Zbývající edge key (klíče hran) mají pořadí menší/větší.",
        "Neplatná data, smyčky i prázdný vstup jsou bezpečné; skutečná krychle má dvanáct hran."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I122: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M03_WireframeCube();
            var edges=p.uniqueEdges(new int[][]{{0,1},{1,0},{0,1},{1,2}});
            check(edges.size()==2,0,edges);
            check(edges.contains(new M03_WireframeCube.Edge(0,1))&&edges.contains(new M03_WireframeCube.Edge(1,2)),1,edges);
            check(p.uniqueEdges(new int[][]{{-1,0},{0,8},{2,2}}).isEmpty(),2,"invalid");
            check(p.uniqueEdges(new int[0][2]).isEmpty(),2,"empty");
            check(p.uniqueEdges(p.edges).size()==12,0,p.uniqueEdges(p.edges).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I122: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_MandelbrotPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Změna seed (počáteční hodnoty) změní paletu.",
        "Stejný seed a výřez zopakují každý pixel (bod obrazu).",
        "Geometrie úniku zůstane nezávislá na paletě."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I104: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M04_MandelbrotPostcards();
            var a=p.render(42,3.2);var b=p.render(43,3.2);check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(p.render(42,3.2)),1,hash(a));check(p.escape(0,0)==160&&p.escape(3,3)<160,2,p.escape(3,3));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I104: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "Všechny tři platné vrcholy jsou dosažitelné.",
        "Pevný seed (počáteční hodnota) zopakuje sequence (posloupnost) indexů.",
        "Jediná možnost dá nulu a prázdný rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I105: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M05_ChaosGame();
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(p.chooseVertex(random,3));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(p.chooseVertex(a,3)==p.chooseVertex(b,3),1,n);
            check(p.chooseVertex(new Random(42),1)==0,2,"single");reject(()->p.chooseVertex(new Random(42),0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I105: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_ColourFilterSecretsTest {
    static int checks;
    static final String[] CASES = {
        "Změna seed (počáteční hodnoty) změní maskování.",
        "Stejný seed zopakuje všechny skryté pixel (body obrazu).",
        "Odhalená zpráva nezávisí na seed šumu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I116: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M06_ColourFilterSecrets();
            var mask=p.mask("HI");var a=p.hide(mask,42);var b=p.hide(mask,43);check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(p.hide(mask,42)),1,hash(a));check(hash(p.reveal(a))==hash(p.reveal(b)),2,hash(p.reveal(a)));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I116: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_TwoNoisyPicturesTest {
    static int checks;
    static final String[] CASES = {
        "Sousední prázdné dlaždice mají různé vzory teček.",
        "Stejné seed (počáteční hodnoty) opakují a jiné mění první část.",
        "Každá dlaždice části má dál přesně dvě černé tečky."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I118: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M07_TwoNoisyPictures();
            var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=p.shares(mask,42);var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
            check(hash(a[0])==hash(p.shares(mask,42)[0]),1,hash(a[0]));check(hash(a[0])!=hash(p.shares(mask,43)[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I118: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_EndlessToneStaircaseTest {
    static int checks;
    static final String[] CASES = {
        "Vznikne celý zadaný vnořený cíl.",
        "Skutečný WAV obsahuje očekávaný počet a frekvenci frame (rámců).",
        "Nadřazený file (soubor) místo adresáře způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I103: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("tone-check-");
        String filename = root.getFileName() + ".wav";
        Path misplaced = Path.of(filename).toAbsolutePath();
        boolean absent = Files.notExists(misplaced);
        try {
            var p = new M08_EndlessToneStaircase();
            if (!absent) throw new IllegalStateException("Unique test filename already exists: " + misplaced);
            Path file = root.resolve("nested").resolve(filename);
            p.write(file, new double[]{0, .2, -.2, 0});
            check(Files.isRegularFile(file), 0, file);
            try (var audio = javax.sound.sampled.AudioSystem.getAudioInputStream(file.toFile())) {
                check(audio.getFrameLength() == 4 && audio.getFormat().getSampleRate() == 22050, 1, audio.getFrameLength());
            }
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> p.write(blocked.resolve(filename), new double[1]), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I103: Unexpected failure while checking the active rule.", failure);
        } finally {
            if (absent) Files.deleteIfExists(misplaced);
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_PixelSortingGlitchTest {
    static int checks;
    static final String[] CASES = {
        "Po exportu existuje přesně zadaný vnořený název.",
        "Načtený PNG je seřazený a původní pixel (body obrazu) zůstanou stejné.",
        "Nepoužitelný nadřazený cíl způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I115: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("pixels-check-");
        try {
            var p = new M09_PixelSortingGlitch();
            var input = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
            input.setRGB(0, 0, 0xFFFFFFFF);
            input.setRGB(1, 0, 0xFF555555);
            input.setRGB(2, 0, 0xFFAAAAAA);
            Path file = root.resolve("nested/glitch.png");
            p.saveSorted(file, input, 0);
            check(Files.isRegularFile(file), 0, file);
            var result = ImageIO.read(file.toFile());
            check(result.getWidth() == 3 && (result.getRGB(0, 0) & 0xFFFFFF) == 0x555555, 1, Integer.toHexString(result.getRGB(0, 0)));
            check(input.getRGB(0, 0) == 0xFFFFFFFF, 1, Integer.toHexString(input.getRGB(0, 0)));
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> p.saveSorted(blocked.resolve("x.png"), input, 0), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I115: Unexpected failure while checking the active rule.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_SecretPNGPostcardTest {
    static int checks;
    static final String[] CASES = {
        "Vnořený PNG vrátí původní UTF-8 zprávu.",
        "Prázdná zpráva přežije stejnou cestu.",
        "Nepodporovaný a chybějící vstupní Path způsobí IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I117: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("secret-check-");
        try {
            var p = new M10_SecretPNGPostcard();
            Path file = root.resolve("nested/secret.png");
            p.message = "Pixel π";
            p.save(file, 42, 1);
            check(p.readMessage(file).equals("Pixel π"), 0, p.readMessage(file));
            p.message = "";
            p.save(file, 42, 1);
            check(p.readMessage(file).isEmpty(), 1, p.readMessage(file));
            Path text = root.resolve("plain.txt");
            Files.writeString(text, "not a PNG");
            ioFailure(() -> p.readMessage(text), 2);
            ioFailure(() -> p.readMessage(root.resolve("missing.png")), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I117: Unexpected failure while checking the active rule.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M11_MirrorCylinderPictureTest {
    static int checks;
    static final String[] CASES = {
        "Zadaný vnořený file (soubor) obsahuje neprázdná data.",
        "Skutečný uložený PNG má očekávané rozměry.",
        "Neplatná nadřazená cesta oznámí IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I119: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("mirror-check-");
        try {
            var p = new M11_MirrorCylinderPicture();
            Path file = root.resolve("nested/mirror.png");
            p.save(file, 42, 125);
            check(Files.exists(file) && Files.size(file) > 0, 0, Files.size(file));
            var image = ImageIO.read(file.toFile());
            check(image != null && image.getWidth() == 900 && image.getHeight() == 800, 1, image == null ? null : image.getWidth());
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> p.save(blocked.resolve("x.png"), 42, 125), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I119: Unexpected failure while checking the active rule.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M12_MagicEyeStripTest {
    static int checks;
    static final String[] CASES = {
        "Seed (počáteční hodnota) pochází z obsahu, nikoli názvu.",
        "Mezery i záporný seed fungují ve skutečném vstupu vykreslení.",
        "Nečíselný text a chybějící cesta zůstanou viditelné očekávané chyby."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I120: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("eye-check-");
        try {
            var p = new M12_MagicEyeStrip();
            Path file = root.resolve("seed.txt");
            Files.writeString(file, " 42\n");
            check(p.readSeed(file) == 42, 0, p.readSeed(file));
            check(hash(p.render(p.readSeed(file), 16)) == hash(p.render(42, 16)), 1, "same picture");
            Files.writeString(file, "-7");
            check(p.readSeed(file) == -7, 1, p.readSeed(file));
            Files.writeString(file, "banana");
            try {
                p.readSeed(file);
                check(false, 2, "accepted");
            } catch (NumberFormatException expected) {
                check(true, 2, "bad number");
            }
            ioFailure(() -> p.readSeed(root.resolve("missing.txt")), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I120: Unexpected failure while checking the active rule.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}
