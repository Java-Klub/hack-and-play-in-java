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
        run("L04.01 Hangman · F10", () -> M01_HangmanTest.main(new String[0]));
        run("L04.02 Ghost · F10", () -> M02_GhostTest.main(new String[0]));
        run("L04.03 Flood · F10", () -> M03_FloodTest.main(new String[0]));
        run("L04.04 SameGame · F10", () -> M04_SameGameTest.main(new String[0]));
        run("L04.05 WordSearch · F11", () -> M05_WordSearchTest.main(new String[0]));
        run("L04.06 VirtualScribbler · F11", () -> M06_VirtualScribblerTest.main(new String[0]));
        run("L04.07 KochSnowflakeCards · F11", () -> M07_KochSnowflakeCardsTest.main(new String[0]));
        run("L04.08 DragonCurve · F11", () -> M08_DragonCurveTest.main(new String[0]));
        run("L04.09 EvilHangman · F12", () -> M09_EvilHangmanTest.main(new String[0]));
        run("L04.10 WordleStyleWordHunt · F12", () -> M10_WordleStyleWordHuntTest.main(new String[0]));
        run("L04.11 Jotto · F12", () -> M11_JottoTest.main(new String[0]));
        run("L04.12 WordChain · F12", () -> M12_WordChainTest.main(new String[0]));
        run("L04.13 YatzyScorecard · F12", () -> M13_YatzyScorecardTest.main(new String[0]));
        IO.println("13 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
