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
        run("L11.01 TinyStepSequencer · F23", () -> M01_TinyStepSequencerTest.main(new String[0]));
        run("L11.02 CirclePacking · F23", () -> M02_CirclePackingTest.main(new String[0]));
        run("L11.03 WireframeCube · F23", () -> M03_WireframeCubeTest.main(new String[0]));
        run("L11.04 MandelbrotPostcards · F27", () -> M04_MandelbrotPostcardsTest.main(new String[0]));
        run("L11.05 ChaosGame · F27", () -> M05_ChaosGameTest.main(new String[0]));
        run("L11.06 ColourFilterSecrets · F27", () -> M06_ColourFilterSecretsTest.main(new String[0]));
        run("L11.07 TwoNoisyPictures · F27", () -> M07_TwoNoisyPicturesTest.main(new String[0]));
        run("L11.08 EndlessToneStaircase · F32", () -> M08_EndlessToneStaircaseTest.main(new String[0]));
        run("L11.09 PixelSortingGlitch · F32", () -> M09_PixelSortingGlitchTest.main(new String[0]));
        run("L11.10 SecretPNGPostcard · F32", () -> M10_SecretPNGPostcardTest.main(new String[0]));
        run("L11.11 MirrorCylinderPicture · F32", () -> M11_MirrorCylinderPictureTest.main(new String[0]));
        run("L11.12 MagicEyeStrip · F32", () -> M12_MagicEyeStripTest.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
