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
        run("L01.01 CafeWallLab · F01", () -> M01_CafeWallLabTest.main(new String[0]));
        run("L01.02 MissingTriangle · F01", () -> M02_MissingTriangleTest.main(new String[0]));
        run("L01.03 TypeMelody · F02", () -> M03_TypeMelodyTest.main(new String[0]));
        run("L01.04 BitmapBanner · F02", () -> M04_BitmapBannerTest.main(new String[0]));
        run("L01.05 ProgramMirror · F02", () -> M05_ProgramMirrorTest.main(new String[0]));
        run("L01.06 HigherOrLower · F03", () -> M06_HigherOrLowerTest.main(new String[0]));
        run("L01.07 SteppingFeet · F03", () -> M07_SteppingFeetTest.main(new String[0]));
        run("L01.08 SunflowerSpiral · F03", () -> M08_SunflowerSpiralTest.main(new String[0]));
        run("L01.09 PixelMonsters · F03", () -> M09_PixelMonstersTest.main(new String[0]));
        IO.println("9 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
