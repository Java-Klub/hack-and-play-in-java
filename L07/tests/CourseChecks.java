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
        run("L07.01 CrazyEightsSimpleMauMau · F09", () -> M01_CrazyEightsSimpleMauMauTest.main(new String[0]));
        run("L07.02 CooperateOrDefect · F09", () -> M02_CooperateOrDefectTest.main(new String[0]));
        run("L07.03 TrailDuel · F09", () -> M03_TrailDuelTest.main(new String[0]));
        run("L07.04 CarrotInABox · F21", () -> M04_CarrotInABoxTest.main(new String[0]));
        run("L07.05 TargetPop · F21", () -> M05_TargetPopTest.main(new String[0]));
        run("L07.06 MiniPacman · F21", () -> M06_MiniPacmanTest.main(new String[0]));
        run("L07.07 GoFish · F25", () -> M07_GoFishTest.main(new String[0]));
        run("L07.08 MiniInvaders · F25", () -> M08_MiniInvadersTest.main(new String[0]));
        run("L07.09 CrossingRush · F25", () -> M09_CrossingRushTest.main(new String[0]));
        run("L07.10 SoftLanding · F25", () -> M10_SoftLandingTest.main(new String[0]));
        run("L07.11 TiltTray · F25", () -> M11_TiltTrayTest.main(new String[0]));
        run("L07.12 PlatformDash · F25", () -> M12_PlatformDashTest.main(new String[0]));
        IO.println("12 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
