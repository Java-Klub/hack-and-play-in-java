import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_AsteroidDrift.java`
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
        run("M01_AsteroidDrift", "L10.01 AsteroidDrift · F28", () -> M01_AsteroidDriftTest.main(new String[0]));
        run("M02_FireworkPainter", "L10.02 FireworkPainter · F28", () -> M02_FireworkPainterTest.main(new String[0]));
        run("M03_ShadowPlayground", "L10.03 ShadowPlayground · F28", () -> M03_ShadowPlaygroundTest.main(new String[0]));
        run("M04_Untangle", "L10.04 Untangle · F29", () -> M04_UntangleTest.main(new String[0]));
        run("M05_SpringToy", "L10.05 SpringToy · F29", () -> M05_SpringToyTest.main(new String[0]));
        run("M06_MirrorArt", "L10.06 MirrorArt · F29", () -> M06_MirrorArtTest.main(new String[0]));
        run("M07_Moire", "L10.07 Moire · F29", () -> M07_MoireTest.main(new String[0]));
        run("M08_StarTunnel", "L10.08 StarTunnel · F29", () -> M08_StarTunnelTest.main(new String[0]));
        run("M09_Pong", "L10.09 Pong · F30", () -> M09_PongTest.main(new String[0]));
        run("M10_Breakout", "L10.10 Breakout · F30", () -> M10_BreakoutTest.main(new String[0]));
        run("M11_OrbitPainter", "L10.11 OrbitPainter · F30", () -> M11_OrbitPainterTest.main(new String[0]));
        run("M12_PendulumWaves", "L10.12 PendulumWaves · F30", () -> M12_PendulumWavesTest.main(new String[0]));
        run("M13_RippleTank", "L10.13 RippleTank · F30", () -> M13_RippleTankTest.main(new String[0]));
        run("M14_Snake", "L10.14 Snake · F30", () -> M14_SnakeTest.main(new String[0]));
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

class M01_AsteroidDriftTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) nevystřelí.",
        "Spuštění callback i skutečná držená klávesa vystřelí jednou.",
        "Cooldown i prohra dál brání nové střele."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I051: " + CASES[example] + " | actual=" + actual);
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
            var p = new M01_AsteroidDrift();
            p.restart();var action=p.firingAction();check(p.shots.isEmpty(),0,p.shots.size());
            action.run();check(p.shots.size()==1&&p.cooldown==10,1,p.shots.size());
            action.run();check(p.shots.size()==1,2,p.shots.size());
            p.restart();p.key(java.awt.event.KeyEvent.VK_SPACE,true);p.step();check(p.shots.size()==1,1,p.shots.size());
            p.lost=true;p.cooldown=0;p.firingAction().run();check(p.shots.size()==1,2,p.shots.size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I051: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_FireworkPainterTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) nevytvoří částice.",
        "Spuštění vytvoří šedesát částic v zadaných nestejných coordinate (souřadnicích).",
        "Skutečný press přidá další výbuch a restart vše smaže."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I072: " + CASES[example] + " | actual=" + actual);
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
            var p = new M02_FireworkPainter();
            p.restart();var action=p.burstAction();check(p.particles.isEmpty(),0,p.particles.size());
            action.accept(43,91);check(p.particles.size()==60&&!p.paused,1,p.particles.size());
            check(p.particles.getFirst().x()==43&&p.particles.getFirst().y()==91,1,p.particles.getFirst());
            p.press(120,80,1);check(p.particles.size()==120&&p.particles.getLast().x()==120,2,p.particles.size());
            p.restart();check(p.particles.isEmpty(),2,p.particles.size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I072: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_ShadowPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) zachová počáteční pozici světla.",
        "Spuštění i skutečné press/drag použijí zadané x/y.",
        "Coordinate (souřadnice) mimo plátno omezí moveLight."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I078: " + CASES[example] + " | actual=" + actual);
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
            var p = new M03_ShadowPlayground();
            p.restart();var action=p.lightAction();check(p.lightX==100&&p.lightY==80,0,p.lightX);
            action.accept(43,91);check(p.lightX==43&&p.lightY==91,1,p.lightX+","+p.lightY);
            action.accept(-20,999);check(p.lightX==0&&p.lightY==p.HEIGHT,2,p.lightX+","+p.lightY);
            p.press(140,70,1);p.drag(160,90);check(p.lightX==160&&p.lightY==90,1,p.lightX+","+p.lightY);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I078: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_UntangleTest {
    static int checks;
    static final String[] CASES = {
        "Nestejný bod vykreslí správně vystředěný uzel.",
        "Kreslení nezmění Point ani model (model pravidel).",
        "Skutečná celá scéna používá opravený helper (pomocnou funkci)."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I056: " + CASES[example] + " | actual=" + actual);
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
            var p = new M04_Untangle();
            p.restart();var point=new Point(120,70);var image=draw(g->p.drawNode(g,point,3),600,420);
            check((image.getRGB(128,70)&0xFFFFFF)==0x23375A,0,Integer.toHexString(image.getRGB(128,70)));
            check(point.equals(new Point(120,70)),1,point);
            p.points[0]=new Point(80,90);var scene=draw(p::draw,600,420);
            check((scene.getRGB(88,90)&0xFFFFFF)==0x23375A,2,Integer.toHexString(scene.getRGB(88,90)));
            var copy=p.points[0].getLocation();draw(p::draw,600,420);check(p.points[0].equals(copy),1,p.points[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I056: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_SpringToyTest {
    static int checks;
    static final String[] CASES = {
        "Rendering (vykreslení) zachová všechny hodnoty pohybu.",
        "Opakované kreslení dá stejné pixel (body obrazu) a nepřidá stopu.",
        "Výslovný step model dál posouvá."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I070: " + CASES[example] + " | actual=" + actual);
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
            var p = new M05_SpringToy();
            p.restart();p.paused=false;double x=p.x,y=p.y,vx=p.vx,vy=p.vy;
            var first=draw(p::draw,p.WIDTH,p.HEIGHT);
            check(p.x==x&&p.y==y&&p.vx==vx&&p.vy==vy,0,p.y);
            check(p.trail.isEmpty(),1,p.trail.size());
            var second=draw(p::draw,p.WIDTH,p.HEIGHT);check(hash(first)==hash(second),1,hash(second));
            p.step();check(p.y!=y&&!p.trail.isEmpty(),2,p.y);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I070: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_MirrorArtTest {
    static int checks;
    static final String[] CASES = {
        "Běžný nestejný bod má popsanou pozici na obrazovce.",
        "Střed/otočení či hranice hloubky zůstanou správné.",
        "Skutečné kreslení používá transformaci a zachová state (stav) modelu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I074: " + CASES[example] + " | actual=" + actual);
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
            var p = new M06_MirrorArt();
            p.restart();var c=p.transform(320,220,0,false);check(c.distance(320,220)<1e-9,0,c);p.axes=4;var q=p.transform(350,220,1,false);check(q.distance(320,250)<1e-9,1,q);var r=p.transform(320,250,0,true);check(r.distance(320,190)<1e-9,1,r);long before=hash(p.canvas);p.stroke(350,220,360,230);check(hash(p.canvas)!=before,2,hash(p.canvas));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I074: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_MoireTest {
    static int checks;
    static final String[] CASES = {
        "Běžný nestejný bod má popsanou pozici na obrazovce.",
        "Střed/otočení či hranice hloubky zůstanou správné.",
        "Skutečné kreslení používá transformaci a zachová state (stav) modelu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I076: " + CASES[example] + " | actual=" + actual);
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
            var p = new M07_Moire();
            p.restart();var c=p.gridPoint(0,0,0);check(c.distance(320,220)<1e-9,0,c);var q=p.gridPoint(1,0,Math.PI/2);check(q.distance(320,242)<1e-8,1,q);double old=p.angle;var a=draw(p::draw,p.WIDTH,p.HEIGHT);var b=draw(p::draw,p.WIDTH,p.HEIGHT);check(p.angle==old&&hash(a)==hash(b),2,p.angle);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I076: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_StarTunnelTest {
    static int checks;
    static final String[] CASES = {
        "Běžný nestejný bod má popsanou pozici na obrazovce.",
        "Střed/otočení či hranice hloubky zůstanou správné.",
        "Skutečné kreslení používá transformaci a zachová state (stav) modelu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I077: " + CASES[example] + " | actual=" + actual);
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
            var p = new M08_StarTunnel();
            p.restart();var star=new M08_StarTunnel.Star(10,20,100);var q=p.project(star,100);check(q.distance(p.centreX+26,p.centreY+52)<1e-9,0,q);var near=p.project(star,0);check(near.distance(p.centreX+520,p.centreY+1040)<1e-9,1,near);var before=List.copyOf(p.stars);draw(p::draw,p.WIDTH,p.HEIGHT);check(p.stars.equals(before),2,p.stars.size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I077: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_PongTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I047: " + CASES[example] + " | actual=" + actual);
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
            var p = new M09_Pong();
            p.restart();double x=p.ballX,y=p.ballY;p.step();check(p.ballX==x+4&&p.ballY==y+2.5,0,p.ballX);p.winner=1;x=p.ballX;y=p.ballY;p.leftDown=true;p.step();check(p.ballX==x&&p.ballY==y&&p.leftY==160,1,p.ballX);p.clearKeys();check(!p.leftDown&&!p.leftUp&&!p.rightUp&&!p.rightDown,2,p.leftDown);p.restart();check(p.winner==0&&p.leftScore==0&&p.rightScore==0,2,p.winner);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I047: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_BreakoutTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I048: " + CASES[example] + " | actual=" + actual);
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
            var p = new M10_Breakout();
            p.restart();double x=p.ballX,y=p.ballY;p.step();check(Math.abs(p.ballX-x-3.2)<1e-9&&Math.abs(p.ballY-y+3.5)<1e-9,0,p.ballX);p.state="GAME OVER";x=p.ballX;y=p.ballY;p.step();check(p.ballX==x&&p.ballY==y&&p.lives==3,1,p.ballY);p.state="YOU WIN";p.step();check(p.ballX==x&&p.ballY==y,1,p.ballX);p.restart();check(p.state.equals("PLAY")&&p.lives==3&&p.score==0,2,p.state);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I048: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M11_OrbitPainterTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I068: " + CASES[example] + " | actual=" + actual);
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
            var p = new M11_OrbitPainter();
            p.restart();double x=p.x,y=p.y;p.step();check(p.x==x&&p.y==y&&p.ticks==0,0,p.ticks);p.paused=false;p.step();check(p.ticks==1&&(p.x!=x||p.y!=y),1,p.ticks);p.paused=true;x=p.x;y=p.y;long ink=hash(p.canvas);p.step();check(p.x==x&&p.y==y&&hash(p.canvas)==ink,0,p.ticks);p.restart();check(p.paused&&p.ticks==0&&p.x==440,2,p.x);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I068: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M12_PendulumWavesTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I071: " + CASES[example] + " | actual=" + actual);
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
            var p = new M12_PendulumWaves();
            p.restart();p.step();check(p.time==0,0,p.time);p.paused=false;p.speed=2;p.step();check(Math.abs(p.time-.04)<1e-12,1,p.time);p.adjust(100);check(p.speed==2,2,p.speed);p.adjust(-100);check(p.speed==.25,2,p.speed);p.restart();check(p.time==0&&p.paused&&p.speed==1,2,p.time);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I071: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M13_RippleTankTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I073: " + CASES[example] + " | actual=" + actual);
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
            var p = new M13_RippleTank();
            p.restart();p.tap(20,10);check(!p.paused&&p.height[10][20]==60,0,p.height[10][20]);p.step();check(p.previous[10][20]==60&&Math.abs(p.height[10][21]-29.76)<1e-9,1,p.height[10][21]);p.restart();p.tap(0,0);check(p.paused&&p.energy()==0,2,p.energy());p.tap(20,10);p.paused=true;double energy=p.energy();p.step();check(p.energy()==energy,2,p.energy());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I073: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M14_SnakeTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I046: " + CASES[example] + " | actual=" + actual);
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
            var p = new M14_Snake();
            p.restart();var head=p.body.getFirst();int dx=p.dx,dy=p.dy;p.turn(0,-1);check(p.dx==dx&&p.dy==dy&&p.body.getFirst().equals(head),0,p.body.getFirst());p.turn(-1,0);p.step();check(p.body.getFirst().equals(new M14_Snake.Cell(head.x(),head.y()-1)),1,p.body.getFirst());p.lost=true;var stopped=List.copyOf(p.body);p.step();check(p.body.equals(stopped),2,p.body);p.restart();check(!p.lost&&p.score==0,2,p.score);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I046: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
