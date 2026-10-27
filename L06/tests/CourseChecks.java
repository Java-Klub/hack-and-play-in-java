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
        run("L06.01 ConnectFour · F14", () -> M01_ConnectFourTest.main(new String[0]));
        run("L06.02 MancalaKalah · F14", () -> M02_MancalaKalahTest.main(new String[0]));
        run("L06.03 SlidingTiles · F14", () -> M03_SlidingTilesTest.main(new String[0]));
        run("L06.04 MiniCrossword · F14", () -> M04_MiniCrosswordTest.main(new String[0]));
        run("L06.05 Sokoban · F15", () -> M05_SokobanTest.main(new String[0]));
        run("L06.06 PegDrop · F15", () -> M06_PegDropTest.main(new String[0]));
        run("L06.07 TrafficJamEscape · F15", () -> M07_TrafficJamEscapeTest.main(new String[0]));
        run("L06.08 FallingBlocks · F15", () -> M08_FallingBlocksTest.main(new String[0]));
        run("L06.09 LightsOut · F33", () -> M09_LightsOutTest.main(new String[0]));
        run("L06.10 Tents · F33", () -> M10_TentsTest.main(new String[0]));
        run("L06.11 PegSolitaire · F33", () -> M11_PegSolitaireTest.main(new String[0]));
        run("L06.12 Merge2048 · F33", () -> M12_Merge2048Test.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
