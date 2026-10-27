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
        run("L03.01 Nim · F07", () -> M01_NimTest.main(new String[0]));
        run("L03.02 Pig · F07", () -> M02_PigTest.main(new String[0]));
        run("L03.03 BlackjackTwentyOne · F07", () -> M03_BlackjackTwentyOneTest.main(new String[0]));
        run("L03.04 TicTacToe · F08", () -> M04_TicTacToeTest.main(new String[0]));
        run("L03.05 Chomp · F08", () -> M05_ChompTest.main(new String[0]));
        run("L03.06 Chopsticks · F08", () -> M06_ChopsticksTest.main(new String[0]));
        run("L03.07 Hanoi · F08", () -> M07_HanoiTest.main(new String[0]));
        run("L03.08 Sudoku · F08", () -> M08_SudokuTest.main(new String[0]));
        run("L03.09 SOS · F13", () -> M09_SOSTest.main(new String[0]));
        run("L03.10 ShutTheBox · F13", () -> M10_ShutTheBoxTest.main(new String[0]));
        run("L03.11 Hexapawn · F13", () -> M11_HexapawnTest.main(new String[0]));
        run("L03.12 ToadsAndFrogs · F13", () -> M12_ToadsAndFrogsTest.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
