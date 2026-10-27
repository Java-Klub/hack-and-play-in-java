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
        run("L05.01 MemoryPairs · F16", () -> M01_MemoryPairsTest.main(new String[0]));
        run("L05.02 MiniBattleshipHunt · F16", () -> M02_MiniBattleshipHuntTest.main(new String[0]));
        run("L05.03 DotsAndBoxes · F16", () -> M03_DotsAndBoxesTest.main(new String[0]));
        run("L05.04 Quarto · F16", () -> M04_QuartoTest.main(new String[0]));
        run("L05.05 Reversi · F17", () -> M05_ReversiTest.main(new String[0]));
        run("L05.06 Hex · F17", () -> M06_HexTest.main(new String[0]));
        run("L05.07 SetStyleTripleHunt · F17", () -> M07_SetStyleTripleHuntTest.main(new String[0]));
        run("L05.08 Nonogram · F17", () -> M08_NonogramTest.main(new String[0]));
        run("L05.09 FourColourMap · F17", () -> M09_FourColourMapTest.main(new String[0]));
        run("L05.10 MirrorLaser · F19", () -> M10_MirrorLaserTest.main(new String[0]));
        run("L05.11 Dominosa · F19", () -> M11_DominosaTest.main(new String[0]));
        run("L05.12 InequalityLatin · F19", () -> M12_InequalityLatinTest.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
