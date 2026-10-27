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
        run("L08.01 Minesweeper · F18", () -> M01_MinesweeperTest.main(new String[0]));
        run("L08.02 Net · F18", () -> M02_NetTest.main(new String[0]));
        run("L08.03 BlackBox · F18", () -> M03_BlackBoxTest.main(new String[0]));
        run("L08.04 LightUp · F18", () -> M04_LightUpTest.main(new String[0]));
        run("L08.05 LoopBuilder · F18", () -> M05_LoopBuilderTest.main(new String[0]));
        run("L08.06 AntHighway · F18", () -> M06_AntHighwayTest.main(new String[0]));
        run("L08.07 Bridges · F20", () -> M07_BridgesTest.main(new String[0]));
        run("L08.08 MagnetDominoes · F20", () -> M08_MagnetDominoesTest.main(new String[0]));
        run("L08.09 Shikaku · F20", () -> M09_ShikakuTest.main(new String[0]));
        run("L08.10 Slant · F20", () -> M10_SlantTest.main(new String[0]));
        run("L08.11 BinaryBalance · F20", () -> M11_BinaryBalanceTest.main(new String[0]));
        run("L08.12 RuleGarden · F26", () -> M12_RuleGardenTest.main(new String[0]));
        run("L08.13 ForestFireSandbox · F26", () -> M13_ForestFireSandboxTest.main(new String[0]));
        IO.println("13 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
