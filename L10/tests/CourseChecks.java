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
        run("L10.01 AsteroidDrift · F28", () -> M01_AsteroidDriftTest.main(new String[0]));
        run("L10.02 FireworkPainter · F28", () -> M02_FireworkPainterTest.main(new String[0]));
        run("L10.03 ShadowPlayground · F28", () -> M03_ShadowPlaygroundTest.main(new String[0]));
        run("L10.04 Untangle · F29", () -> M04_UntangleTest.main(new String[0]));
        run("L10.05 SpringToy · F29", () -> M05_SpringToyTest.main(new String[0]));
        run("L10.06 MirrorArt · F29", () -> M06_MirrorArtTest.main(new String[0]));
        run("L10.07 Moire · F29", () -> M07_MoireTest.main(new String[0]));
        run("L10.08 StarTunnel · F29", () -> M08_StarTunnelTest.main(new String[0]));
        run("L10.09 Pong · F30", () -> M09_PongTest.main(new String[0]));
        run("L10.10 Breakout · F30", () -> M10_BreakoutTest.main(new String[0]));
        run("L10.11 OrbitPainter · F30", () -> M11_OrbitPainterTest.main(new String[0]));
        run("L10.12 PendulumWaves · F30", () -> M12_PendulumWavesTest.main(new String[0]));
        run("L10.13 RippleTank · F30", () -> M13_RippleTankTest.main(new String[0]));
        run("L10.14 Snake · F30", () -> M14_SnakeTest.main(new String[0]));
        IO.println("14 missions; " + failed + " still need repair.");
        if (failed > 0) {
            throw new AssertionError("Use Test File for the current mission; other red missions can wait.");
        }
    }
}
