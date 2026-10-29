import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_ReactionDiffusionSkins.java`
/// Checks call the test methods directly in this JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_ReactionDiffusionSkins", "L12.01 ReactionDiffusionSkins · F34", () -> M01_ReactionDiffusionSkinsTest.main(new String[0]));
        run("M02_FlockPlayground", "L12.02 FlockPlayground · F34", () -> M02_FlockPlaygroundTest.main(new String[0]));
        run("M03_PhasingRhythms", "L12.03 PhasingRhythms · F34", () -> M03_PhasingRhythmsTest.main(new String[0]));
        run("M04_PendulumPainter", "L12.04 PendulumPainter · F34", () -> M04_PendulumPainterTest.main(new String[0]));
        run("M05_KnightTour", "L12.05 KnightTour · F35", () -> M05_KnightTourTest.main(new String[0]));
        run("M06_CipherPostcards", "L12.06 CipherPostcards · F35", () -> M06_CipherPostcardsTest.main(new String[0]));
        run("M07_RetroSoundForge", "L12.07 RetroSoundForge · F35", () -> M07_RetroSoundForgeTest.main(new String[0]));
        run("M08_FireflySynchrony", "L12.08 FireflySynchrony · F35", () -> M08_FireflySynchronyTest.main(new String[0]));
        run("M09_CrystalGrowth", "L12.09 CrystalGrowth · F36", () -> M09_CrystalGrowthTest.main(new String[0]));
        run("M10_PictureMusic", "L12.10 PictureMusic · F36", () -> M10_PictureMusicTest.main(new String[0]));
        run("M11_SecretBeeps", "L12.11 SecretBeeps · F36", () -> M11_SecretBeepsTest.main(new String[0]));
        run("M12_MountainPostcards", "L12.12 MountainPostcards · F36", () -> M12_MountainPostcardsTest.main(new String[0]));
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

class M01_ReactionDiffusionSkinsTest {
    static int checks;
    static final String[] CASES = {
        "Default chemistry receives the real current cell values.",
        "Replacing the interface implementation changes the result.",
        "The actual step uses the replacement reaction in both next buffers."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I126: " + CASES[example] + " | actual=" + actual);
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
            var p = new M01_ReactionDiffusionSkins();
            p.reset(42);p.a[0][0]=.5;p.b[0][0]=.4;check(Math.abs(p.reactionAt(0,0)-.08)<1e-12,0,p.reactionAt(0,0));
            p.a[3][2]=.7;p.b[3][2]=.2;check(Math.abs(p.reactionAt(2,3)-.028)<1e-12,0,p.reactionAt(2,3));
            p.chemistry=(a,b)->.1*a+.05*b;check(Math.abs(p.reactionAt(2,3)-.08)<1e-12,1,p.reactionAt(2,3));
            double expectedA=Math.clamp(p.a[3][2]+p.laplace(p.a,2,3)-.08+.055*(1-p.a[3][2]),0,1);
            double expectedB=Math.clamp(p.b[3][2]+.5*p.laplace(p.b,2,3)+.08-.117*p.b[3][2],0,1);
            p.step();check(Math.abs(p.a[3][2]-expectedA)<1e-12&&Math.abs(p.b[3][2]-expectedB)<1e-12,2,p.b[3][2]);
            p.chemistry=(a,b)->.07;check(Math.abs(p.reactionAt(0,0)-.07)<1e-12,1,p.reactionAt(0,0));
            for(var row:p.a)Arrays.fill(row,.5);for(var row:p.b)Arrays.fill(row,.4);p.step();
            check(Math.abs(p.a[0][0]-.4575)<1e-9&&Math.abs(p.b[0][0]-.4232)<1e-9,2,p.b[0][0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I126: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_FlockPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Default strength respects the distance boundary.",
        "A replacement role is observed by obstaclePush.",
        "The real bird step changes when the role changes."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I128: " + CASES[example] + " | actual=" + actual);
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
            var p = new M02_FlockPlayground();
            p.reset(42);check(p.obstaclePush(20)==.2&&p.obstaclePush(75)==0,0,p.obstaclePush(20));
            p.avoidance=d->d/100;check(p.obstaclePush(35)==.35&&p.obstaclePush(90)==.9,1,p.obstaclePush(35));
            p.avoidance=d->.7;check(p.obstaclePush(20)==.7,1,p.obstaclePush(20));
            var other=new M02_FlockPlayground();other.reset(42);other.avoidance=d->0;
            p.x[0]=other.x[0]=310;p.y[0]=other.y[0]=200;p.vx[0]=other.vx[0]=0;p.vy[0]=other.vy[0]=0;
            p.step();other.step();check(Math.abs(p.vx[0]-other.vx[0])>1e-6,2,p.vx[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I128: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_PhasingRhythmsTest {
    static int checks;
    static final String[] CASES = {
        "Both role results contribute with gain 0.17.",
        "Opposite replacement voices cancel.",
        "Actual synthesis uses the composition and keeps duration guards."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I133: " + CASES[example] + " | actual=" + actual);
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
            var p = new M03_PhasingRhythms();
            p.first=t->.2;p.second=t->.5;check(Math.abs(p.mix(.4)-.119)<1e-12,0,p.mix(.4));
            p.first=t->t+.2;p.second=t->.5-2*t;
            check(Math.abs(p.mix(.4)-.051)<1e-12&&Math.abs(p.mix(.8)+.017)<1e-12,0,p.mix(.8));
            p.first=t->-.2;p.second=t->.2;check(Math.abs(p.mix(.8))<1e-12,1,p.mix(.8));
            p.first=t->.25;p.second=t->0;var samples=p.signal(1);check(samples.length==22050&&Math.abs(samples[100]-.0425)<1e-12,2,samples[100]);
            p.first=t->t*.25;p.second=t->-t*.1;samples=p.signal(1);
            check(Math.abs(samples[100]-.17*.15*100/22050)<1e-12&&Math.abs(samples[1000]-.17*.15*1000/22050)<1e-12,2,samples[1000]);
            reject(()->p.signal(.5),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I133: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_PendulumPainterTest {
    static int checks;
    static final String[] CASES = {
        "The default role sees current angle, length and gravity.",
        "A constant alternate acceleration changes the actual velocity.",
        "A zero alternate rule keeps an initially resting angle fixed."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I138: " + CASES[example] + " | actual=" + actual);
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
            var p = new M04_PendulumPainter();
            p.reset(42);double expected=-p.gravity/p.length*Math.sin(p.angle);check(Math.abs(p.accelerationNow()-expected)<1e-12,0,p.accelerationNow());
            p.angle=.4;p.length=120;expected=-p.gravity/p.length*Math.sin(p.angle);check(Math.abs(p.accelerationNow()-expected)<1e-12,0,p.accelerationNow());
            p.acceleration=(angle,length)->angle+length/100;
            check(Math.abs(p.accelerationNow()-1.6)<1e-12,1,p.accelerationNow());
            p.angle=-.2;p.length=50;check(Math.abs(p.accelerationNow()-.3)<1e-12,1,p.accelerationNow());
            p.step();check(Math.abs(p.velocity-.3*.03*.999)<1e-12,1,p.velocity);
            p.reset(42);
            p.acceleration=(angle,length)->2;p.step();check(Math.abs(p.velocity-.05994)<1e-12,1,p.velocity);
            p.acceleration=(angle,length)->0;p.reset(42);double before=p.angle;p.step();check(p.angle==before&&p.ticks==1,2,p.angle);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I138: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_KnightTourTest {
    static int checks;
    static final String[] CASES = {
        "Move preserves the record component order.",
        "Further legal moves advance count once.",
        "Bad coordinates preserve state and Restart restores the initial board."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I096: " + CASES[example] + " | actual=" + actual);
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
            var p = new M05_KnightTour();
            p.restart();check(p.apply(new M05_KnightTour.Move(1,2))&&p.x==1&&p.y==2,0,p.x+","+p.y);
            check(p.apply(new M05_KnightTour.Move(3,3))&&p.count==3,1,p.count);
            int x=p.x,y=p.y;check(!p.apply(new M05_KnightTour.Move(99,0))&&p.x==x&&p.y==y,2,p.count);
            check(p.apply(new M05_KnightTour.Restart())&&p.count==1&&p.x==0&&p.y==0,2,p.count);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I096: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_CipherPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "LEMON transforms ATTACK AT DAWN! into LXFOPV EF RNHR!.",
        "Decoding uses the same carried keyword and restores the text.",
        "Other alternatives, punctuation and invalid keys retain their contracts."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I131: " + CASES[example] + " | actual=" + actual);
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
            var p = new M06_CipherPostcards();
            var cipher=new M06_CipherPostcards.Vigenere("LEMON");String coded=p.transform("ATTACK AT DAWN!",cipher,false);
            check(coded.equals("LXFOPV EF RNHR!"),0,coded);
            check(p.transform(coded,cipher,true).equals("ATTACK AT DAWN!"),1,coded);
            var orange=new M06_CipherPostcards.Vigenere("ORANGE");String other=p.transform("ATTACK AT DAWN!",orange,false);
            check(other.equals("OKTNIO OK DNCR!")&&p.transform(other,orange,true).equals("ATTACK AT DAWN!"),1,other);
            check(p.transform("AbZ",new M06_CipherPostcards.Caesar(3),false).equals("DeC"),2,"Caesar");
            check(p.transform("A!",new M06_CipherPostcards.Substitution("ZYXWVUTSRQPONMLKJIHGFEDCBA"),false).equals("Z!"),2,"Substitution");
            reject(()->p.transform("A",new M06_CipherPostcards.Vigenere("123"),false),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I131: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_RetroSoundForgeTest {
    static int checks;
    static final String[] CASES = {
        "Wobble uses arbitrary carried base and depth values.",
        "Jump and Laser retain their independent formulas.",
        "Invalid time is rejected and real sound presets differ."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I132: " + CASES[example] + " | actual=" + actual);
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
            var p = new M07_RetroSoundForge();
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Wobble(300,90),1.0/32)-390)<1e-9,0,p.frequency(new M07_RetroSoundForge.Wobble(300,90),1.0/32));
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Wobble(300,90),3.0/32)-210)<1e-9,0,p.frequency(new M07_RetroSoundForge.Wobble(300,90),3.0/32));
            check(p.frequency(new M07_RetroSoundForge.Wobble(410,25),0)==410,0,p.frequency(new M07_RetroSoundForge.Wobble(410,25),0));
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Jump(100,200),.5)-200)<1e-9,1,"jump");
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Laser(100,.25),1)-25)<1e-9,1,"laser");
            reject(()->p.frequency(new M07_RetroSoundForge.Wobble(300,90),Double.NaN),2);
            check(!Arrays.equals(p.signal("wobble",.1),p.signal("laser",.1)),2,"different waves");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I132: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_FireflySynchronyTest {
    static int checks;
    static final String[] CASES = {
        "A valid Coupling value reaches the model.",
        "Both the upper boundary and invalid-value preservation work.",
        "Toggle and seeded Restart dispatch to the correct independent rules."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I139: " + CASES[example] + " | actual=" + actual);
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
            var p = new M08_FireflySynchrony();
            p.reset(42);p.apply(new M08_FireflySynchrony.Coupling(.35));check(p.coupling==.35,0,p.coupling);
            p.apply(new M08_FireflySynchrony.Coupling(2));check(p.coupling==2,1,p.coupling);
            p.apply(new M08_FireflySynchrony.Coupling(2.1));p.apply(new M08_FireflySynchrony.Coupling(Double.NaN));check(p.coupling==2,1,p.coupling);
            for(double invalid:new double[]{-.1,2.1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.NaN}){
                p.apply(new M08_FireflySynchrony.Coupling(.35));p.apply(new M08_FireflySynchrony.Coupling(invalid));check(p.coupling==.35,1,p.coupling);
            }
            p.apply(new M08_FireflySynchrony.Coupling(0));check(p.coupling==0,1,p.coupling);
            boolean run=p.running;p.apply(new M08_FireflySynchrony.Toggle());check(p.running!=run,2,p.running);
            var expected=new M08_FireflySynchrony();expected.reset(17);p.apply(new M08_FireflySynchrony.Restart(17));check(Arrays.equals(p.phase,expected.phase),2,p.phase[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I139: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_CrystalGrowthTest {
    static int checks;
    static final String[] CASES = {
        "The added mode sees a diagonal-only neighbour.",
        "The old four-neighbour mode still excludes diagonals and accepts orthogonal contact.",
        "Edges/empty data are safe and real crystal growth still attaches cells."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I127: " + CASES[example] + " | actual=" + actual);
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
            var p = new M09_CrystalGrowth();
            boolean[][] grid=new boolean[3][3];grid[0][0]=true;
            check(p.touching(grid,1,1,true),0,"diagonal");check(!p.touching(grid,1,1,false),1,"old mode");
            grid[0][1]=true;check(p.touching(grid,1,1,false),1,"orthogonal");
            check(p.touching(grid,0,1,true),2,"edge");check(!p.touching(new boolean[0][0],0,0,true),2,"empty");
            p.diagonalGrowth=true;var grown=p.grow(42,20);int cells=0;for(var row:grown)for(boolean cell:row)if(cell)cells++;check(cells>1&&cells<=21,2,cells);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I127: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_PictureMusicTest {
    static int checks;
    static final String[] CASES = {
        "The new +12 shift doubles frequency and changes actual samples.",
        "Zero and -12 retain the old mapping and octave relation.",
        "Invalid rows and shifts are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I134: " + CASES[example] + " | actual=" + actual);
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
            var p = new M10_PictureMusic();
            double base=p.pitchFrequency(4,0);check(Math.abs(p.pitchFrequency(4,12)-base*2)<1e-9,0,p.pitchFrequency(4,12));
            check(Math.abs(p.pitchFrequency(4,-12)-base/2)<1e-9,1,p.pitchFrequency(4,-12));
            check(Math.abs(p.pitchFrequency(11,0)-220)<1e-9,1,p.pitchFrequency(11,0));
            reject(()->p.pitchFrequency(12,0),2);reject(()->p.pitchFrequency(0,13),2);
            var image=p.demo();p.transpose=0;var a=p.signal(image);p.transpose=12;check(!Arrays.equals(a,p.signal(image)),0,"transposed WAV");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I134: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M11_SecretBeepsTest {
    static int checks;
    static final String[] CASES = {
        "The new word gap changes the actual waveform length.",
        "The original seven-unit mode and three-unit letter gap remain unchanged.",
        "Word-gap values outside 3–12 are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I143: " + CASES[example] + " | actual=" + actual);
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
            var p = new M11_SecretBeeps();
            check(p.units("E E",10).equals(List.of(1,-10,1)),0,p.units("E E",10));
            check(p.units("E E").equals(List.of(1,-7,1))&&p.units("EE",10).equals(List.of(1,-3,1)),1,p.units("EE",10));
            reject(()->p.units("E E",2),2);reject(()->p.units("E E",13),2);
            p.wordGap=7;int original=p.signal("E E",20).length;p.wordGap=10;check(p.signal("E E",20).length-original==3*(22050*20/1000),0,p.signal("E E",20).length);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I143: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

class M12_MountainPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "The new transform reverses order and changes the real image.",
        "The returned array is independent of the original.",
        "Empty and one-element ridges preserve their meaning."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I140: " + CASES[example] + " | actual=" + actual);
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
            var p = new M12_MountainPostcards();
            double[] input={3,7,2};var result=p.reverseRidge(input);check(Arrays.equals(result,new double[]{2,7,3}),0,Arrays.toString(result));
            result[0]=99;check(Arrays.equals(input,new double[]{3,7,2}),1,Arrays.toString(input));
            check(p.reverseRidge(new double[0]).length==0&&Arrays.equals(p.reverseRidge(new double[]{4}),new double[]{4}),2,"bounds");
            p.mirrored=false;var original=p.render(42,.65);p.mirrored=true;check(hash(original)!=hash(p.render(42,.65)),0,"mirrored image");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I140: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
