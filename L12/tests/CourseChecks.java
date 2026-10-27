// Build support: aggregates independent functional checks; no game rules live here.
class CourseChecks {
    interface Check { void run() throws Exception; }
    static int failed;
    static void run(String label, Check check) {
        try {
            check.run();
            IO.println("GREEN " + label);
        } catch (Throwable problem) {
            failed++;
            IO.println("RED " + label + ": " + problem.getMessage());
        }
    }
    public static void main(String[] args) {
        run("L12.01 ReactionDiffusionSkins · F34", () -> M01_ReactionDiffusionSkinsTest.main(new String[0]));
        run("L12.02 FlockPlayground · F34", () -> M02_FlockPlaygroundTest.main(new String[0]));
        run("L12.03 PhasingRhythms · F34", () -> M03_PhasingRhythmsTest.main(new String[0]));
        run("L12.04 PendulumPainter · F34", () -> M04_PendulumPainterTest.main(new String[0]));
        run("L12.05 KnightTour · F35", () -> M05_KnightTourTest.main(new String[0]));
        run("L12.06 CipherPostcards · F35", () -> M06_CipherPostcardsTest.main(new String[0]));
        run("L12.07 RetroSoundForge · F35", () -> M07_RetroSoundForgeTest.main(new String[0]));
        run("L12.08 FireflySynchrony · F35", () -> M08_FireflySynchronyTest.main(new String[0]));
        run("L12.09 CrystalGrowth · F36", () -> M09_CrystalGrowthTest.main(new String[0]));
        run("L12.10 PictureMusic · F36", () -> M10_PictureMusicTest.main(new String[0]));
        run("L12.11 SecretBeeps · F36", () -> M11_SecretBeepsTest.main(new String[0]));
        run("L12.12 MountainPostcards · F36", () -> M12_MountainPostcardsTest.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
