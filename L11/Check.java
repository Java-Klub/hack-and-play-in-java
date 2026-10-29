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
/// Hry zůstávají compact sources; následující kontroly k nim přistupují pomocí reflection.
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
        IO.println(checked + " missions; " + failed + " mission goals remaining.");
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

@SuppressWarnings("unchecked")
class M01_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Dvě přepnutí obnoví stejný Set (množinu) aktivních not.",
        "Nová nota se objeví právě jednou.",
        "Neplatná cell (buňka) zachová členství; prázdný Set (množina) vytvoří tiché zvukové vzorky."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.01: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M01_TinyStepSequencer");
            Reflect.call(p, "reset");var original=Set.copyOf(((java.util.Set<Object>) Reflect.get(p, "notes")));Reflect.call(p, "toggle", 0, 1);Reflect.call(p, "toggle", 0, 1);
            check(((java.util.Set<Object>) Reflect.get(p, "notes")).equals(original),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            ((java.util.Set<Object>) Reflect.get(p, "notes")).clear();check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).contains(Reflect.create("M01_TinyStepSequencer$Note", 2, 4)),1,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(!((boolean) Reflect.call(p, "toggle", -1, 0))&&!((boolean) Reflect.call(p, "toggle", 4, 0))&&!((boolean) Reflect.call(p, "toggle", 0, 8))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),2,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(Arrays.stream(((double[]) Reflect.call(p, "sequence", 1))).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.01: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "První střed se uloží a přijme.",
        "Stejný střed se odmítne; prohozené coordinate (souřadnice) jsou jiná hodnota.",
        "Skutečná zahrada dál umístí osm různých bublin."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.02: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M02_CirclePacking");
            Set<Object> seen=new HashSet<>();
            check(((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,0,seen);
            check(!((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,1,seen);
            check(((boolean) Reflect.call(p, "claimCentre", seen, 7, 4))&&seen.size()==2,1,seen);
            var bubbles=((java.util.List<Object>) Reflect.call(p, "pack", 42, 8));check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->Reflect.create("M02_CirclePacking$Centre", (int)((double) Reflect.call(b, "x")), (int)((double) Reflect.call(b, "y")))).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.02: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "Stejné a obrácené dvojice se sloučí na dvě různé hrany.",
        "Zbývající edge key (klíče hran) mají pořadí menší/větší.",
        "Neplatná data, smyčky i prázdný vstup jsou bezpečné; skutečná krychle má dvanáct hran."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.03: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M03_WireframeCube");
            var edges=((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{0,1},{1,0},{0,1},{1,2}}));
            check(edges.size()==2,0,edges);
            check(edges.contains(Reflect.create("M03_WireframeCube$Edge", 0, 1))&&edges.contains(Reflect.create("M03_WireframeCube$Edge", 1, 2)),1,edges);
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{-1,0},{0,8},{2,2}})).isEmpty(),2,"invalid");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[0][2])).isEmpty(),2,"empty");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size()==12,0,((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.03: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_MandelbrotPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Nástroj najde všechny odpovídající seed ve včetně zahrnutém rozsahu 0..255.",
        "Prefix (začátek), žádná shoda i zachování vstupu fungují.",
        "Běžná paleta i fraktál dál zachovají opakovatelnost."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.04: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("M04_MandelbrotPostcards");
        int[] palette = (int[]) Reflect.call(p, "palette", 7L);
        int[] sample = Arrays.copyOf(palette, 4);
        int[] before = sample.clone();
        var found = (java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) sample);
        check(found.equals(java.util.List.of(7L)), 0, found);
        check(Arrays.equals(sample, before), 1, "sample unchanged");
        var edge = (java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) (int[]) Reflect.call(p, "palette", 255L));
        check(edge.equals(java.util.List.of(255L)), 0, edge);
        var expected = new java.util.ArrayList<Long>();
        for (long seed = 0; seed <= 255; seed++) {
            var random = new Random(seed);
            int colour = java.awt.Color.HSBtoRGB(random.nextInt(360) / 360.0F, .8F, 1);
            if (colour == palette[0]) expected.add(seed);
        }
        check(Reflect.call(p, "matchingSeeds", (Object) new int[]{palette[0]}).equals(expected), 1, "all prefix candidates");
        check(((java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) new int[]{0})).isEmpty(), 1, "impossible colour");
        reject(() -> Reflect.call(p, "matchingSeeds", (Object) new int[0]), 1);
        reject(() -> Reflect.call(p, "matchingSeeds", (Object) new int[17]), 1);
        var random = new Random(7);
        for (int colour : palette) check(colour == java.awt.Color.HSBtoRGB(random.nextInt(360) / 360.0F, .8F, 1), 2, "unchanged palette recipe");
        var a = (BufferedImage) Reflect.call(p, "render", 42L, 3.2);
        check(hash(a) == hash((BufferedImage) Reflect.call(p, "render", 42L, 3.2)), 2, "repeatable picture");
        check((int) Reflect.call(p, "escape", 0.0, 0.0) == 160, 2, "fractal geometry");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "Všechny tři platné vrcholy jsou dosažitelné.",
        "Pevný seed (počáteční hodnota) zopakuje sequence (posloupnost) indexů.",
        "Jediná možnost dá nulu a prázdný rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.05: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M05_ChaosGame");
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(((int) Reflect.call(p, "chooseVertex", random, 3)));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(((int) Reflect.call(p, "chooseVertex", a, 3))==((int) Reflect.call(p, "chooseVertex", b, 3)),1,n);
            check(((int) Reflect.call(p, "chooseVertex", new Random(42), 1))==0,2,"single");reject(()->Reflect.call(p, "chooseVertex", new Random(42), 0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.05: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_ColourFilterSecretsTest {
    static int checks;
    static final String[] CASES = {
        "Změna seed (počáteční hodnoty) změní maskování.",
        "Stejný seed zopakuje všechny skryté pixel (body obrazu).",
        "Odhalená zpráva nezávisí na seed šumu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.06: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M06_ColourFilterSecrets");
            var mask=((java.awt.image.BufferedImage) Reflect.call(p, "mask", "HI"));var a=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42));var b=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 43));check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42))),1,hash(a));check(hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a)))==hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", b))),2,hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.06: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_TwoNoisyPicturesTest {
    static int checks;
    static final String[] CASES = {
        "Sousední prázdné dlaždice mají různé vzory teček.",
        "Stejné seed (počáteční hodnoty) opakují a jiné mění první část.",
        "Každá dlaždice části má dál přesně dvě černé tečky."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.07: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M07_TwoNoisyPictures");
            var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42));var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
            check(hash(a[0])==hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42))[0]),1,hash(a[0]));check(hash(a[0])!=hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 43))[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.07: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_EndlessToneStaircaseTest {
    static int checks;
    static final String[] CASES = {
        "Vznikne celý zadaný vnořený cíl.",
        "Skutečný WAV obsahuje očekávaný počet a frekvenci frame (rámců).",
        "Nadřazený file (soubor) místo adresáře způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.08: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M08_EndlessToneStaircase");
            if (!absent) throw new IllegalStateException("Unique test filename already exists: " + misplaced);
            Path file = root.resolve("nested").resolve(filename);
            Reflect.call(p, "write", file, new double[]{0, .2, -.2, 0});
            check(Files.isRegularFile(file), 0, file);
            try (var audio = javax.sound.sampled.AudioSystem.getAudioInputStream(file.toFile())) {
                check(audio.getFrameLength() == 4 && audio.getFormat().getSampleRate() == 22050, 1, audio.getFrameLength());
            }
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> Reflect.call(p, "write", blocked.resolve(filename), new double[1]), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.08: Unexpected failure while checking the active rule.", failure);
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

@SuppressWarnings("unchecked")
class M09_PixelSortingGlitchTest {
    static int checks;
    static final String[] CASES = {
        "Po exportu existuje přesně zadaný vnořený název.",
        "Načtený PNG je seřazený a původní pixel (body obrazu) zůstanou stejné.",
        "Nepoužitelný nadřazený cíl způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.09: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M09_PixelSortingGlitch");
            var input = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
            input.setRGB(0, 0, 0xFFFFFFFF);
            input.setRGB(1, 0, 0xFF555555);
            input.setRGB(2, 0, 0xFFAAAAAA);
            Path file = root.resolve("nested/glitch.png");
            Reflect.call(p, "saveSorted", file, input, 0);
            check(Files.isRegularFile(file), 0, file);
            var result = ImageIO.read(file.toFile());
            check(result.getWidth() == 3 && (result.getRGB(0, 0) & 0xFFFFFF) == 0x555555, 1, Integer.toHexString(result.getRGB(0, 0)));
            check(input.getRGB(0, 0) == 0xFFFFFFFF, 1, Integer.toHexString(input.getRGB(0, 0)));
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> Reflect.call(p, "saveSorted", blocked.resolve("x.png"), input, 0), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.09: Unexpected failure while checking the active rule.", failure);
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

@SuppressWarnings("unchecked")
class M10_SecretPNGPostcardTest {
    static int checks;
    static final String[] CASES = {
        "Skutečná obálka dodá nové pohlednici pixely i rozměry.",
        "Zpráva přetrvá a původní soubor zůstane beze změny.",
        "Nečitelné vstupy a obálka jako vlastní cíl se odmítnou."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.10: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("cover-check-");
        try {
            var p = Reflect.create("M10_SecretPNGPostcard");
            Path cover = root.resolve("cover.png");
            Path card = root.resolve("nested/card.png");
            var original = new BufferedImage(48, 20, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < 20; y++) for (int x = 0; x < 48; x++) original.setRGB(x, y, java.awt.Color.HSBtoRGB(x / 48.0F, .7F, .6F + y / 60.0F));
            ImageIO.write(original, "png", cover.toFile());
            byte[] before = Files.readAllBytes(cover);
            Reflect.call(p, "postcardFromCover", cover, card, "Meet π at noon");
            var result = ImageIO.read(card.toFile());
            check(result != null && result.getWidth() == 48 && result.getHeight() == 20, 0, result == null ? "no image" : result.getWidth() + "x" + result.getHeight());
            boolean sameArtwork = true;
            for (int y = 0; y < 20; y++) for (int x = 0; x < 48; x++) sameArtwork &= (original.getRGB(x,y) & 0xFEFEFE) == (result.getRGB(x,y) & 0xFEFEFE);
            check(sameArtwork, 0, "only the message bits may change");
            check(Reflect.call(Reflect.create("M10_SecretPNGPostcard"), "readMessage", card).equals("Meet π at noon"), 1, "persisted message");
            check(Arrays.equals(before, Files.readAllBytes(cover)), 1, "cover bytes");
            reject(() -> Reflect.call(p, "postcardFromCover", cover, cover, "overwrite"), 2);
            check(Arrays.equals(before, Files.readAllBytes(cover)), 1, "cover after rejected write");
            ioFailure(() -> Reflect.call(p, "postcardFromCover", root.resolve("missing.png"), card, "x"), 2);
            Path invalid = root.resolve("not-image.txt");
            Files.writeString(invalid, "not a PNG");
            ioFailure(() -> Reflect.call(p, "postcardFromCover", invalid, card, "x"), 2);
            check(Reflect.call(p, "readMessage", card).equals("Meet π at noon"), 1, "bad input preserves the prior card");
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_MirrorCylinderPictureTest {
    static int checks;
    static final String[] CASES = {
        "Vybraný soubor dodá první řádek bez okrajových whitespace (bílých znaků).",
        "Obsah UTF-8 se zachová a caption (nápis) ovlivní skutečný obrázek.",
        "Chybějící soubor i neplatný první řádek ohlásí I/O failure (chybu vstupu/výstupu)."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.11: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M11_MirrorCylinderPicture");
            Path file = root.resolve("nested/word.txt");
            Files.createDirectories(file.getParent());
            Files.writeString(file, "  MOON  \nignored second line\n");
            check(((String) Reflect.call(p, "readCaption", file)).equals("MOON"), 0, Reflect.call(p, "readCaption", file));
            Files.writeString(file, "π!\n");
            check(((String) Reflect.call(p, "readCaption", file)).equals("π!"), 1, Reflect.call(p, "readCaption", file));
            Files.writeString(file, "");
            ioFailure(() -> Reflect.call(p, "readCaption", file), 2);
            Files.writeString(file, "   \nMOON");
            ioFailure(() -> Reflect.call(p, "readCaption", file), 2);
            Files.writeString(file, "ABCDEFGHI");
            ioFailure(() -> Reflect.call(p, "readCaption", file), 2);
            ioFailure(() -> Reflect.call(p, "readCaption", root.resolve("missing.txt")), 2);
            Reflect.set(p, "caption", "JAVA");
            var first = (BufferedImage) Reflect.call(p, "render", 42L, 125.0);
            Reflect.set(p, "caption", "MOON");
            check(hash(first) != hash((BufferedImage) Reflect.call(p, "render", 42L, 125.0)), 1, "caption affects artwork");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L11.11: Unexpected failure while checking the active rule.", failure);
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

@SuppressWarnings("unchecked")
class M12_MagicEyeStripTest {
    static int checks;
    static final String[] CASES = {
        "Deník vybere poslední číselný řádek a přeskočí prázdné řádky i # komentáře.",
        "Zvolený seed zopakuje skutečný obrázek bez přepsání deníku.",
        "Překlepy, žádná data a chybějící soubor zůstanou jasnými chybami."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L11.12: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("notebook-check-");
        try {
            var p = Reflect.create("M12_MagicEyeStrip");
            Path file = root.resolve("seed.txt");
            Files.writeString(file, "  # first trial\n42\n\n # selected trial\n -7 \n");
            byte[] before = Files.readAllBytes(file);
            long selected;
            try {
                selected = (long) Reflect.call(p, "readSeed", file);
            } catch (NumberFormatException failure) {
                check(false, 0, "valid notebook rejected: " + failure.getMessage());
                return;
            }
            check(selected == -7, 0, selected);
            check(Arrays.equals(before, Files.readAllBytes(file)), 1, "notebook bytes");
            check(hash((BufferedImage) Reflect.call(p, "render", selected, 16.0)) == hash((BufferedImage) Reflect.call(p, "render", -7L, 16.0)), 1, "same dot field");
            Files.writeString(file, " 0 ");
            check((long) Reflect.call(p, "readSeed", file) == 0, 0, "zero is real data");
            Files.writeString(file, "# notes only\n  ");
            reject(() -> Reflect.call(p, "readSeed", file), 2);
            Files.writeString(file, "banana\n42");
            reject(() -> Reflect.call(p, "readSeed", file), 2);
            Files.writeString(file, "42 # not a whole-line comment");
            reject(() -> Reflect.call(p, "readSeed", file), 2);
            ioFailure(() -> Reflect.call(p, "readSeed", root.resolve("missing.txt")), 2);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

// Připravený přístup ke compact programům. Testy používají běžné assertions, pole a JDK collections.
class Reflect {
    static Class<?> type(String name) {
        return switch (name) {
            case "boolean" -> boolean.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "char" -> char.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "float" -> float.class;
            case "double" -> double.class;
            case "void" -> void.class;
            default -> load(name);
        };
    }

    private static Class<?> load(String name) {
        try {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException missing) {
                int nested = name.indexOf('$');
                if (nested < 0) throw missing;
                Class.forName(name.substring(0, nested));
                return Class.forName(name);
            }
        } catch (ClassNotFoundException failure) {
            throw new IllegalStateException("Cannot find compact program type: " + name, failure);
        }
    }

    static Object create(String name, Object... args) {
        var matches = new ArrayList<Constructor<?>>();
        for (var constructor : type(name).getDeclaredConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) matches.add(constructor);
        }
        var constructor = one(matches, "constructor " + name);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot construct " + name, failure);
        }
    }

    static Object get(Object target, String name) {
        try {
            return field(target, name).get(target instanceof Class<?> ? null : target);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot read field " + name, failure);
        }
    }

    static Object set(Object target, String name, Object value) {
        try {
            var field = field(target, name);
            Object receiver = target instanceof Class<?> ? null : target;
            field.set(receiver, value);
            return field.get(receiver);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot write field " + name, failure);
        }
    }

    private static Field field(Object target, String name) throws NoSuchFieldException {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            try {
                var field = current.getDeclaredField(name);
                if (target instanceof Class<?> && !Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("An instance is required for field " + name);
                }
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException missing) {
                // Continue with inherited fields.
            }
        }
        throw new NoSuchFieldException(owner.getName() + "." + name);
    }

    static Object call(Object target, String name, Object... args) {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        var methods = new LinkedHashMap<String, Method>();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            for (var method : current.getDeclaredMethods()) {
                methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
            }
        }
        for (var method : owner.getMethods()) {
            methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
        }
        var matches = new ArrayList<Method>();
        for (var method : methods.values()) {
            if (!method.isBridge() && !method.isSynthetic()
                    && method.getName().equals(name) && accepts(method.getParameterTypes(), args)
                    && (!(target instanceof Class<?>) || Modifier.isStatic(method.getModifiers()))) {
                matches.add(method);
            }
        }
        var method = one(matches, "method " + owner.getName() + "." + name);
        try {
            method.setAccessible(true);
            return method.invoke(target instanceof Class<?> ? null : target, args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot call " + owner.getName() + "." + name, failure);
        }
    }

    static Object array(String component, Object... values) {
        Object result = Array.newInstance(type(component), values.length);
        for (int index = 0; index < values.length; index++) Array.set(result, index, values[index]);
        return result;
    }

    private static boolean accepts(Class<?>[] parameters, Object[] args) {
        if (parameters.length != args.length) return false;
        for (int index = 0; index < parameters.length; index++) {
            Class<?> to = parameters[index];
            Object value = args[index];
            if (value == null) {
                if (to.isPrimitive()) return false;
            } else if (to.isPrimitive()) {
                String targets = switch (value) {
                    case Boolean ignored -> "boolean";
                    case Byte ignored -> "byte short int long float double";
                    case Short ignored -> "short int long float double";
                    case Character ignored -> "char int long float double";
                    case Integer ignored -> "int long float double";
                    case Long ignored -> "long float double";
                    case Float ignored -> "float double";
                    case Double ignored -> "double";
                    default -> "";
                };
                if (!Arrays.asList(targets.split(" ")).contains(to.getName())) return false;
            } else if (!to.isInstance(value)) return false;
        }
        return true;
    }

    private static <T> T one(List<T> matches, String description) {
        if (matches.size() != 1) {
            throw new IllegalStateException((matches.isEmpty() ? "No compatible " : "Ambiguous ") + description);
        }
        return matches.getFirst();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException raise(Throwable failure) throws T {
        throw (T) failure;
    }
}
