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
        run("L09.01 WordLadder · F22", () -> M01_WordLadderTest.main(new String[0]));
        run("L09.02 GapHopper · F22", () -> M02_GapHopperTest.main(new String[0]));
        run("L09.03 FlipbookStudio · F22", () -> M03_FlipbookStudioTest.main(new String[0]));
        run("L09.04 VoronoiStainedGlass · F22", () -> M04_VoronoiStainedGlassTest.main(new String[0]));
        run("L09.05 PhotoRemixLab · F24", () -> M05_PhotoRemixLabTest.main(new String[0]));
        run("L09.06 TruchetTileLab · F24", () -> M06_TruchetTileLabTest.main(new String[0]));
        run("L09.07 MarkovMischief · F24", () -> M07_MarkovMischiefTest.main(new String[0]));
        run("L09.08 DungeonBlueprints · F24", () -> M08_DungeonBlueprintsTest.main(new String[0]));
        run("L09.09 FractalTreeGarden · F31", () -> M09_FractalTreeGardenTest.main(new String[0]));
        run("L09.10 ImageToASCII · F31", () -> M10_ImageToASCIITest.main(new String[0]));
        run("L09.11 MazePostcards · F31", () -> M11_MazePostcardsTest.main(new String[0]));
        run("L09.12 TileStampStudio · F31", () -> M12_TileStampStudioTest.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
