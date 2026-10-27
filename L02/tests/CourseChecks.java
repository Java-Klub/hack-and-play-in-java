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
        run("L02.01 CannonPuzzle · F04", () -> M01_CannonPuzzleTest.main(new String[0]));
        run("L02.02 MiniGolf · F04", () -> M02_MiniGolfTest.main(new String[0]));
        run("L02.03 BouncingBalls · F04", () -> M03_BouncingBallsTest.main(new String[0]));
        run("L02.04 Spirograph · F04", () -> M04_SpirographTest.main(new String[0]));
        run("L02.05 AnagramRounds · F05", () -> M05_AnagramRoundsTest.main(new String[0]));
        run("L02.06 MadLibsStoryMachine · F05", () -> M06_MadLibsStoryMachineTest.main(new String[0]));
        run("L02.07 Codebreaker · F06", () -> M07_CodebreakerTest.main(new String[0]));
        run("L02.08 PokerDice · F06", () -> M08_PokerDiceTest.main(new String[0]));
        run("L02.09 SonarTreasureHunt · F06", () -> M09_SonarTreasureHuntTest.main(new String[0]));
        run("L02.10 MagicNumberCards · F06", () -> M10_MagicNumberCardsTest.main(new String[0]));
        IO.println("10 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
